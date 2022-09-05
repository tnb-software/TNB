package software.tnb.google.cloud.functions.validation;

import software.tnb.common.utils.WaitUtils;
import software.tnb.google.cloud.common.account.GoogleCloudAccount;
import software.tnb.google.storage.service.GoogleStorage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.functions.v1.CloudFunction;
import com.google.cloud.functions.v1.CloudFunctionName;
import com.google.cloud.functions.v1.CloudFunctionStatus;
import com.google.cloud.functions.v1.CloudFunctionsServiceClient;
import com.google.cloud.functions.v1.CreateFunctionRequest;
import com.google.cloud.functions.v1.DeleteFunctionRequest;
import com.google.cloud.functions.v1.HttpsTrigger;
import com.google.cloud.functions.v1.ListFunctionsRequest;
import com.google.cloud.functions.v1.LocationName;
import com.google.iam.v1.Binding;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GoogleFunctionsValidation {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleFunctionsValidation.class);

    private final GoogleCloudAccount account;
    private final CloudFunctionsServiceClient client;
    private final GoogleStorage storage;

    private final String defaultRegion = "us-central1";
    private final String bucketPrefix = "function-";

    // Save the function names, as the client needs CloudFunctionName (consists of project/location/name)
    // So that the user doesn't need to specify the region in get/delete methods for example
    // The CloudFunctionName is the key as it is always unique
    private final Map<String, String> functionNames;

    public GoogleFunctionsValidation(GoogleCloudAccount account, CloudFunctionsServiceClient client, GoogleStorage storage) {
        this.account = account;
        this.client = client;
        this.storage = storage;
        this.functionNames = new HashMap<>();
    }

    public void createFunction(String name, String region, String runtime, String entryPoint, Path zipSource) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("The function name must start with a letter followed by up to 62 letters, numbers, hyphens,"
                + " or underscores and must end with a letter or a number! Was: \"" + name + "\"");
        }
        storage.validation().createBucket(bucketPrefix + name.toLowerCase());
        storage.validation().uploadFile(bucketPrefix + name.toLowerCase(), zipSource);

        final String functionName = CloudFunctionName.format(account.projectId(), region, name);

        CloudFunction fx = CloudFunction.newBuilder()
            .setName(functionName)
            .setEntryPoint(entryPoint)
            .setRuntime(runtime)
            .setHttpsTrigger(HttpsTrigger.newBuilder().setSecurityLevel(HttpsTrigger.SecurityLevel.SECURE_OPTIONAL).build())
            .setSourceArchiveUrl(String.format("gs://%s/%s", bucketPrefix + name, zipSource.getFileName().toString()))
            .setServiceAccountEmail(account.clientEmail())
            .build();

        client.createFunctionCallable().call(CreateFunctionRequest.newBuilder()
            .setFunction(fx)
            .setLocation(LocationName.format(account.projectId(), region))
            .build());

        functionNames.put(functionName, name);

        client.setIamPolicy(SetIamPolicyRequest.newBuilder()
            .setResource(functionName)
            .setPolicy(Policy.newBuilder()
                .addBindings(Binding.newBuilder()
                    .setRole("roles/cloudfunctions.invoker")
                    .addMembers("allUsers")
                    .build())
                .build())
            .build());

        WaitUtils.waitFor(() -> CloudFunctionStatus.ACTIVE == getFunction(functionName).getStatus(), 60, 2000L,
            "Waiting until the function " + name + " is running");
    }

    public void createFunction(String name, String runtime, String entryPoint, Path zipSource) {
        createFunction(name, defaultRegion, runtime, entryPoint, zipSource);
    }

    public CloudFunction getFunction(String name) {
        return client.getFunction(getFunctionName(name));
    }

    public List<CloudFunction> listFunctions() {
        return listFunctions(defaultRegion);
    }

    public List<CloudFunction> listFunctions(String region) {
        List<CloudFunction> functions = new ArrayList<>();
        client.listFunctions(ListFunctionsRequest.newBuilder().setParent(LocationName.format(account.projectId(), region)).build()).iterateAll()
            .forEach(functions::add);
        return functions;
    }

    public String getUrl(String name) {
        return getFunction(getFunctionName(name)).getHttpsTrigger().getUrl();
    }

    public void deleteFunction(String functionName) {
        LOG.info("Deleting function {}", functionName);
        final String cloudFunctionName = getFunctionName(functionName);
        try {
            client.deleteFunctionCallable().call(DeleteFunctionRequest.newBuilder().setName(cloudFunctionName).build());
        } finally {
            storage.validation().deleteBucket(bucketPrefix + StringUtils.substringAfterLast(cloudFunctionName, "/").toLowerCase());
        }
    }

    private boolean isValidName(String name) {
        return (name.length() > 0 && name.length() <= 63) && name.matches("^[a-zA-Z].*") && name.matches(".*[a-zA-Z0-9]$");
    }

    private String getFunctionName(String name) {
        if (name.matches("projects/.*/locations/.*/functions/.*")) {
            return name;
        } else if (functionNames.containsValue(name)) {
            final List<Map.Entry<String, String>> entries =
                functionNames.entrySet().stream().filter(e -> name.equals(e.getValue())).collect(Collectors.toList());
            if (entries.size() > 1) {
                throw new IllegalArgumentException("Multiple functions with the same name exist (in different location)! Use either a full function"
                    + " name (projects/<project>/locations/<location>/functions/<name>) or use a method with region and function name parameters");
            } else {
                return entries.get(0).getKey();
            }
        } else {
            throw new IllegalArgumentException("No function with the name \"" + name + "\" was created in this execution!"
                + " If you want to use a previously created function, use either a full function name"
                + " (projects/<project>/locations/<location>/functions/<name>) or use a method with region and function name parameters");
        }
    }
}

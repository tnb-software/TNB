package org.jboss.fuse.tnb.lambda.validation;

import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.iam.service.IAM;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.InvalidParameterValueException;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;
import software.amazon.awssdk.services.lambda.model.Runtime;

public class LambdaValidation {
    private static final Logger LOG = LoggerFactory.getLogger(LambdaValidation.class);
    private static final String ROLE_NAME = "tnb-lambda-role";

    private final LambdaClient client;
    private final IAM iam;

    public LambdaValidation(LambdaClient client, IAM iam) {
        this.client = client;
        this.iam = iam;
    }

    public void createFunction(String name, Runtime runtime, String handler, SdkBytes zipFile) {
        LOG.info("Creating Lambda function with name {}", name);
        final String arn;
        if (iam.validation().roleExists(ROLE_NAME)) {
            arn = iam.validation().getRoleArn(ROLE_NAME).get();
        } else {
            try (InputStream is = this.getClass().getResourceAsStream("/role-policy.json")) {
                arn = iam.validation().createRole(
                    ROLE_NAME,
                    "Used in TNB lambda service, if deleted, it will be automatically recreated when running the tests again",
                    IOUtils.toString(is, Charset.defaultCharset())
                );
            } catch (IOException e) {
                throw new RuntimeException("Unable to load role-policy.json", e);
            }
        }

        // It takes some time until you can use the role (usually around ~10 seconds)
        int retries = 0;
        while (retries < 12) {
            try {
                client.createFunction(builder -> builder
                    .functionName(name)
                    .role(arn)
                    .runtime(runtime)
                    .handler(handler)
                    .publish(true)
                    .code(FunctionCode.builder()
                        .zipFile(zipFile)
                        .build())
                );
                break;
            } catch (InvalidParameterValueException ex) {
                if (ex.getMessage().contains("The role defined for the function cannot be assumed by Lambda")) {
                    LOG.trace("Role not ready yet, will be retried");
                    retries++;
                    WaitUtils.sleep(5000L);
                } else {
                    throw new RuntimeException("Unable to create lambda function: ", ex);
                }
            }
        }
    }

    public GetFunctionResponse getFunction(String name) {
        try {
            return client.getFunction(b -> b.functionName(name).build());
        } catch (ResourceNotFoundException ex) {
            return null;
        }
    }

    public InvokeResponse invokeFunction(String name, SdkBytes bytes) {
        return client.invoke(b -> b.functionName(name).payload(bytes));
    }

    public void deleteFunction(String name) {
        LOG.debug("Deleting Lambda function {}", name);
        try {
            client.deleteFunction(b -> b.functionName(name));
        } catch (ResourceNotFoundException ignored) {
        }
    }

    public List<FunctionConfiguration> listFunctions() {
        return client.listFunctions().functions();
    }

    public String getRoleArn() {
        return iam.validation().getRoleArn(ROLE_NAME).get();
    }
}

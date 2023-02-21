package software.tnb.hyperfoil.validation;

import software.tnb.hyperfoil.service.HyperfoilConfiguration;
import software.tnb.hyperfoil.validation.generated.ApiClient;
import software.tnb.hyperfoil.validation.generated.ApiException;
import software.tnb.hyperfoil.validation.generated.ApiResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.temporal.ChronoUnit;

import dev.failsafe.RetryPolicy;
import dev.failsafe.okhttp.FailsafeCall;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Version of apiClient supporting a retry policy for sync calls.
 * The async calls work whithout any retry policy.
 * 
 */
public class ApiClientWithRetryPolicy extends ApiClient {

    public ApiClientWithRetryPolicy() {
        super();
    }

    /**
     * Basic constructor with custom OkHttpClient
     *
     * @param client a {@link okhttp3.OkHttpClient} object
     */
    public ApiClientWithRetryPolicy(OkHttpClient client) {
        super(client);
    }

    /**
     * Execute HTTP call and deserialize the HTTP response body into the given return type.
     * A <b>retry policy</b> is applied for failures throwing <i>SocketException</i> or 
     * <i>SocketTimeoutException</i>. If you don't specify default values for the 
     * <i>retry policy</i>, default ones will be used. For custom values look to 
     * {@link software.tnb.hyperfoil.service.Configuration}
     * 
     * @see software.tnb.hyperfoil.service.Configuration
     * @param returnType The return type used to deserialize HTTP response body
     * @param <T> The return type corresponding to (same with) returnType
     * @param call Call
     * @return ApiResponse object containing response status, headers and
     * data, which is a Java object deserialized from response body and would be null
     * when returnType is null.
     * @throws ApiException If fail to execute the call
     */
    @Override
    public <T> ApiResponse<T> execute(Call call, Type returnType) throws ApiException {
        try {
            RetryPolicy<Response> retryPolicy = RetryPolicy.<Response>builder()
                    .handle(SocketException.class, SocketTimeoutException.class)
                    .withBackoff(HyperfoilConfiguration.retryBackoffDelayInSec(),
                            HyperfoilConfiguration.retryBackoffMaxDelayInSec(), ChronoUnit.SECONDS)
                    .withMaxRetries(HyperfoilConfiguration.retryNumberOfRetries()).build();
            FailsafeCall failsafeCall = FailsafeCall.with(retryPolicy).compose(call);
            Response response = failsafeCall.execute();
            T data = handleResponse(response, returnType);
            return new ApiResponse<T>(response.code(), response.headers().toMultimap(), data);
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }
    
}

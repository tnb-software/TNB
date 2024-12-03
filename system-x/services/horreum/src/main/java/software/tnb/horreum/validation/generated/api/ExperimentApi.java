/*
 * Horreum REST API
 * Horreum automated change anomaly detection. For more information, please see [https://horreum.hyperfoil.io/](https://horreum.hyperfoil.io/)
 *
 * The version of the OpenAPI document: 0.17
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package software.tnb.horreum.validation.generated.api;

import software.tnb.horreum.validation.generated.ApiCallback;
import software.tnb.horreum.validation.generated.ApiClient;
import software.tnb.horreum.validation.generated.ApiException;
import software.tnb.horreum.validation.generated.ApiResponse;
import software.tnb.horreum.validation.generated.Configuration;
import software.tnb.horreum.validation.generated.Pair;

import com.google.gson.reflect.TypeToken;


import software.tnb.horreum.validation.generated.model.ConditionConfig;
import software.tnb.horreum.validation.generated.model.ExperimentProfile;
import software.tnb.horreum.validation.generated.model.ExperimentResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentApi {
    private ApiClient localVarApiClient;
    private int localHostIndex;
    private String localCustomBaseUrl;

    public ExperimentApi() {
        this(Configuration.getDefaultApiClient());
    }

    public ExperimentApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public int getHostIndex() {
        return localHostIndex;
    }

    public void setHostIndex(int hostIndex) {
        this.localHostIndex = hostIndex;
    }

    public String getCustomBaseUrl() {
        return localCustomBaseUrl;
    }

    public void setCustomBaseUrl(String customBaseUrl) {
        this.localCustomBaseUrl = customBaseUrl;
    }

    /**
     * Build call for addOrUpdateProfile
     * @param testId Test ID to retrieve Experiment Profiles for (required)
     * @param experimentProfile  (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call addOrUpdateProfileCall(Integer testId, ExperimentProfile experimentProfile, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = experimentProfile;

        // create path and map variables
        String localVarPath = "/api/experiment/{testId}/profiles"
            .replace("{" + "testId" + "}", localVarApiClient.escapeString(testId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call addOrUpdateProfileValidateBeforeCall(Integer testId, ExperimentProfile experimentProfile, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'testId' is set
        if (testId == null) {
            throw new ApiException("Missing the required parameter 'testId' when calling addOrUpdateProfile(Async)");
        }

        // verify the required parameter 'experimentProfile' is set
        if (experimentProfile == null) {
            throw new ApiException("Missing the required parameter 'experimentProfile' when calling addOrUpdateProfile(Async)");
        }

        return addOrUpdateProfileCall(testId, experimentProfile, _callback);

    }

    /**
     * 
     * Save new or update existing Experiment Profiles for a Test 
     * @param testId Test ID to retrieve Experiment Profiles for (required)
     * @param experimentProfile  (required)
     * @return Integer
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Integer addOrUpdateProfile(Integer testId, ExperimentProfile experimentProfile) throws ApiException {
        ApiResponse<Integer> localVarResp = addOrUpdateProfileWithHttpInfo(testId, experimentProfile);
        return localVarResp.getData();
    }

    /**
     * 
     * Save new or update existing Experiment Profiles for a Test 
     * @param testId Test ID to retrieve Experiment Profiles for (required)
     * @param experimentProfile  (required)
     * @return ApiResponse&lt;Integer&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Integer> addOrUpdateProfileWithHttpInfo(Integer testId, ExperimentProfile experimentProfile) throws ApiException {
        okhttp3.Call localVarCall = addOrUpdateProfileValidateBeforeCall(testId, experimentProfile, null);
        Type localVarReturnType = new TypeToken<Integer>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Save new or update existing Experiment Profiles for a Test 
     * @param testId Test ID to retrieve Experiment Profiles for (required)
     * @param experimentProfile  (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call addOrUpdateProfileAsync(Integer testId, ExperimentProfile experimentProfile, final ApiCallback<Integer> _callback) throws ApiException {

        okhttp3.Call localVarCall = addOrUpdateProfileValidateBeforeCall(testId, experimentProfile, _callback);
        Type localVarReturnType = new TypeToken<Integer>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for deleteProfile
     * @param testId Test ID (required)
     * @param profileId Experiment Profile ID (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 204 </td><td> No Content </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deleteProfileCall(Integer testId, Integer profileId, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/experiment/{testId}/profiles/{profileId}"
            .replace("{" + "testId" + "}", localVarApiClient.escapeString(testId.toString()))
            .replace("{" + "profileId" + "}", localVarApiClient.escapeString(profileId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(basePath, localVarPath, "DELETE", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call deleteProfileValidateBeforeCall(Integer testId, Integer profileId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'testId' is set
        if (testId == null) {
            throw new ApiException("Missing the required parameter 'testId' when calling deleteProfile(Async)");
        }

        // verify the required parameter 'profileId' is set
        if (profileId == null) {
            throw new ApiException("Missing the required parameter 'profileId' when calling deleteProfile(Async)");
        }

        return deleteProfileCall(testId, profileId, _callback);

    }

    /**
     * 
     * Delete an Experiment Profiles for a Test
     * @param testId Test ID (required)
     * @param profileId Experiment Profile ID (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 204 </td><td> No Content </td><td>  -  </td></tr>
     </table>
     */
    public void deleteProfile(Integer testId, Integer profileId) throws ApiException {
        deleteProfileWithHttpInfo(testId, profileId);
    }

    /**
     * 
     * Delete an Experiment Profiles for a Test
     * @param testId Test ID (required)
     * @param profileId Experiment Profile ID (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 204 </td><td> No Content </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Void> deleteProfileWithHttpInfo(Integer testId, Integer profileId) throws ApiException {
        okhttp3.Call localVarCall = deleteProfileValidateBeforeCall(testId, profileId, null);
        return localVarApiClient.execute(localVarCall);
    }

    /**
     *  (asynchronously)
     * Delete an Experiment Profiles for a Test
     * @param testId Test ID (required)
     * @param profileId Experiment Profile ID (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 204 </td><td> No Content </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deleteProfileAsync(Integer testId, Integer profileId, final ApiCallback<Void> _callback) throws ApiException {

        okhttp3.Call localVarCall = deleteProfileValidateBeforeCall(testId, profileId, _callback);
        localVarApiClient.executeAsync(localVarCall, _callback);
        return localVarCall;
    }
    /**
     * Build call for models
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call modelsCall(final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/experiment/models";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call modelsValidateBeforeCall(final ApiCallback _callback) throws ApiException {
        return modelsCall(_callback);

    }

    /**
     * 
     * Retrieve a list of Condition Config models
     * @return List&lt;ConditionConfig&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ConditionConfig> models() throws ApiException {
        ApiResponse<List<ConditionConfig>> localVarResp = modelsWithHttpInfo();
        return localVarResp.getData();
    }

    /**
     * 
     * Retrieve a list of Condition Config models
     * @return ApiResponse&lt;List&lt;ConditionConfig&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ConditionConfig>> modelsWithHttpInfo() throws ApiException {
        okhttp3.Call localVarCall = modelsValidateBeforeCall(null);
        Type localVarReturnType = new TypeToken<List<ConditionConfig>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Retrieve a list of Condition Config models
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call modelsAsync(final ApiCallback<List<ConditionConfig>> _callback) throws ApiException {

        okhttp3.Call localVarCall = modelsValidateBeforeCall(_callback);
        Type localVarReturnType = new TypeToken<List<ConditionConfig>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for profiles
     * @param testId Test ID to retrieve Experiment Profiles for (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call profilesCall(Integer testId, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/experiment/{testId}/profiles"
            .replace("{" + "testId" + "}", localVarApiClient.escapeString(testId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call profilesValidateBeforeCall(Integer testId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'testId' is set
        if (testId == null) {
            throw new ApiException("Missing the required parameter 'testId' when calling profiles(Async)");
        }

        return profilesCall(testId, _callback);

    }

    /**
     * 
     * Retrieve Experiment Profiles by Test ID
     * @param testId Test ID to retrieve Experiment Profiles for (required)
     * @return List&lt;ExperimentProfile&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ExperimentProfile> profiles(Integer testId) throws ApiException {
        ApiResponse<List<ExperimentProfile>> localVarResp = profilesWithHttpInfo(testId);
        return localVarResp.getData();
    }

    /**
     * 
     * Retrieve Experiment Profiles by Test ID
     * @param testId Test ID to retrieve Experiment Profiles for (required)
     * @return ApiResponse&lt;List&lt;ExperimentProfile&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ExperimentProfile>> profilesWithHttpInfo(Integer testId) throws ApiException {
        okhttp3.Call localVarCall = profilesValidateBeforeCall(testId, null);
        Type localVarReturnType = new TypeToken<List<ExperimentProfile>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Retrieve Experiment Profiles by Test ID
     * @param testId Test ID to retrieve Experiment Profiles for (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call profilesAsync(Integer testId, final ApiCallback<List<ExperimentProfile>> _callback) throws ApiException {

        okhttp3.Call localVarCall = profilesValidateBeforeCall(testId, _callback);
        Type localVarReturnType = new TypeToken<List<ExperimentProfile>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for runExperiments
     * @param datasetId The dataset to run the experiment on (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> Array of experiment results </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call runExperimentsCall(Integer datasetId, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/experiment/run";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (datasetId != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("datasetId", datasetId));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call runExperimentsValidateBeforeCall(Integer datasetId, final ApiCallback _callback) throws ApiException {
        return runExperimentsCall(datasetId, _callback);

    }

    /**
     * 
     * Run an experiment for a given dataset and experiment profile
     * @param datasetId The dataset to run the experiment on (optional)
     * @return List&lt;ExperimentResult&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> Array of experiment results </td><td>  -  </td></tr>
     </table>
     */
    public List<ExperimentResult> runExperiments(Integer datasetId) throws ApiException {
        ApiResponse<List<ExperimentResult>> localVarResp = runExperimentsWithHttpInfo(datasetId);
        return localVarResp.getData();
    }

    /**
     * 
     * Run an experiment for a given dataset and experiment profile
     * @param datasetId The dataset to run the experiment on (optional)
     * @return ApiResponse&lt;List&lt;ExperimentResult&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> Array of experiment results </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ExperimentResult>> runExperimentsWithHttpInfo(Integer datasetId) throws ApiException {
        okhttp3.Call localVarCall = runExperimentsValidateBeforeCall(datasetId, null);
        Type localVarReturnType = new TypeToken<List<ExperimentResult>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Run an experiment for a given dataset and experiment profile
     * @param datasetId The dataset to run the experiment on (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> Array of experiment results </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call runExperimentsAsync(Integer datasetId, final ApiCallback<List<ExperimentResult>> _callback) throws ApiException {

        okhttp3.Call localVarCall = runExperimentsValidateBeforeCall(datasetId, _callback);
        Type localVarReturnType = new TypeToken<List<ExperimentResult>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}

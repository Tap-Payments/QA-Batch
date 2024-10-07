package com.tappayments.automation.qabatch.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tappayments.automation.qabatch.config.ConfigManager;
import com.tappayments.automation.qabatch.requests.BatchSystemRequest;
import com.tappayments.automation.qabatch.stress.BatchUploader;
import com.tappayments.automation.utils.CommonAutomationUtils;
import io.restassured.response.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AppUtils {

    public static Map<String, Object> batchRequestBody(){

        Map<String, Object> batchRequestBody = new HashMap<>();
        batchRequestBody.put("file_name", "batch_invoice_" + UUID.randomUUID().toString());
        batchRequestBody.put("file_type", "invoice");

        Map<String, String> user = new HashMap<>();
        user.put("id", "001");
        user.put("segment", "bus_sZUaS34231124CWYs26cS1O521");
        user.put("brand", "brd_hFUaS34231124yZqX26JE1u521");
        user.put("entity", "ent_RcErA8231416erYY8Nk2g89");

        batchRequestBody.put("user", user);
        return batchRequestBody;
    }

    public static Map<String, Object> getListRequestBody() {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("page", 0);
        requestBody.put("limit", 50);

        Map<String, Object> date = new HashMap<>();
        Map<String, Object> period = new HashMap<>();
        period.put("date", date);

        requestBody.put("period", period);
        requestBody.put("users", List.of("001"));
        requestBody.put("batches", List.of());

        Map<String, List<String>> filters = new HashMap<>();
        filters.put("status", List.of());

        requestBody.put("filters", filters);

        return requestBody;
    }


    public static String batchRequestBodyWithLongFileName(){

        return UUID.randomUUID().toString().repeat(3);
    }

    public static void validatePresignedUrlResponse(Response response, Map<String, Object> batchRequestBody) {

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        CommonAutomationUtils.verifyExactMatch(response,
                Map.of(
                        AppConstants.STATUS, AppConstants.LINK_GENERATED,
                        AppConstants.FILE_NAME, batchRequestBody.get(AppConstants.FILE_NAME),
                        AppConstants.FILE_TYPE, batchRequestBody.get(AppConstants.FILE_TYPE).toString().toLowerCase()
                )
        );
        CommonAutomationUtils.verifyNonEmpty(response, List.of(AppConstants.ID, AppConstants.PRE_SIGNED_URL));
    }

    public static void validateBatchStatusResponse(Response response, List<String> batchResponse, String expectedStatus) {

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        CommonAutomationUtils.verifyExactMatch(response,
                Map.of(
                        AppConstants.ID, batchResponse.get(0),
                        AppConstants.FILE_NAME, batchResponse.get(1),
                        AppConstants.FILE_TYPE, batchResponse.get(2).toLowerCase(),
                        AppConstants.STATUS, expectedStatus
                )
        );
        CommonAutomationUtils.verifyNonEmpty(response, List.of(AppConstants.CREATED_AT, AppConstants.UPDATED_AT));
    }

    public static void validateBatchFileResponse(Response response, List<String> batchResponse, String status) {

        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Object> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");
        CommonAutomationUtils.verifyExactMatch(response,
                Map.of(
                        AppConstants.FILES + "[0]." + AppConstants.STATUS, status,
                        AppConstants.FILES + "[0]." + AppConstants.ID, batchResponse.get(0),
                        AppConstants.FILES + "[0]." + AppConstants.USER + "." + AppConstants.ID, batchResponse.get(1),
                        AppConstants.FILES + "[0]." + AppConstants.FILE_NAME, batchResponse.get(2),
                        AppConstants.FILES + "[0]." + AppConstants.FILE_TYPE, batchResponse.get(3)
                )
        );
    }

    public static Response getBatchResponse() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        return BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
    }

    public static Response getBatchResponseDifferentUser(String userId) {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.USER, Map.of("id", userId));
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        return BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
    }

    public static String generatePresignedUrl(String fileUploadUrl) throws IOException {
        // Create the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(AppConstants.FILE_NAME, UUID.randomUUID());
        requestBody.put(AppConstants.FILE_TYPE, AppConstants.INVOICE);

        Map<String, String> user = new HashMap<>();
        user.put(AppConstants.ID, "001");
        user.put(AppConstants.SEGMENT, "bus_sZUaS34231124CWYs26cS1O521");
        user.put(AppConstants.BRAND, "brd_hFUaS34231124yZqX26JE1u521");
        user.put(AppConstants.ENTITY, "ent_RcErA8231416erYY8Nk2g89");
        requestBody.put(AppConstants.USER, user);

        // Convert request body to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestBody);

        // Make HTTP POST request to generate presigned URL
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(fileUploadUrl);
            post.setHeader(AppConstants.AUTHORIZATION, AppConstants.BEARER + ConfigManager.getPropertyValue(AppConstants.AUTHORIZATION_VALUE));
            post.setHeader(AppConstants.CONTENT_TYPE, ConfigManager.getPropertyValue(AppConstants.CONTENT_TYPE_VALUE));
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);

                    // Extract the pre_signed_url from the response
                    Map<String, Object> responseMap = objectMapper.readValue(result, Map.class);

                    System.out.println("=======================================");
                    System.out.println("Batch id:"+responseMap.get("id"));
                    System.out.println("File name:"+responseMap.get("file_name"));
                    System.out.println("File type:"+responseMap.get("file_type"));
                    System.out.println("=======================================");

                    return (String) responseMap.get("pre_signed_url");
                }
            }
        }
        return null;
    }

    public static void uploadFile(String preSignedUrl, File file) throws IOException {
        // Load file from the resources directory
        ClassLoader classLoader = BatchUploader.class.getClassLoader();

        // Make a PUT request to upload the file as binary
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut uploadFile = new HttpPut(preSignedUrl);  // S3 generally requires PUT for presigned URLs

            // Send the file as raw binary
            FileEntity fileEntity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);
            uploadFile.setEntity(fileEntity);

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String result = EntityUtils.toString(responseEntity);
                    System.out.println("File upload response successfully.");
                }
            }
        }
    }

    public static File getFileUploaded(String preSignedUrl, String fileName){

        try {
            ClassLoader classLoader = BatchUploader.class.getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            AppUtils.uploadFile(preSignedUrl, file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void validateBatchStatusFileUploadResponse(List<String> batchParameters, Map<String, Object> batchResponse, String endpoint) {

        String batchStatus = "";
        int maxRetries = 10;
        int attempt = 0;

        while (batchStatus != null && !batchStatus.equals(AppConstants.COMPLETED) && attempt++ < maxRetries) {

            Response response = getBatchStatusResponse(endpoint);
            batchStatus = response.jsonPath().getString(AppConstants.STATUS);
            System.out.println(response.prettyPrint());
            if(batchStatus != null && batchStatus.equals(AppConstants.COMPLETED)){

                AppUtils.validateBatchStatusResponse(response, batchParameters, AppConstants.COMPLETED);
                CommonAutomationUtils.verifyExactMatch(response, batchResponse);
            } else {

                int delay = Integer.parseInt(ConfigManager.getPropertyValue(AppConstants.DELAY_AFTER_UPLOAD_FILE));

                System.out.println("Processing... retrying in " + delay + " ms.");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void validateFileBatchStatusFileUploadResponse(List<String> batchParameters, Map<String, Object> batchResponse) {

        String batchStatus = "";
        int maxRetries = 10;
        int attempt = 0;

        while (batchStatus != null && !batchStatus.equals(AppConstants.COMPLETED) && attempt++ < maxRetries) {

            Response response = getBatchFileStatusResponse(batchParameters);
            batchStatus = response.jsonPath().getString(AppConstants.FILES + "[0]." + AppConstants.STATUS);

            if(batchStatus != null && batchStatus.equals(AppConstants.COMPLETED)){

                AppUtils.validateBatchFileResponse(response, batchParameters, AppConstants.COMPLETED);
                CommonAutomationUtils.verifyExactMatch(response, batchResponse);
            } else {

                int delay = Integer.parseInt(ConfigManager.getPropertyValue(AppConstants.DELAY_AFTER_UPLOAD_FILE));

                System.out.println("Processing... retrying in " + delay + " ms.");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Response getBatchStatusResponse(String endpoint) {

        return BatchSystemRequest.getRequest(endpoint);
    }

    public static Response getBatchFileStatusResponse(List<String> batchParameters){

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.BATCHES, List.of(batchParameters.get(0)));
        listRequestBody.put(AppConstants.USERS, List.of(batchParameters.get(1)));
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        return BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
    }

    public static int getFileRowCount(File file) {

        try (var lines = Files.lines(file.toPath())) {
            return (int) lines.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getBatchStatusParameters() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        String preSignedUrl = batchResponse.jsonPath().getString(AppConstants.PRE_SIGNED_URL);

        return List.of(batchId, fileName, fileType, preSignedUrl);
    }

    public static List<String> getFileBatchStatusParameters() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        String preSignedUrl = batchResponse.jsonPath().getString(AppConstants.PRE_SIGNED_URL);

        return List.of(batchId, userId, fileName, fileType, preSignedUrl);
    }

    public static Response getFileBatchStatusResponse(List<String> fileBatchStatusParameters) {

        File file = AppUtils.getFileUploaded(fileBatchStatusParameters.get(4), ConfigManager.getPropertyValue(AppConstants.UPLOAD_FILE_NAME));
        int fileRows = AppUtils.getFileRowCount(file)-1;

        AppUtils.validateFileBatchStatusFileUploadResponse(
                List.of(
                        fileBatchStatusParameters.get(0),
                        fileBatchStatusParameters.get(1),
                        fileBatchStatusParameters.get(2),
                        fileBatchStatusParameters.get(3)
                ),
                Map.of(AppConstants.FILES + "[0]." + AppConstants.TOTAL_RECORDS, fileRows)
        );
        return AppUtils.getBatchFileStatusResponse(List.of(fileBatchStatusParameters.get(0), fileBatchStatusParameters.get(1)));
    }

    public static Response getBatchStatusResponse(List<String> fileBatchStatusParameters, String endpoint) {

        File file = AppUtils.getFileUploaded(fileBatchStatusParameters.get(3), ConfigManager.getPropertyValue(AppConstants.UPLOAD_FILE_NAME));
        int fileRows = AppUtils.getFileRowCount(file)-1;
        String finalEndpoint = "/" + AppConstants.FILE + "/" + fileBatchStatusParameters.get(0) + "/" + endpoint;

        AppUtils.validateBatchStatusFileUploadResponse(fileBatchStatusParameters, Map.of(AppConstants.TOTAL_RECORDS, fileRows), finalEndpoint);

        return AppUtils.getBatchStatusResponse(finalEndpoint);
    }

    //------------------------------Common test cases...

    public static void getValidBatchNumberNoDownloadFileTestCase(String endpoint) {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        Response response = BatchSystemRequest.getRequest("/" + AppConstants.FILE + "/" + batchId + "/" + endpoint);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_NOT_FOUND,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.NO_FOUND_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.NO_FOUND_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.NO_FOUND_BATCH_STATUS_ERROR_DESCRIPTION[1]
                )
        );
    }

    public static void getInvalidBatchNumberNoDownloadFileTestCase(String endpoint) {

        Response response = BatchSystemRequest.getRequest("/" + AppConstants.FILE + "/invalid_batch_id/" + endpoint);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_NOT_FOUND,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.NO_FOUND_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.NO_FOUND_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.NO_FOUND_BATCH_STATUS_ERROR_DESCRIPTION[2]
                )
        );
    }

    public static void getMissingBatchNumberTestCase(String endpoint) {

        Response response = BatchSystemRequest.getRequest("/" + AppConstants.FILE + "/" + endpoint);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_NOT_FOUND,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.REQUEST_NOT_FOUND_DATA_CODE,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.REQUEST_NOT_FOUND_ERROR_DESCRIPTION[0]
                )
        );
    }

    public static void getIncorrectFormatBatchNumberTestCase(String endpoint) {

        Response response = BatchSystemRequest.getRequest("/" + AppConstants.FILE + "/batch_abc/" + endpoint);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_NOT_FOUND,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.NO_FOUND_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.NO_FOUND_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.NO_FOUND_BATCH_STATUS_ERROR_DESCRIPTION[2]
                )
        );
    }

    public static void getBatchNumberDifferentValidKeyTestCase(String endpoint) {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String preSignedUrl = batchResponse.jsonPath().getString(AppConstants.PRE_SIGNED_URL);
        AppUtils.getFileUploaded(preSignedUrl, ConfigManager.getPropertyValue(AppConstants.UPLOAD_FILE_NAME));

        Response response = BatchSystemRequest.getRequest(
                "/" + AppConstants.FILE + "/" + batchId + "/" + endpoint,
                Map.of(
                        AppConstants.CONTENT_TYPE, ConfigManager.getPropertyValue(AppConstants.CONTENT_TYPE_VALUE),
                        AppConstants.AUTHORIZATION, AppConstants.BEARER + ConfigManager.getPropertyValue(AppConstants.AUTHORIZATION_VALUE1)
                )
        );

        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_UNAUTHORIZED,
                Map.of(
                        AppConstants.ERRORS_CODE, HttpStatus.SC_UNAUTHORIZED,
                        AppConstants.ERRORS_ERROR, AppConstants.UNAUTHORIZED_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.UNAUTHORIZED_ERROR_DESCRIPTION[0]
                )
        );
    }

    public static void getValidTotalRecordsTestCase(String endpoint) {

        List<String> fileBatchStatusParameters = AppUtils.getBatchStatusParameters();
        AppUtils.getBatchStatusResponse(fileBatchStatusParameters, endpoint);
    }

    public static void getSumOfRecordsTestCase(String endpoint) {

        List<String> fileBatchStatusParameters = AppUtils.getBatchStatusParameters();
        Response batchStatusResponse = AppUtils.getBatchStatusResponse(fileBatchStatusParameters, endpoint);

        int successRecords = batchStatusResponse.jsonPath().getInt(AppConstants.SUCCESS_RECORDS);
        int validationFailedRecords = batchStatusResponse.jsonPath().getInt(AppConstants.VALIDATION_FAILED_RECORDS);
        int writerFailedRecords = batchStatusResponse.jsonPath().getInt(AppConstants.WRITER_FAILED_RECORDS);
        int totalRecords = batchStatusResponse.jsonPath().getInt(AppConstants.TOTAL_RECORDS);

        Assert.assertEquals(successRecords + validationFailedRecords + writerFailedRecords, totalRecords, "Sum of records is not equal to total records");
    }

    public static void getNegativeTotalRecordsTestCase(String endpoint) {

        List<String> fileBatchStatusParameters = AppUtils.getBatchStatusParameters();
        Response batchStatusResponse = AppUtils.getBatchStatusResponse(fileBatchStatusParameters, endpoint);
        Assert.assertTrue(batchStatusResponse.jsonPath().getInt(AppConstants.TOTAL_RECORDS) > 0, "Total records is not greater than 0");
    }

    public static void getTimestampValidationTestCase(String endpoint) {

        List<String> fileBatchStatusParameters = AppUtils.getBatchStatusParameters();
        Response batchStatusResponse = AppUtils.getBatchStatusResponse(fileBatchStatusParameters, endpoint);

        long createdAt = batchStatusResponse.jsonPath().getLong(AppConstants.CREATED_AT);
        long updatedAt = batchStatusResponse.jsonPath().getLong(AppConstants.UPDATED_AT);
        long jobStartedAt = batchStatusResponse.jsonPath().getLong(AppConstants.JOB_STARTED_AT);
        long jobFinishedAt = batchStatusResponse.jsonPath().getLong(AppConstants.JOB_FINISHED_AT);

        Assert.assertTrue(createdAt < jobStartedAt, "created_at should be before job_started_at");
        Assert.assertTrue(jobStartedAt < jobFinishedAt, "job_started_at should be before job_finished_at");
        Assert.assertEquals(jobFinishedAt, updatedAt, "job_finished_at shouldn't equals updated_at");
    }

    public static void getNonNegativeRecordsTestCase(String endpoint) {

        List<String> fileBatchStatusParameters = AppUtils.getBatchStatusParameters();
        Response batchStatusResponse = AppUtils.getBatchStatusResponse(fileBatchStatusParameters, endpoint);

        int totalRecords = batchStatusResponse.jsonPath().getInt(AppConstants.TOTAL_RECORDS);
        int successRecords = batchStatusResponse.jsonPath().getInt(AppConstants.SUCCESS_RECORDS);
        int validationFailedRecords = batchStatusResponse.jsonPath().getInt(AppConstants.VALIDATION_FAILED_RECORDS);
        int writerFailedRecords = batchStatusResponse.jsonPath().getInt(AppConstants.WRITER_FAILED_RECORDS);

        Assert.assertTrue(totalRecords >= 0, "Total records is negative");
        Assert.assertTrue(successRecords >= 0, "Success records is negative");
        Assert.assertTrue(validationFailedRecords >= 0, "Validation failed records is negative");
        Assert.assertTrue(writerFailedRecords >= 0, "Writer failed records is negative");
    }

    public static void getNonNegativeTimestampsTestCase(String endpoint) {

        List<String> fileBatchStatusParameters = AppUtils.getBatchStatusParameters();
        Response batchStatusResponse = AppUtils.getBatchStatusResponse(fileBatchStatusParameters, endpoint);

        long createdAt = batchStatusResponse.jsonPath().getLong(AppConstants.CREATED_AT);
        long updatedAt = batchStatusResponse.jsonPath().getLong(AppConstants.UPDATED_AT);
        long jobStartedAt = batchStatusResponse.jsonPath().getLong(AppConstants.JOB_STARTED_AT);
        long jobFinishedAt = batchStatusResponse.jsonPath().getLong(AppConstants.JOB_FINISHED_AT);

        Assert.assertTrue(createdAt >= 0, "created_at is negative");
        Assert.assertTrue(updatedAt >= 0, "updated_at is negative");
        Assert.assertTrue(jobStartedAt >= 0, "job_started_at is negative");
        Assert.assertTrue(jobFinishedAt >= 0, "job_finished_at is negative");
    }
}

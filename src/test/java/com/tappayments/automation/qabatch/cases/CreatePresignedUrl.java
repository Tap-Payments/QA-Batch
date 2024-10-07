package com.tappayments.automation.qabatch.cases;

import com.tappayments.automation.qabatch.base.BaseTest;
import com.tappayments.automation.qabatch.requests.BatchSystemRequest;
import com.tappayments.automation.qabatch.utils.AppConstants;
import com.tappayments.automation.qabatch.utils.AppUtils;
import com.tappayments.automation.utils.CommonAutomationUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import java.util.Map;
import java.util.UUID;

public class CreatePresignedUrl extends BaseTest {

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Name Validation",
                    "SC:Create File Name"
            },
            description = "This test verifies the creation of a presigned URL using a valid file name with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 1
    )
    public void createValidFileNameTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        System.out.println("--------------------");
        System.out.println(response.prettyPrint());
        AppUtils.validatePresignedUrlResponse(response, batchRequestBody);
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Name Validation",
                    "SC:Create File Name"
            },
            description = "This test verifies the creation of a presigned URL using a empty file name with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 2
    )
    public void createEmptyFileNameTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_NAME, "");
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_DATA_FILE_NAME_ERROR_DESCRIPTION[0]
                )
        );
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Name Validation",
                    "SC:Create File Name"
            },
            description = "This test verifies the creation of a presigned URL using a file name with more than 100 characters with the required batch data and validates that the response contains a correct error message and status code.",
            priority = 3
    )
    public void createFileNameExceedsCharacterLimitTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_NAME, "batch_invoice_" + AppUtils.batchRequestBodyWithLongFileName());
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_DATA_FILE_NAME_ERROR_DESCRIPTION[0]
                )
        );
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Name Validation",
                    "SC:Create File Name"
            },
            description = "This test verifies the creation of a presigned URL using a null file name with the required batch data and validates that the response contains a correct error message and status code.",
            priority = 4
    )
    public void createNullFileNameTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_NAME, null);
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_DATA_FILE_NAME_ERROR_DESCRIPTION[1]
                )
        );
    }

    // case for file name with spaces
    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Name Validation",
                    "SC:Create File Name"
            },
            description = "This test verifies the creation of a presigned URL using a file name with spaces with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 5
    )
    public void createFileNameWithSpacesTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_NAME, "batch invoice " + UUID.randomUUID().toString());
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        AppUtils.validatePresignedUrlResponse(response, batchRequestBody);
    }


    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Type Validation",
                    "SC:Create File Type"
            },
            description = "This test verifies the creation of a presigned URL using a valid file type with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 6
    )
    public void createValidFileNameWithFileTypeTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_TYPE, "INVOICE");
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        AppUtils.validatePresignedUrlResponse(response, batchRequestBody);
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Type Validation",
                    "SC:Create File Type"
            },
            description = "This test verifies the creation of a presigned URL using a valid small file type with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 7
    )
    public void createValidFileNameWithFileTypeInSmallLettersTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_TYPE, "invoice");
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        AppUtils.validatePresignedUrlResponse(response, batchRequestBody);
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Type Validation",
                    "SC:Create File Type"
            },
            description = "This test verifies the creation of a presigned URL using a valid mixed file type with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 8
    )
    public void createValidFileNameWithFileTypeInMixedLettersTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_TYPE, "InVoIcE");
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        AppUtils.validatePresignedUrlResponse(response, batchRequestBody);
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Type Validation",
                    "SC:Create File Type"
            },
            description = "This test verifies the creation of a presigned URL using a invalid file type with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 9
    )
    public void createValidFileNameWithFileTypeInSpecialCharactersTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_TYPE, "invalid_file_type");
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_DATA_FILE_TYPE_ERROR_DESCRIPTION[0]
                )
        );
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Type Validation",
                    "SC:Create File Type"
            },
            description = "This test verifies the creation of a presigned URL using a numeric file type with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 10
    )
    public void createValidFileNameWithFileTypeInNumbersTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_TYPE, "1234567890");
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_DATA_FILE_TYPE_ERROR_DESCRIPTION[0]
                )
        );
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Type Validation",
                    "SC:Create File Type"
            },
            description = "This test verifies the creation of a presigned URL using a empty file type with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 11
    )
    public void createValidFileNameWithFileTypeEmptyStringTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_TYPE, "");
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_DATA_FILE_TYPE_ERROR_DESCRIPTION[0]
                )
        );
    }

    @Test(
            groups = {
                    "MC:Create Presigned Batch URL",
                    "Abdul Rehman",
                    "SECTION:File Type Validation",
                    "SC:Create File Type"
            },
            description = "This test verifies the creation of a presigned URL using a null file type with the required batch data and validates that the response contains a correct presigned URL.",
            priority = 12
    )
    public void createValidFileNameWithFileTypeNullTestCase() {

        Map<String, Object> batchRequestBody = AppUtils.batchRequestBody();
        batchRequestBody.put(AppConstants.FILE_TYPE, null);
        String requestBody = CommonAutomationUtils.stringToJson(batchRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.PRESIGNED_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_DATA_FILE_TYPE_ERROR_DESCRIPTION[1]
                )
        );
    }
}
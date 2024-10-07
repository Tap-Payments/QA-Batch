package com.tappayments.automation.qabatch.cases;

import com.tappayments.automation.qabatch.App;
import com.tappayments.automation.qabatch.base.BaseTest;
import com.tappayments.automation.qabatch.config.ConfigManager;
import com.tappayments.automation.qabatch.requests.BatchSystemRequest;
import com.tappayments.automation.qabatch.stress.BatchUploader;
import com.tappayments.automation.qabatch.utils.AppConstants;
import com.tappayments.automation.qabatch.utils.AppUtils;
import com.tappayments.automation.utils.CommonAutomationUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GetBatchStatus extends BaseTest {

    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when a valid batch number is used to request the batch status.",
            priority = 1
    )
    public void getValidBatchNumberTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Response response = BatchSystemRequest.getRequest("/" + AppConstants.FILE + "/" + batchId + "/" + AppConstants.STATUS);
        AppUtils.validateBatchStatusResponse(response, List.of(batchId, fileName, fileType), AppConstants.LINK_GENERATED);
    }

    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when an invalid batch number is used.",
            priority = 2
    )
    public void getInvalidBatchNumberTestCase() {

        Response response = BatchSystemRequest.getRequest("/" + AppConstants.FILE + "/invalid_batch_id/" + AppConstants.STATUS);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_NOT_FOUND,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.NO_FOUND_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.NO_FOUND_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.NO_FOUND_BATCH_STATUS_ERROR_DESCRIPTION[0]
                )
        );
    }

    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when the batch number is missing from the request.",
            priority = 3
    )
    public void getMissingBatchNumberTestCase() {

        Response response = BatchSystemRequest.getRequest("/" + AppConstants.FILE + "/" + AppConstants.STATUS);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_NOT_FOUND,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.REQUEST_NOT_FOUND_DATA_CODE,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.REQUEST_NOT_FOUND_ERROR_DESCRIPTION[0]
                )
        );
    }

    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when a batch number with an incorrect format is provided.",
            priority = 4
    )
    public void getIncorrectFormatBatchNumberTestCase() {

        Response response = BatchSystemRequest.getRequest("/" + AppConstants.FILE + "/batch_abc/" + AppConstants.STATUS);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_NOT_FOUND,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.NO_FOUND_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.NO_FOUND_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.NO_FOUND_BATCH_STATUS_ERROR_DESCRIPTION[0]
                )
        );
    }

    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when attempting to retrieve the batch status using a different valid key but without proper authorization.",
            priority = 5
    )
    public void getBatchNumberDifferentValidKeyTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);

        Response response = BatchSystemRequest.getRequest(
                "/" + AppConstants.FILE + "/" + batchId + "/" + AppConstants.STATUS,
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

    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test validates the total records in the batch upload process. It retrieves the batch details from the response, including batch ID, file name, file type, and pre-signed URL," +
                    " and uses these details to fetch the uploaded file. Finally, the test verifies that the batch upload response contains the correct information and the total number of records matches " +
                    "the expected value.",
            priority = 6
    )
    public void getValidTotalRecordsTestCase() {

        AppUtils.getValidTotalRecordsTestCase(AppConstants.STATUS);
    }

    // add case that the sum of success_records + validation_failed_records + writer_failed_records should be equal to total_records
    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the sum of success records, validation failed records, and writer failed records is equal to the total records in the batch upload process.",
            priority = 7
    )
    public void getSumOfRecordsTestCase() {

        AppUtils.getSumOfRecordsTestCase(AppConstants.STATUS);
    }


    // add case total_records can't be negative
    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the total records in the batch upload process are negative.",
            priority = 8
    )
    public void getNegativeTotalRecordsTestCase() {

        AppUtils.getNegativeTotalRecordsTestCase(AppConstants.STATUS);
    }

    // add case of Validate the timestamps (created_at, updated_at, job_started_at, job_finished_at)
    // Expected Result: The timestamps should be in the correct date value like created_at < job_started_at < updated_at
    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the timestamps in the batch upload process are validated.",
            priority = 9
    )
    public void getTimestampValidationTestCase() {

        AppUtils.getTimestampValidationTestCase(AppConstants.STATUS);
    }

    // add case of Validate that total_records, success_records, validation_failed_records, and writer_failed_records are non-negative
    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the total records, success records, validation failed records, and writer failed records in the batch upload process are non-negative.",
            priority = 10
    )
    public void getNonNegativeRecordsTestCase() {

        AppUtils.getNonNegativeRecordsTestCase(AppConstants.STATUS);
    }

    // add case of Validate that created_at, updated_at, job_started_at, and job_finished_at are non-negative
    @Test(
            groups = {
                    "MC:Get Batch Status",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the timestamps created_at, updated_at, job_started_at, and job_finished_at in the batch upload process are non-negative.",
            priority = 11
    )
    public void getNonNegativeTimestampsTestCase() {

        AppUtils.getNonNegativeTimestampsTestCase(AppConstants.STATUS);
    }
}
package com.tappayments.automation.qabatch.cases;

import com.tappayments.automation.qabatch.base.BaseTest;
import com.tappayments.automation.qabatch.config.ConfigManager;
import com.tappayments.automation.qabatch.requests.BatchSystemRequest;
import com.tappayments.automation.qabatch.utils.AppConstants;
import com.tappayments.automation.qabatch.utils.AppUtils;
import com.tappayments.automation.utils.CommonAutomationUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class DownloadInputFile extends BaseTest {

    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when a valid batch number is used to request the download input file.",
            priority = 1
    )
    public void getValidBatchNumberNoDownloadFileTestCase() {

        AppUtils.getValidBatchNumberNoDownloadFileTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when a valid batch number is used to request the download input file.",
            priority = 2
    )
    public void getInvalidBatchNumberNoDownloadFileTestCase() {

        AppUtils.getInvalidBatchNumberNoDownloadFileTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when the batch number is missing from the request.",
            priority = 3
    )
    public void getMissingBatchNumberTestCase() {

        AppUtils.getMissingBatchNumberTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when a batch number with an incorrect format is provided.",
            priority = 4
    )
    public void getIncorrectFormatBatchNumberTestCase() {

        AppUtils.getIncorrectFormatBatchNumberTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when attempting to retrieve the batch status using a different valid key but without proper authorization.",
            priority = 5
    )
    public void getBatchNumberDifferentValidKeyTestCase() {

        AppUtils.getBatchNumberDifferentValidKeyTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:Batch Number Validation",
                    "SC:Batch Number"
            },
            description = "This test verifies the response when a valid batch number is used to request the batch status.",
            priority = 6
    )
    public void getValidBatchNumberTestCase() {

        AppUtils.getValidTotalRecordsTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the sum of success records, validation failed records, and writer failed records is equal to the total records in the batch upload process.",
            priority = 7
    )
    public void getSumOfRecordsTestCase() {

        AppUtils.getSumOfRecordsTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    // add case total_records can't be negative
    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the total records in the batch upload process are negative.",
            priority = 8
    )
    public void getNegativeTotalRecordsTestCase() {

        AppUtils.getNegativeTotalRecordsTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    // add case of Validate the timestamps (created_at, updated_at, job_started_at, job_finished_at)
    // Expected Result: The timestamps should be in the correct date value like created_at < job_started_at < updated_at
    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the timestamps in the batch upload process are validated.",
            priority = 9
    )
    public void getTimestampValidationTestCase() {

        AppUtils.getTimestampValidationTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }

    // add case of Validate that total_records, success_records, validation_failed_records, and writer_failed_records are non-negative
    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the total records, success records, validation failed records, and writer failed records in the batch upload process are non-negative.",
            priority = 10
    )
    public void getNonNegativeRecordsTestCase() {

        AppUtils.getNonNegativeRecordsTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }


    // add case of Validate that created_at, updated_at, job_started_at, and job_finished_at are non-negative
    @Test(
            groups = {
                    "MC:Download Input File",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies the response when the timestamps created_at, updated_at, job_started_at, and job_finished_at in the batch upload process are non-negative.",
            priority = 11
    )
    public void getNonNegativeTimestampsTestCase() {

        AppUtils.getNonNegativeTimestampsTestCase(AppConstants.INPUT + "/" + AppConstants.DOWNLOAD);
    }
}

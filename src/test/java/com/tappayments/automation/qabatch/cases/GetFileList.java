package com.tappayments.automation.qabatch.cases;

import com.tappayments.automation.qabatch.App;
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

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class GetFileList extends BaseTest {

    // Add below are the test cases for file list.
    // case for valid page no
    // Request body: {
    //    "page": 0,
    //    "limit": 50,
    //    "period": {
    //        "date": {
    //            "from": 1707512400939,
    //            "to": 1710158860939
    //        }
    //    },
    //    "users": [
    //        "usr_9090"
    //    ],
    //    "batches":[
    //     "batch_uwyeuyeuewe",
    //     "batch_uwyeuyeuewe",
    //    ],
    //    "filters": {
    //        "status": [
    //            "failed",
    //            "completed"
    //        ]
    //    }
    //}
    //endpoint: https://api.tap.company/v2/batch/files/list
    //method: POST
    // Example method test case:
    //1. call the getListRequestBody method from AppUtils class and store the result in a variable.
    //2. convert the result to a JSON string to call the CommonAutomationUtils.stringToJson method.
    //3. call the BatchSystemRequest.postRequest method with the endpoint and the JSON string as parameters.
    //4. validate the response using the CommonAutomationUtils.verifyCommonResponseSuccessValidation method.
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Page No Validation",
                    "SC:Page No"
            },
            description = "This test verifies the response when a valid page no request is made to get the file list.",
            priority = 1
    )
    public void getValuePageNoFileListTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();

        listRequestBody.put(AppConstants.USERS, List.of(userId));
        listRequestBody.put(AppConstants.BATCHES, List.of(batchId));

        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        AppUtils.validateBatchFileResponse(response, List.of(batchId, userId, fileName, fileType), AppConstants.LINK_GENERATED);

    }

    // case for invalid page no
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Page No Validation",
                    "SC:Page No"
            },
            description = "This test verifies the response when an invalid page no request is made to get the file list.",
            priority = 2
    )
    public void getInvalidPageNoFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.PAGE, -1);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_PAGE_NO_ERROR_DESCRIPTION[0]
                )
        );
    }

    // Non-integer Page Number
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Page No Validation",
                    "SC:Page No"
            },
            description = "This test verifies the response when a non-integer page no request is made to get the file list.",
            priority = 3
    )
    public void getNonIntegerPageNoFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.PAGE, "abc");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.STATUS, HttpStatus.SC_BAD_REQUEST,
                        AppConstants.ERROR, AppConstants.BAD_REQUEST
                )
        );
    }

    //Alpha numeric one Page Number
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Page No Validation",
                    "SC:Page No"
            },
            description = "This test verifies the response when an alpha numeric page no request is made to get the file list.",
            priority = 4
    )
    public void getAlphaNumericPageNoFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.PAGE, "one");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.STATUS, HttpStatus.SC_BAD_REQUEST,
                        AppConstants.ERROR, AppConstants.BAD_REQUEST
                )
        );
    }

    //Large Page Number like 10000
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Page No Validation",
                    "SC:Page No"
            },
            description = "This test verifies the response when a large page no request is made to get the file list.",
            priority = 5
    )
    public void getLargePageNoFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.PAGE, 100);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);

        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Object> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertTrue(files.isEmpty(), "Files list isn't empty");
    }

    // case for missing page no
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Page No Validation",
                    "SC:Page No"
            },
            description = "This test verifies the response when a missing page no request is made to get the file list.",
            priority = 6
    )
    public void getMissingPageNoFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.remove(AppConstants.PAGE);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_PAGE_NO_ERROR_DESCRIPTION[1]
                )
        );
    }

    // Valid Limit
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Limit Validation",
                    "SC:Limit"
            },
            description = "This test verifies the response when a valid limit request is made to get the file list.",
            priority = 7
    )
    public void getValidLimitFileListTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.LIMIT, 5);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        AppUtils.validateBatchFileResponse(response, List.of(batchId, userId, fileName, fileType), AppConstants.LINK_GENERATED);
    }

    //Zero Limit
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Limit Validation",
                    "SC:Limit"
            },
            description = "This test verifies the response when a zero limit request is made to get the file list.",
            priority = 8
    )
    public void getZeroLimitFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.LIMIT, 0);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.LIMIT_ERROR_DESCRIPTION[0]
                )
        );
    }

    //Negative Limit
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Limit Validation",
                    "SC:Limit"
            },
            description = "This test verifies the response when a negative limit request is made to get the file list.",
            priority = 9
    )
    public void getNegativeLimitFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.LIMIT, -1);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.LIMIT_ERROR_DESCRIPTION[0]
                )
        );
    }

    // Non-integer Limit
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Limit Validation",
                    "SC:Limit"
            },
            description = "This test verifies the response when a non-integer limit request is made to get the file list.",
            priority = 10
    )
    public void getNonIntegerLimitFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.LIMIT, "fifty");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.STATUS, HttpStatus.SC_BAD_REQUEST,
                        AppConstants.ERROR, AppConstants.BAD_REQUEST
                )
        );
    }

    // case for missing limit
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Limit Validation",
                    "SC:Limit"
            },
            description = "This test verifies the response when a missing limit request is made to get the file list.",
            priority = 11
    )
    public void getMissingLimitFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.remove(AppConstants.LIMIT);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.LIMIT_ERROR_DESCRIPTION[1]
                )
        );
    }

    // Valid Date Range period.date.from and period.date.to
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Date Range Validation",
                    "SC:Date Range"
            },
            description = "This test verifies the response when a valid date range request is made to get the file list.",
            priority = 12
    )
    public void getValidDateRangeFileListTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        Map<String, Long> date = new HashMap<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate lastWeekDate = currentDate.minusWeeks(1);

        long from = lastWeekDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long to = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        date.put(AppConstants.FROM, from);
        date.put(AppConstants.TO, to);

        Map<String, Object> period = new HashMap<>();
        period.put(AppConstants.DATE, date);

        listRequestBody.put(AppConstants.PERIOD, period);
        listRequestBody.put(AppConstants.USERS, List.of(userId));
        listRequestBody.put(AppConstants.BATCHES, List.of(batchId));

        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        AppUtils.validateBatchFileResponse(response, List.of(batchId, userId, fileName, fileType), AppConstants.LINK_GENERATED);
    }

    //from Greater than to Date Range period.date.from and period.date.to
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Date Range Validation",
                    "SC:Date Range"
            },
            description = "This test verifies the response when a from date is greater than to date request is made to get the file list.",
            priority = 13
    )
    public void getFromGreaterThanToDateRangeFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        Map<String, Long> date = new HashMap<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate lastWeekDate = currentDate.minusWeeks(1);

        long from = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long to = lastWeekDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        date.put(AppConstants.FROM, from);
        date.put(AppConstants.TO, to);

        Map<String, Object> period = new HashMap<>();
        period.put(AppConstants.DATE, date);

        listRequestBody.put(AppConstants.PERIOD, period);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.ERRORS_CODE, AppConstants.INVALID_DATA_CODE,
                        AppConstants.ERRORS_ERROR, AppConstants.INVALID_DATA_ERROR,
                        AppConstants.ERRORS_DESCRIPTION, AppConstants.INVALID_DATE_ERROR_DESCRIPTION[0]
                )
        );
    }

    //Non-numeric Date Values
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Date Range Validation",
                    "SC:Date Range"
            },
            description = "This test verifies the response when a non-numeric date values request is made to get the file list.",
            priority = 14
    )
    public void getNonNumericDateRangeFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        Map<String, Object> date = new HashMap<>();
        date.put(AppConstants.FROM, "invalid");
        date.put(AppConstants.TO, "data");

        Map<String, Object> period = new HashMap<>();
        period.put(AppConstants.DATE, date);

        listRequestBody.put(AppConstants.PERIOD, period);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.STATUS, HttpStatus.SC_BAD_REQUEST,
                        AppConstants.ERROR, AppConstants.BAD_REQUEST
                )
        );
    }

    // case for missing date range
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Date Range Validation",
                    "SC:Date Range"
            },
            description = "This test verifies the response when a missing date range request is made to get the file list.",
            priority = 15
    )
    public void getMissingDateRangeFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.remove(AppConstants.PERIOD);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);

        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Object> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");
    }

    // case for date range for today
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Date Range Validation",
                    "SC:Date Range"
            },
            description = "This test verifies the response when a date range for today request is made to get the file list.",
            priority = 16
    )
    public void getDateRangeForTodayFileListTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        Map<String, Long> date = new HashMap<>();
        LocalDate currentDate = LocalDate.now();

        long from = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long to = currentDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        date.put(AppConstants.FROM, from);
        date.put(AppConstants.TO, to);

        Map<String, Object> period = new HashMap<>();
        period.put(AppConstants.DATE, date);

        listRequestBody.put(AppConstants.PERIOD, period);
        listRequestBody.put(AppConstants.USERS, List.of(userId));

        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        AppUtils.validateBatchFileResponse(response, List.of(batchId, userId, fileName, fileType), AppConstants.LINK_GENERATED);
    }

    // case for valid user list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:User List Validation",
                    "SC:User List"
            },
            description = "This test verifies the response when a valid user list request is made to get the file list.",
            priority = 17
    )
    public void getValidUserListFileListTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.USERS, List.of(userId));
        listRequestBody.remove(AppConstants.BATCHES);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        AppUtils.validateBatchFileResponse(response, List.of(batchId, userId, fileName, fileType), AppConstants.LINK_GENERATED);
    }

    // case for empty user list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:User List Validation",
                    "SC:User List"
            },
            description = "This test verifies the response when an empty user list request is made to get the file list.",
            priority = 18
    )
    public void getEmptyUserListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.USERS, List.of());
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Object> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");
    }

    // case for non string user value
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:User List Validation",
                    "SC:User List"
            },
            description = "This test verifies the response when a non string user value request is made to get the file list.",
            priority = 19
    )
    public void getNonStringUserListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.USERS, 001);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        CommonAutomationUtils.verifyCommonResponseFailedValidation(response, HttpStatus.SC_BAD_REQUEST,
                Map.of(
                        AppConstants.STATUS, HttpStatus.SC_BAD_REQUEST,
                        AppConstants.ERROR, AppConstants.BAD_REQUEST
                )
        );
    }

    // case for Duplicated User Entries
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:User List Validation",
                    "SC:User List"
            },
            description = "This test verifies the response when duplicated user entries request is made to get the file list.",
            priority = 20
    )
    public void getDuplicatedUserListFileListTestCase() {

        Response batchResponse = AppUtils.getBatchResponseDifferentUser("009");
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.USERS, List.of("009", "009"));
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        System.out.println(requestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        AppUtils.validateBatchFileResponse(response, List.of(batchId, userId, fileName, fileType), AppConstants.LINK_GENERATED);
    }

    // case for multiple user list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:User List Validation",
                    "SC:User List"
            },
            description = "This test verifies the response when multiple user list request is made to get the file list.",
            priority = 21
    )
    public void getMultipleUserListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.USERS, List.of("001", "002", "009"));
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        Set<String> validUserIds = Set.of("001", "002", "009");

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            String userId = ((Map<String, String>) file.get(AppConstants.USER)).get(AppConstants.ID);
            Assert.assertTrue(validUserIds.contains(userId), "User ID " + userId + " is not valid");
        }
    }

    // case for valid batch list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Batch List Validation",
                    "SC:Batch List"
            },
            description = "This test verifies the response when a valid batch list request is made to get the file list.",
            priority = 22
    )
    public void getValidBatchListFileListTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.BATCHES, List.of(batchId));
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        AppUtils.validateBatchFileResponse(response, List.of(batchId, userId, fileName, fileType), AppConstants.LINK_GENERATED);
    }

    // case for empty batch list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Batch List Validation",
                    "SC:Batch List"
            },
            description = "This test verifies the response when an empty batch list request is made to get the file list.",
            priority = 23
    )
    public void getEmptyBatchListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.BATCHES, List.of());
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        System.out.println(requestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Object> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");
    }

    // case for duplicated batch entries
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Batch List Validation",
                    "SC:Batch List"
            },
            description = "This test verifies the response when duplicated batch entries request is made to get the file list.",
            priority = 24
    )
    public void getDuplicatedBatchListFileListTestCase() {

        Response batchResponse = AppUtils.getBatchResponse();
        String batchId = batchResponse.jsonPath().getString(AppConstants.ID);
        String userId = batchResponse.jsonPath().getString(AppConstants.USER + "." + AppConstants.ID);
        String fileName = batchResponse.jsonPath().getString(AppConstants.FILE_NAME);
        String fileType = batchResponse.jsonPath().getString(AppConstants.FILE_TYPE);
        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.BATCHES, List.of(batchId, batchId));
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        AppUtils.validateBatchFileResponse(response, List.of(batchId, userId, fileName, fileType), AppConstants.LINK_GENERATED);
    }

    // case for multiple batch list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Batch List Validation",
                    "SC:Batch List"
            },
            description = "This test verifies the response when multiple batch list request is made to get the file list.",
            priority = 25
    )
    public void getMultipleBatchListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        Response batchResponse = AppUtils.getBatchResponse();
        String batchId1 = batchResponse.jsonPath().getString(AppConstants.ID);
        Response batchResponse1 = AppUtils.getBatchResponse();
        String batchId2 = batchResponse1.jsonPath().getString(AppConstants.ID);
        Response batchResponse2 = AppUtils.getBatchResponse();
        String batchId3 = batchResponse2.jsonPath().getString(AppConstants.ID);
        listRequestBody.put(AppConstants.BATCHES, List.of(batchId1, batchId2, batchId3));
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        Set<String> validBatchIds = Set.of(batchId1, batchId2, batchId3);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            String batchId = file.get(AppConstants.ID).toString();
            Assert.assertTrue(validBatchIds.contains(batchId), "Batch ID " + batchId + " is not valid");
        }
    }

    // case for invalid batch id
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Batch List Validation",
                    "SC:Batch List"
            },
            description = "This test verifies the response when an invalid batch id request is made to get the file list.",
            priority = 26
    )
    public void getInvalidBatchListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.BATCHES, List.of("invalid_batch_id"));
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertEquals(count, 0, "Count is not equal to 0");
        Assert.assertTrue(files.isEmpty(), "Files list isn't empty");
    }

    // case for valid status list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Status List Validation",
                    "SC:Status List"
            },
            description = "This test verifies the response when a valid status list request is made to get the file list.",
            priority = 27
    )
    public void getValidStatusListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.FILTERS, Map.of(AppConstants.STATUS, List.of("failed", "completed")));
        listRequestBody.remove(AppConstants.USERS);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        System.out.println(requestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        Set<String> validStatuses = Set.of("FAILED", "COMPLETED");

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            String status = file.get(AppConstants.STATUS).toString();
            Assert.assertTrue(validStatuses.contains(status), "Status " + status + " is not valid");
        }
    }

    // case for valid status completed only
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Status List Validation",
                    "SC:Status List"
            },
            description = "This test verifies the response when a valid status completed only request is made to get the file list.",
            priority = 28
    )
    public void getValidStatusCompletedOnlyFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.FILTERS, Map.of(AppConstants.STATUS, List.of("completed")));
        listRequestBody.remove(AppConstants.USERS);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            String status = file.get(AppConstants.STATUS).toString();
            Assert.assertEquals(status, "COMPLETED", "Status " + status + " is not valid");
        }
    }

    // case for valid status failed only
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Status List Validation",
                    "SC:Status List"
            },
            description = "This test verifies the response when a valid status failed only request is made to get the file list.",
            priority = 29
    )
    public void getValidStatusFailedOnlyFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.FILTERS, Map.of(AppConstants.STATUS, List.of("failed")));
        listRequestBody.remove(AppConstants.USERS);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(requestBody);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            String status = file.get(AppConstants.STATUS).toString();
            Assert.assertEquals(status, "FAILED", "Status " + status + " is not valid");
        }
    }

    // case for empty status list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Status List Validation",
                    "SC:Status List"
            },
            description = "This test verifies the response when an empty status list request is made to get the file list.",
            priority = 30
    )
    public void getEmptyStatusListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.FILTERS, Map.of(AppConstants.STATUS, List.of()));
        listRequestBody.remove(AppConstants.USERS);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");
    }

    // case for invalid status list
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Status List Validation",
                    "SC:Status List"
            },
            description = "This test verifies the response when an invalid status list request is made to get the file list.",
            priority = 31
    )
    public void getInvalidStatusListFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.FILTERS, Map.of(AppConstants.STATUS, List.of("invalid")));
        listRequestBody.remove(AppConstants.USERS);
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertEquals(count, 0, "Count is not equal to 0");
        Assert.assertTrue(files.isEmpty(), "Files list isn't empty");
    }

    // case for valid order by
    // possible values: chronological, reverse_chronological
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Order By Validation",
                    "SC:Order By"
            },
            description = "This test verifies the response when a valid order by request is made to get the file list.",
            priority = 32
    )
    public void getValidOrderByFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.ORDER_BY, "chronological");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        List<Long> fileCreationDates = new ArrayList<>();

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            long creationDate = Long.parseLong(file.get(AppConstants.CREATED_AT).toString());
            fileCreationDates.add(creationDate);
        }

        List<Long> sortedFileCreationDates = new ArrayList<>(fileCreationDates);
        Collections.sort(sortedFileCreationDates);

        Assert.assertEquals(fileCreationDates, sortedFileCreationDates, "Files are not sorted in chronological order");
    }

    // case for valid order by reverse chronological
    // possible values: chronological, reverse_chronological
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Order By Validation",
                    "SC:Order By"
            },
            description = "This test verifies the response when a valid order by reverse chronological request is made to get the file list.",
            priority = 33
    )
    public void getValidOrderByReverseChronologicalFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.ORDER_BY, "reverse_chronological");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        List<Long> fileCreationDates = new ArrayList<>();

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            long creationDate = Long.parseLong(file.get(AppConstants.CREATED_AT).toString());
            fileCreationDates.add(creationDate);
        }

        List<Long> sortedFileCreationDates = new ArrayList<>(fileCreationDates);
        sortedFileCreationDates.sort(Collections.reverseOrder());

        Assert.assertEquals(fileCreationDates, sortedFileCreationDates, "Files are not sorted in reverse chronological order");
    }

    // case for invalid order by
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Order By Validation",
                    "SC:Order By"
            },
            description = "This test verifies the response when an invalid order by request is made to get the file list.",
            priority = 34
    )
    public void getInvalidOrderByFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.ORDER_BY, "invalid_order");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        List<Long> fileCreationDates = new ArrayList<>();

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            long creationDate = Long.parseLong(file.get(AppConstants.CREATED_AT).toString());
            fileCreationDates.add(creationDate);
        }

        List<Long> sortedFileCreationDates = new ArrayList<>(fileCreationDates);
        Collections.sort(sortedFileCreationDates);

        Assert.assertEquals(fileCreationDates, sortedFileCreationDates, "Files are not sorted in chronological order");
    }

    // case for empty order by
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Order By Validation",
                    "SC:Order By"
            },
            description = "This test verifies the response when an empty order by request is made to get the file list.",
            priority = 35
    )
    public void getEmptyOrderByFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.ORDER_BY, "");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(requestBody);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        List<Long> fileCreationDates = new ArrayList<>();

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            long creationDate = Long.parseLong(file.get(AppConstants.CREATED_AT).toString());
            fileCreationDates.add(creationDate);
        }

        List<Long> sortedFileCreationDates = new ArrayList<>(fileCreationDates);
        Collections.sort(sortedFileCreationDates);

        Assert.assertEquals(fileCreationDates, sortedFileCreationDates, "Files are not sorted in chronological order");
    }

    // case for null order by
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Order By Validation",
                    "SC:Order By"
            },
            description = "This test verifies the response when a null order by request is made to get the file list.",
            priority = 36
    )
    public void getNullOrderByFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.ORDER_BY, "reverse_chronological");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        List<Long> fileCreationDates = new ArrayList<>();

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            long creationDate = Long.parseLong(file.get(AppConstants.CREATED_AT).toString());
            fileCreationDates.add(creationDate);
        }

        List<Long> sortedFileCreationDates = new ArrayList<>(fileCreationDates);
        sortedFileCreationDates.sort(Collections.reverseOrder());

        Assert.assertEquals(fileCreationDates, sortedFileCreationDates, "Files are not sorted in reverse chronological order");
    }

    // case for missing order by
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:Order By Validation",
                    "SC:Order By"
            },
            description = "This test verifies the response when a missing order by request is made to get the file list.",
            priority = 37
    )
    public void getMissingOrderByFileListTestCase() {

        Map<String, Object> listRequestBody = AppUtils.getListRequestBody();
        listRequestBody.put(AppConstants.ORDER_BY, "reverse_chronological");
        String requestBody = CommonAutomationUtils.stringToJson(listRequestBody);
        Response response = BatchSystemRequest.postRequest(requestBody, AppConstants.FILE_LIST_URL);
        System.out.println(response.prettyPrint());
        int count = response.jsonPath().getInt(AppConstants.COUNT);
        List<Map<String, Object>> files = response.jsonPath().getList(AppConstants.FILES);
        List<Long> fileCreationDates = new ArrayList<>();

        CommonAutomationUtils.verifyStatusCode(response, HttpStatus.SC_OK);
        Assert.assertTrue(count > 0, "Count is not greater than 0");
        Assert.assertFalse(files.isEmpty(), "Files list is empty");

        for (Map<String, Object> file : files) {

            long creationDate = Long.parseLong(file.get(AppConstants.CREATED_AT).toString());
            fileCreationDates.add(creationDate);
        }

        List<Long> sortedFileCreationDates = new ArrayList<>(fileCreationDates);
        sortedFileCreationDates.sort(Collections.reverseOrder());

        Assert.assertEquals(fileCreationDates, sortedFileCreationDates, "Files are not sorted in reverse chronological order");
    }

    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test validates the total records in the batch upload process. It retrieves the batch details from the response, including batch ID, file name, file type, and pre-signed URL," +
            " and uses these details to fetch the uploaded file. Finally, the test verifies that the batch upload response contains the correct information and the total number of records matches " +
            "the expected value",
            priority = 38
    )
    public void getTotalRecordsFileListTestCase() {

        List<String> fileBatchStatusParameters = AppUtils.getFileBatchStatusParameters();
        AppUtils.getFileBatchStatusResponse(fileBatchStatusParameters);
    }

    // add case that the sum of getSumOfRecordsTestCase success_records + validation_failed_records + writer_failed_records should be equal to total_records
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test validates the sum of success, validation failed, and writer failed records matches the total records after file upload.",
            priority = 39
    )
    public void getSumOfRecordsTestCase() {

        List<String> fileBatchStatusParameters = AppUtils.getFileBatchStatusParameters();
        Response batchFileStatusResponse = AppUtils.getFileBatchStatusResponse(fileBatchStatusParameters);

        int successRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.SUCCESS_RECORDS);
        int validationFailedRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.VALIDATION_FAILED_RECORDS);
        int writerFailedRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.WRITER_FAILED_RECORDS);
        int totalRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.TOTAL_RECORDS);

        Assert.assertEquals(successRecords + validationFailedRecords + writerFailedRecords, totalRecords, "Sum of records is not equal to total records");
    }

    // add case total_records can't be negative
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies that the total records can't be negative after file upload.",
            priority = 40
    )
    public void getNegativeTotalRecordsFileListTestCase() {

        List<String> fileBatchStatusParameters = AppUtils.getFileBatchStatusParameters();
        Response batchFileStatusResponse = AppUtils.getFileBatchStatusResponse(fileBatchStatusParameters);

        int totalRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.TOTAL_RECORDS);
        Assert.assertTrue(totalRecords >= 0, "Total records is negative");
    }

    // add case of Validate the timestamps (created_at, updated_at, job_started_at, job_finished_at)
    // Expected Result: The timestamps should be in the correct date value like created_at < job_started_at < updated_at
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test validates the timestamps (created_at, updated_at, job_started_at, job_finished_at) after file upload.",
            priority = 41
    )
    public void getTimestampsFileListTestCase() {

        List<String> fileBatchStatusParameters = AppUtils.getFileBatchStatusParameters();
        Response batchFileStatusResponse = AppUtils.getFileBatchStatusResponse(fileBatchStatusParameters);

        long createdAt = batchFileStatusResponse.jsonPath().getLong(AppConstants.FILES + "[0]." + AppConstants.CREATED_AT);
        long updatedAt = batchFileStatusResponse.jsonPath().getLong(AppConstants.FILES + "[0]." + AppConstants.UPDATED_AT);
        long jobStartedAt = batchFileStatusResponse.jsonPath().getLong(AppConstants.FILES + "[0]." + AppConstants.JOB_STARTED_AT);
        long jobFinishedAt = batchFileStatusResponse.jsonPath().getLong(AppConstants.FILES + "[0]." + AppConstants.JOB_FINISHED_AT);

        Assert.assertTrue(createdAt < jobStartedAt, "Created at is not less than job started at");
        Assert.assertTrue(jobStartedAt < updatedAt, "Job started at is not less than updated at");
        Assert.assertTrue(jobStartedAt < jobFinishedAt, "Job started at is not less than job finished at");
    }

    // add case of Validate that total_records, success_records, validation_failed_records, and writer_failed_records are non-negative
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies that total records, success records, validation failed records, and writer failed records are non-negative after file upload.",
            priority = 42
    )
    public void getNonNegativeRecordsFileListTestCase() {

        List<String> fileBatchStatusParameters = AppUtils.getFileBatchStatusParameters();
        Response batchFileStatusResponse = AppUtils.getFileBatchStatusResponse(fileBatchStatusParameters);

        int totalRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.TOTAL_RECORDS);
        int successRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.SUCCESS_RECORDS);
        int validationFailedRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.VALIDATION_FAILED_RECORDS);
        int writerFailedRecords = batchFileStatusResponse.jsonPath().getInt(AppConstants.FILES + "[0]." + AppConstants.WRITER_FAILED_RECORDS);

        Assert.assertTrue(totalRecords >= 0, "Total records is negative");
        Assert.assertTrue(successRecords >= 0, "Success records is negative");
        Assert.assertTrue(validationFailedRecords >= 0, "Validation failed records is negative");
        Assert.assertTrue(writerFailedRecords >= 0, "Writer failed records is negative");
    }

    // add case of Validate that created_at, updated_at, job_started_at, and job_finished_at are non-negative
    @Test(
            groups = {
                    "MC:Get File List",
                    "Abdul Rehman",
                    "SECTION:File Upload and Status Validation",
                    "SC:File Upload and Status"
            },
            description = "This test verifies that created at, updated at, job started at, and job finished at are non-negative after file upload.",
            priority = 43
    )
    public void getNonNegativeTimestampsFileListTestCase() {

        List<String> fileBatchStatusParameters = AppUtils.getFileBatchStatusParameters();
        Response batchFileStatusResponse = AppUtils.getFileBatchStatusResponse(fileBatchStatusParameters);

        long createdAt = batchFileStatusResponse.jsonPath().getLong(AppConstants.FILES + "[0]." + AppConstants.CREATED_AT);
        long updatedAt = batchFileStatusResponse.jsonPath().getLong(AppConstants.FILES + "[0]." + AppConstants.UPDATED_AT);
        long jobStartedAt = batchFileStatusResponse.jsonPath().getLong(AppConstants.FILES + "[0]." + AppConstants.JOB_STARTED_AT);
        long jobFinishedAt = batchFileStatusResponse.jsonPath().getLong(AppConstants.FILES + "[0]." + AppConstants.JOB_FINISHED_AT);

        Assert.assertTrue(createdAt >= 0, "Created at is negative");
        Assert.assertTrue(updatedAt >= 0, "Updated at is negative");
        Assert.assertTrue(jobStartedAt >= 0, "Job started at is negative");
        Assert.assertTrue(jobFinishedAt >= 0, "Job finished at is negative");
    }
}
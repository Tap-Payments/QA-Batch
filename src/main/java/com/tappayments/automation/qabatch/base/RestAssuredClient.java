package com.tappayments.automation.qabatch.base;

import com.tappayments.automation.config.ExtentReportManager;
import com.tappayments.automation.qabatch.config.ConfigManager;
import com.tappayments.automation.qabatch.utils.AppConstants;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class RestAssuredClient extends BaseTest {

    public static RequestSpecification requestSpecification(Map<String, String> headers) {

        return given().headers(headers);
    }

    public static Response postRequest(String body, String endPoint) {

        Map<String, String> headers = Map.of(
                AppConstants.CONTENT_TYPE, ConfigManager.getPropertyValue(AppConstants.CONTENT_TYPE_VALUE),
                AppConstants.AUTHORIZATION, AppConstants.BEARER + ConfigManager.getPropertyValue(AppConstants.AUTHORIZATION_VALUE)
        );

        RequestSpecification requestSpecification = requestSpecification(headers);

        Response response = requestSpecification.body(body)
                .when()
                .log().all()
                .post(endPoint);

        printRequestLogInReport(requestSpecification);
        printResponseLogInReport(response);

        return response;
    }

    public static Response getRequest(String endPoint) {

        return getRequest(endPoint, Map.of(
                AppConstants.CONTENT_TYPE, ConfigManager.getPropertyValue(AppConstants.CONTENT_TYPE_VALUE),
                AppConstants.AUTHORIZATION, AppConstants.BEARER + ConfigManager.getPropertyValue(AppConstants.AUTHORIZATION_VALUE)
        ));
    }

    public static Response getRequest(String endPoint, Map<String, String> headers) {

        RequestSpecification requestSpecification = requestSpecification(headers);

        Response response = requestSpecification
                .when()
                .log().all()
                .get(endPoint);

        printRequestLogInReport(requestSpecification);
        printResponseLogInReport(response);

        return response;
    }

    private static void printRequestLogInReport(RequestSpecification requestSpecification){

        QueryableRequestSpecification queryableRequestSpecification = SpecificationQuerier.query(requestSpecification);
        ExtentReportManager.logInfoDetails("Endpoint is : " + queryableRequestSpecification.getURI());
        ExtentReportManager.logInfoDetails("Method is : " + queryableRequestSpecification.getMethod());
        ExtentReportManager.logInfoDetails("Headers are");
        ExtentReportManager.logHeaders(queryableRequestSpecification.getHeaders().asList());
        ExtentReportManager.logInfoDetails("Request body is: ");
        ExtentReportManager.logJson(queryableRequestSpecification.getBody());
    }

    private static void printResponseLogInReport(Response response){

        ExtentReportManager.logInfoDetails("Response status : " + response.getStatusCode());
        ExtentReportManager.logInfoDetails("Response body is : ");
        ExtentReportManager.logJson(response.getBody().prettyPrint());
    }
}
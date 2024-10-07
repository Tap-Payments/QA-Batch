package com.tappayments.automation.qabatch.base;
import com.tappayments.automation.qabatch.config.ConfigManager;
import com.tappayments.automation.qabatch.utils.AppConstants;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    @BeforeClass
    public void setup() {

        RestAssured.baseURI = ConfigManager.getPropertyValue(AppConstants.BASE_URI_VALUE);
    }
}
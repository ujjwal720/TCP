package login;


import java.io.IOException;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import testsuitebase.CommonMethods;
import testsuitebase.SuiteBase;
import utility.SeleniumFunctions;
import utility.SuiteUtility;

/**
 * This class is for validating the Login Functionality
 */
public class Login extends SuiteBase {

    @BeforeTest
    public void resetFlag() throws IOException {
        className = this.getClass().getSimpleName(); 
    }

    /**
     * verify Login for User Profile
     */
    @Test(dataProvider = "LoginUserProfile_Data", priority = 1)
    public void LoginUserProfile(
            String testCaseID,
            String Username,
            String Password,
            String ExpectedText
            ) throws JSONException, Exception {

    	/**
    	 * store method name**
    	 * 
    	 */
        testCaseName = new Object() {}.getClass().getEnclosingMethod().getName(); 
        
        checkTestToRun(testCaseName);
        
        threadsleep(1000);
        try {
            testReport = extent.startTest(testCaseID);
            testReport.assignCategory(className);
            testEmailReport = extentEmailReport.startTest(testCaseID);
            testEmailReport.assignCategory(className);

            
            //SeleniumFunctions.clickbyXpath("link_login","");
            threadsleep(2000);
            SeleniumFunctions.input("txtbx_username", Username);
            SeleniumFunctions.input("txtbx_password", Password);
            
            SeleniumFunctions.clickbyXpath("btn_login","");
            threadsleep(2000);
            String labeltext = SeleniumFunctions.FindElementbyXPath_returnText("label_welcome", "");
            
            if(labeltext.equals(ExpectedText))
            {
            	testResult(true,"",testCaseName,"");
            	SeleniumFunctions.clickbyXpath("btn_loout","");
            }
            else
            {
            	testResult(false,"",testCaseName,"");   
            	labeltext="";
            }
            threadsleep(2000);
            
            
            
        } catch (Exception e) {
            exceptionHandle(e,testCaseID);
        }
    }

    /**
     * Data provider for LoginUserProfile
     * @return
     * @throws IOException
     */
    @DataProvider
    public Object[][] LoginUserProfile_Data() throws IOException {
    	className = this.getClass().getSimpleName();
        fileInitialization();
        return SuiteUtility.GetTestDataUtility(filePath, "LoginUserProfile");
    }
      
    /**
     * This method is used to write results in report after execution of methods
     * 
     * @throws Exception
     * @throws JSONException
     */
    @AfterMethod
    public void reporterDataResults() throws JSONException, Exception {
        extent.endTest(testReport);
        extent.flush();
        extentEmailReport.endTest(testEmailReport);
        extentEmailReport.flush();
        writeResult(testSkip, testFail, filePath, sheetName, testCaseName, dataSet);
    }
}
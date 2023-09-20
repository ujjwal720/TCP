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
 * This class is for validating the Audit Log Functionality
 */
public class GoogleSearch extends SuiteBase {

    @BeforeTest
    public void resetFlag() throws IOException {
        className = this.getClass().getSimpleName(); //var1 -> store class name | GoogleSearch
    }

    /**
     * verify Google search
     */
    @Test(dataProvider = "Search_Data", priority = 1)
    public void Search(
            String testCaseID,
            String searchtext 
            ) throws JSONException, Exception {

    	/**
    	 * store method name**
    	 * 
    	 */
        testCaseName = new Object() {}.getClass().getEnclosingMethod().getName(); // var2 -> store the method name | Search
        
        checkTestToRun(testCaseName);
        
        threadsleep(1000);
        try {
            testReport = extent.startTest(testCaseID);
            testReport.assignCategory(className);
            testEmailReport = extentEmailReport.startTest(testCaseID);
            testEmailReport.assignCategory(className);

           // driver = new ChromeDriver();
            
            SeleniumFunctions.input("txt_searchbox", searchtext);
            
            SeleniumFunctions.clickbyXpath("btn_search","");
            
            driver.navigate().back();
            
            Boolean Result = true;
            threadsleep(2000);
            testResult(Result,"",testCaseName,"");
            
        } catch (Exception e) {
            exceptionHandle(e,testCaseID);
        }
    }

    /**
     * Data provider for Search
     * @return
     * @throws IOException
     */
    @DataProvider
    public Object[][] Search_Data() throws IOException {
    	className = this.getClass().getSimpleName();
        fileInitialization();
        return SuiteUtility.GetTestDataUtility(filePath, "Search");
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
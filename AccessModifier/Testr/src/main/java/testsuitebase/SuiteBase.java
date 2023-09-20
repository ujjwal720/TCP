package testsuitebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.json.JSONException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import utility.ReadXLS;
import utility.SeleniumFunctions;
import utility.SuiteUtility;

/**
 * This class is used Suite Initialization
 */
public class SuiteBase {
   
    public static String className;
    protected static Properties objectRepository;
    protected static Properties commonLocator;
    public static SoftAssert s_assert;
    public static ExtentTest testReport, testEmailReport;
    public static ReadXLS filePath;
    public static String testCaseName;
    public static String sheetName;
    public static String errorlogText;
    public static String toRunColumnNameTestCase;
    public static String toRunColumnNameTestData;
    protected static String testDataToRun[];
    public static ExtentReports extent;
    public static ExtentReports extentEmailReport;
    public static String path_OR, path_CL;
    public static String toastMessage = " ";
    public static String testCaseNamePrevious = " ";
    public static String baseURL;
    protected static WebDriver driver;
    public static WebDriverWait driverWait;
    protected static Date d = new Date();
    public static String dateWithoutTime = d.toString().substring(0, 10);
    public static String dateWithTime = d.toString();
    public static final String DIR = System.getProperty("user.dir");
    public static final String DOWNLOADPATH = "C:\\Users\\" + System.getProperty("user.name") + "\\Downloads";
    public static int dataSet = -1;
    public static int failureCount;
    public static int testCaseCount;
    public static int failureAllowed = 3, failureCount_SeleniumFun;
    public static boolean testSkip = false;
    public static boolean testFail = false;
    public static boolean testMethodFail = false;
    public static boolean testCasePass = true;
    public static boolean actualResult;

    /**
     * This method will be executed only once. Here we will do initialization
     * related to report generation, common locator and browser invoke .
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @BeforeSuite
    public static void suiteInitialization() throws IOException, InterruptedException {
        path_CL = DIR + "//src//test//resources//elementlocator//CommonLocator.txt";
        try (FileInputStream fs = new FileInputStream(path_CL);) {
            dateWithTime = dateWithTime.replace(' ', '_');
            dateWithTime = dateWithTime.replace(':', '_');
            /**
             * Create folder to save the test result report according to the
             * date and report generated
             */
            createfolder_currentdate();
            createfolder_emailreport();
            File dirPath = new File(DIR + "//reports//EmailReport//");
            for (File f : dirPath.listFiles()) {
                if (!f.isDirectory())
                    f.delete();
            }
            commonLocator = new Properties();
            commonLocator.load(fs);
            baseURL = commonLocator.getProperty("URL");

            /**
             * Generates the report from the execution of script through
             * Selenium with the details data
             */
            String appName = commonLocator.getProperty("applicationName");
            String reportFolderPath = DIR + "//reports//" + appName + "//" + dateWithoutTime;
            String reportPath = reportFolderPath + "//" + appName + "_" + dateWithTime + "_.html";

            /**
             * Generates the mailable report which is compressed in size to
             * email
             */
            String emailableReportPath = DIR + "//reports//EmailReport//" + appName + "testReport.html";

            extent = new ExtentReports(reportPath, true);
            extent.loadConfig(new File(DIR + "//config.xml"));

            extentEmailReport = new ExtentReports(emailableReportPath, true);
            extentEmailReport.loadConfig(new File(DIR + "//config.xml"));

            System.out.println("Execution started:");
            
            /**
             * browser to Run the script
             */
            if ("remote".equals(commonLocator.getProperty("RunConfig"))) {
                SeleniumFunctions.openRemoteBrowser(commonLocator.getProperty("Browser"));
                SeleniumFunctions.navigate(baseURL);
            } else {
                SeleniumFunctions.openBrowser(commonLocator.getProperty("Browser"));
                SeleniumFunctions.navigate(baseURL);
            }
        } catch (Exception e) {
        	System.out.println("Error occured in suite initialization: " + e);
        }
    }

    /**
     * This method is used to check Test Case and Test Data to run
     * 
     * @param tcName
     * @throws IOException
     * @throws InterruptedException
     */
    public static void checkTestToRun(String tcName) throws IOException {
        dataSet++;
        try (FileInputStream fs = new FileInputStream(path_OR);) {
            objectRepository = new Properties(System.getProperties());
            objectRepository.load(fs);
        } catch (Exception e) {
        	System.out.println("Error occured in loading object repository: " + e);
        }
        s_assert = new SoftAssert();
        ReadXLS testCaseListExcel = new ReadXLS(DIR + "//src//test//resources//excelfiles//" + className + ".xls");
		filePath = testCaseListExcel;
        sheetName = "TestCasesList";
        toRunColumnNameTestCase = "CaseToRun";
        toRunColumnNameTestData = "DataToRun";
        System.out.println(tcName + " : Execution started.");
        

        /**
         * Check Test Case Run flag
         */
        if (!SuiteUtility.checkToRunUtility(filePath, sheetName, toRunColumnNameTestCase, tcName)) {
        	System.out.println(tcName + " : CaseToRun = N for So Skipping Execution.");
        	testSkip = true;
            SuiteUtility.WriteResultUtility(filePath, sheetName, "Pass/Fail/Skip", tcName, "SKIPPED");
            throw new SkipException(
                    tcName + "'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of " + tcName);
        }
        testDataToRun = SuiteUtility.checkToRunUtilityOfData(filePath, tcName, toRunColumnNameTestData);

        /**
         * Check Test Data Run flag
         */
        if (!"Y".equalsIgnoreCase(testDataToRun[dataSet])) {
        	System.out.println(
                    tcName + " : DataToRun = N for data set line " + (dataSet + 1)
                            + " So skipping Its execution.");
            testSkip = true;
            throw new SkipException(
                    "DataToRun for row number " + dataSet + " Is No Or Blank. So Skipping Its Execution.");
        }
        driverWait = new WebDriverWait(driver, 60);
    }

    /**
     * This method is used for Data and Locator file initialization
     * 
     * @throws IOException
     */
    public static void fileInitialization() {
        /**
         * Constructor Of Read_XLS Utility Class.
         */
        ReadXLS testCaseListExcel = new ReadXLS(DIR + "//src//test//resources//excelfiles//" + className + ".xls");

        /**
         * Set path of Locator file
         */
        path_OR = DIR + "//src//test//resources//elementlocator//" + className + "Locator.txt";

        /**
         * Bellow given syntax will Insert log In applog.log file.
         */
        System.out.println("All Excel Files Initialised successfully.");
        testSkip = false;
        testFail = false;
        failureCount_SeleniumFun = 0;
        dataSet = -1;
        filePath = testCaseListExcel;

    }

    /**
     * This is method is used to save test result in data file
     * 
     * @param actualResult
     * @param ExpectedResult
     * @param TestCaseName
     * @throws Exception 
     * @throws JSONException 
     */
    public static void testResult(Boolean actualResult, String expectedResult, String testCaseName,String testStep)
            throws JSONException, Exception {
        CommonMethods cm = new CommonMethods();
        if (!(actualResult)) {
        	System.out.println(
                    testCaseName + " : Actual Value " + actualResult +
                            " and expected value " + expectedResult
                            + " Not match. Test data set failed.");
            s_assert.assertEquals(
                    actualResult,
                    expectedResult,
                    actualResult + " And " + expectedResult + " Not Match");
            testReport.log(LogStatus.FAIL, "Test Failed for:- " + testCaseName);
            testFail = true;
            testCasePass = false;
            cm.captureScreenshot(testCaseName);
           
        } else {
            cm.captureScreenshot(testCaseName);
            testReport.log(LogStatus.PASS, "Test Passed for:- " + testCaseName);
        }
        if (testFail) {
            testReport.log(LogStatus.ERROR, "Test Failed for:- " + testCaseName);
            s_assert.assertAll();
        }
    }

    /**
     * This method is used to create current date folder under reports ->
     * AuditConfirmations
     */
    public static void createfolder_currentdate() {
        File newDIR = new File(DIR + "//reports//Search//" + dateWithoutTime);
        boolean result = false;
        if (!newDIR.exists()) {
            try {
                newDIR.mkdir();
                result = true;
            } catch (SecurityException se) {
            	System.err.println("Failed to create folder: " + se);
            }
            if (result) {
            	System.out.println("Current date folder created");
            }
        }
    }

    /**
     * This method is used to create current email report folder under reports
     */
    public static void createfolder_emailreport() {
        File newDIR = new File(DIR + "//reports//EmailReport");
        boolean result = false;
        if (!newDIR.exists()) {
            try {
                newDIR.mkdir();
                result = true;
            } catch (SecurityException se) {
            	System.err.println("Failed to create folder: " + se);
            }
            if (result) {
            	System.out.println("Current date folder created");
            }
        }
    }

    /**
     * This method is used to log error occurred during test case execution. It
     * will also mark test case as failed
     * 
     * @param e
     * @throws Exception 
     * @throws JSONException 
     */
    public static void exceptionHandle(Exception e,String testStep) throws JSONException, Exception {
    	System.err.println("Error occured during test execution: " + e);
        testCasePass = false;
        testFail = true;
        testReport.log(LogStatus.FAIL, testCaseName + " Fails");
        actualResult = false;
        testResult(actualResult, "", testCaseName,testStep);
    }

    /**This method is used for implicit wait
     * 
     * @param waittime
     * @throws InterruptedException
     */
    public static void threadsleep(int waittime) throws InterruptedException
    {
    	Thread.sleep(waittime);
    }
    /**
     * This is method is used to write Test Case and Test Data result
     * 
     * @param testCasePass
     * @param testMethodFail
     * @param testSkip
     * @param testFail
     * @param filePath
     * @param sheetName
     * @param testCaseName
     * @param dataSet
     */
    public static void writeResult(
            
            Boolean testSkip,
            Boolean testFail,
            ReadXLS filePath,
            String sheetName,
            String testCaseName,
            int dataSet) {
        if (!testCaseNamePrevious.equals(testCaseName)) {
            failureCount = 0;
            testCaseCount = 0;
        }
        testCaseNamePrevious = testCaseName;
        if (testSkip) {
        	System.out.println(
                    testCaseName + " : Reporting test data set line " + (dataSet + 1) + " as SKIPPED In excel.");
            SuiteUtility.WriteResultUtility(filePath, testCaseName, "Pass/Fail/Skip", dataSet + 1, "SKIPPED");
        } else if (testFail) {
            failureCount++;
            System.out.println(
                    testCaseName + " : Reporting test data set line " + (dataSet + 1) + " as FAILED In excel.");
            s_assert = null;
            SuiteUtility.WriteResultUtility(filePath, testCaseName, "Pass/Fail/Skip", dataSet + 1, "FAILED");
        } else {
        	System.out.println(
                    testCaseName + " : Reporting test data set line " + (dataSet + 1) + " as PASSED In excel.");
            SuiteUtility.WriteResultUtility(filePath, testCaseName, "Pass/Fail/Skip", dataSet + 1, "PASSED");
            testCaseCount++;
        }
        /*
         * Test Case Result
         */
        if (failureCount >= 1) {
        	System.out.println(testCaseName + " : Reporting test case as FAILED In excel.");
            SuiteUtility.WriteResultUtility(filePath, sheetName, "Pass/Fail/Skip", testCaseName, "FAILED");
        } else if (testCaseCount < 1 && testSkip) {
        	System.out.println(testCaseName + " : Reporting test case as SKIPPED In excel.");
            SuiteUtility.WriteResultUtility(filePath, sheetName, "Pass/Fail/Skip", testCaseName, "SKIPPED");
        } else {
        	System.out.println(testCaseName + " : Reporting test case as PASSED In excel.");
            SuiteUtility.WriteResultUtility(filePath, sheetName, "Pass/Fail/Skip", testCaseName, "PASSED");
        }
    }

    /**
     * This is method is used to close web driver and extent report instances
     * after test execution
     * 
     * @throws IOException
     */
    @AfterSuite
    public void stopExecution() {
        driver.close();
        driver.quit();
        extent.flush();
    }
}

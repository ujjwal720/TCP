package testsuitebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.relevantcodes.extentreports.LogStatus;

import utility.SeleniumFunctions;

/**
 * This class contains functionalities we which are frequently using in our test
 * automation project
 *
 */
public class CommonMethods extends SuiteBase {

    /**
     * Get Toast Message to verify the message to see the execution the of
     * particular is passing or not
     * 
     * @param expectedMessage
     * @throws Exception
     */
    public static void verifytoastMessage(String expectedMessage) throws Exception {
        try {
            String xpathToastMessage = "(//*[contains(@class,'toast-content')])[last()]";
            driverWait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath(xpathToastMessage))));
            toastMessage = SeleniumFunctions.FindElementbyXPath_returnText("label_toastmessage", "");
            if (toastMessage == null || "".equals(toastMessage)) {
                actualResult = false;
            } else {
                if (toastMessage.toLowerCase().contains(expectedMessage.toLowerCase())) {
                    actualResult = true;
                    testReport.log(LogStatus.PASS, "Toast message verified successfully " + toastMessage);
                    testEmailReport.log(LogStatus.PASS, "Toast message verified successfully " + toastMessage);
                } else {
                    actualResult = false;
                    testReport.log(LogStatus.FAIL, "Toast message  not verified successfully " + toastMessage);
                    testEmailReport.log(LogStatus.FAIL, "Toast message not verified successfully " + toastMessage);
                }
            }
        } catch (Exception e) {
            actualResult = false;
            testReport.log(LogStatus.FAIL, "Toast message  not verified successfully " + toastMessage);
            testEmailReport.log(LogStatus.FAIL, "Toast message not verified successfully " + toastMessage);
            System.err.println("Error in verifying toast message: " + e);
        }
    }

    /**
     * Capturing the screenshot of the test cases either passing or failing
     * 
     * @param testCaseID
     * @throws IOException
     * @throws InterruptedException
     */
    public static void captureScreenshot(String testCaseID) throws IOException, InterruptedException {
        try {
            dateWithTime = dateWithTime.replace(' ', '_');
            dateWithTime = dateWithTime.replace(':', '_');
            Thread.sleep(1000);
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File screenshotLocation = new File(
                    DIR + "\\screenshots\\" + "\\" + testCaseID + "_" + dateWithTime + ".png");
            FileUtils.copyFile(screenshot, screenshotLocation);
            Thread.sleep(1000);
            InputStream is = new FileInputStream(screenshotLocation);
            byte[] imageBytes = IOUtils.toByteArray(is);
            Thread.sleep(2000);
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            testReport.log(
                    LogStatus.INFO,
                    "Snapshot below: " + testReport.addBase64ScreenShot("data:image/png;base64," + base64));
        } catch (Exception e) {
        	System.err.println("Failed to capture screenshot:" + e);
        }
    }
}
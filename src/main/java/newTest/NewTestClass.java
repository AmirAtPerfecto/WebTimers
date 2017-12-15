package newTest;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import perfecto.Utils;

import java.io.IOException;


public class NewTestClass {
    RemoteWebDriver driver;
    PerfectoExecutionContext perfectoExecutionContext;
    ReportiumClient reportiumClient;
    String pageTimersString = "", pageResourceTimersString = "";
    WebPageTimersClass pageTimers;
    WebPageResourceTimerClass pageResourceTimers;


    @Parameters({"platformName", "platformVersion", "browserName", "browserVersion", "screenResolution"})
    @BeforeTest
    public void beforeTest(String platformName, String platformVersion, String browserName, String browserVersion, String screenResolution) throws IOException {
        driver = Utils.getRemoteWebDriver(platformName, platformVersion, browserName, browserVersion, screenResolution);
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withProject(new Project("My Project", "1.0"))
                .withJob(new Job("My Job", 45))
                .withContextTags("tag1")
                .withWebDriver(driver)
                .build();
        reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
    }

    @Test
    public void test() {
        try {
            reportiumClient.testStart("Perfecto Web Timers Test", new TestContext("Performance", "tag3"));
            System.out.println("Yay");
                driver.get("http://www.amazon.com");
                // obtain the data from the page

                pageTimers = new WebPageTimersClass(driver, "Amazon Home");
                pageResourceTimers = new WebPageResourceTimerClass(driver, "Amazon Home");
                analyzeWebTimers("Amazon.com");

                driver.findElementById("twotabsearchtextbox").sendKeys("The Digital Qulity Handbook");
                driver.findElementById("twotabsearchtextbox").submit();
                driver.findElement(By.xpath("//a[contains(@href, 'Quality')]")).click();
                driver.findElementById("add-to-cart-button");

                pageTimers = new WebPageTimersClass(driver, "Quality DevOPS Book");
                pageResourceTimers = new WebPageResourceTimerClass(driver, "Quality DevOPS Book");
                analyzeWebTimers("Quality_DevOPS");

            reportiumClient.testStop(TestResultFactory.createSuccess());
        } catch (Exception e) {
            //reportiumClient.testStop(TestResultFactory.createFailure(e.getMessage(), e));
            e.printStackTrace();
        }
    }

    @AfterTest
    public void afterTest() {
        try {
            // Retrieve the URL of the Single Test Report, can be saved to your execution summary and used to download the report at a later point
            String reportURL = reportiumClient.getReportUrl();
            System.out.println("Report URL:" + reportURL);

            driver.close();
            System.out.println("Report: " + reportURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.quit();
    }

    private void analyzeWebTimers(String pageName) {
        if (pageTimers.isPageLoadLongerThanBaseline(200, WebPageTimersClass.CompareMethod.VS_AVG, pageName))
            System.out.println("Page "+ pageName+ "took much longer to load!!! ");
        pageTimers.appendToCSV(pageName);
        pageTimersString = pageTimers.toString();
        pageResourceTimers.appendToCSV(pageName);
        pageTimersString = pageResourceTimers.toString();

        if (null != pageTimers)
            System.out.println(pageTimers);
        if (null != pageTimersString)
            System.out.println(pageTimersString);
    }


}
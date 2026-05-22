package base;

import java.time.Duration;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import utilities.constants;

public class Test_Base_Class {

        protected WebDriver driver;

        @BeforeMethod
        public void setup() {

                ChromeOptions options = new ChromeOptions();

                // ==========================
                // HEADLESS
                // ==========================
                options.addArguments("--headless=new");

                // ==========================
                // ANTI DETECTION
                // ==========================
                options.addArguments(
                                "--disable-blink-features=AutomationControlled");

                options.setExperimentalOption(
                                "excludeSwitches",
                                new String[] { "enable-automation" });

                options.setExperimentalOption(
                                "useAutomationExtension",
                                false);

                // ==========================
                // USER AGENT
                // ==========================
                options.addArguments(
                                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36");

                // ==========================
                // STABILITY
                // ==========================
                options.addArguments("--disable-notifications");

                options.addArguments("--remote-allow-origins=*");

                options.addArguments("--disable-gpu");

                options.addArguments("--no-sandbox");

                options.addArguments("--disable-dev-shm-usage");

                options.addArguments("--window-size=1920,1080");

                driver = new ChromeDriver(options);

                // ==========================
                // REMOVE WEBDRIVER FLAG
                // ==========================
                ((JavascriptExecutor) driver).executeScript(
                                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

                driver.manage()
                                .timeouts()
                                .implicitlyWait(Duration.ofSeconds(10));

                driver.manage()
                                .window()
                                .setSize(new Dimension(1920, 1080));

                // ==========================
                // OPEN BASE URL FIRST
                // ==========================
                driver.get(constants.BASE_URL);

                System.out.println(
                                "Browser launched successfully.");
        }

        @AfterMethod
        public void teardown() {

                if (driver != null) {

                        driver.quit();

                        System.out.println(
                                        "Browser closed successfully.");
                }
        }
}
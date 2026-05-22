package pom;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class home_page_pom {

        private final WebDriver driver;

        private final WebDriverWait wait;

        public home_page_pom(WebDriver driver) {

                this.driver = driver;

                this.wait = new WebDriverWait(
                                driver,
                                Duration.ofSeconds(30));

                PageFactory.initElements(driver, this);
        }

        // ==========================
        // DYNAMIC FINDER
        // ==========================
        private WebElement findElementDynamic(By... locators) {

                for (By locator : locators) {

                        try {

                                WebElement element = new WebDriverWait(
                                                driver,
                                                Duration.ofSeconds(5))
                                                .until(ExpectedConditions.presenceOfElementLocated(locator));

                                if (element.isDisplayed()) {

                                        System.out.println(
                                                        "Locator worked : "
                                                                        + locator);

                                        return element;
                                }

                        } catch (Exception ignored) {
                        }
                }

                throw new RuntimeException(
                                "Element not found.");
        }

        // ==========================
        // SAFE CLICK
        // ==========================
        private void safeClick(WebElement element) {

                try {

                        wait.until(ExpectedConditions.elementToBeClickable(
                                        element));

                        element.click();

                } catch (Exception e) {

                        JavascriptExecutor js = (JavascriptExecutor) driver;

                        js.executeScript(
                                        "arguments[0].click();",
                                        element);
                }
        }

        // ==========================
        // VIEW PROFILE
        // ==========================
        public void clickViewProfile() {

                try {

                        WebElement profile = findElementDynamic(
                                        By.xpath("//a[contains(@href,'/mnjuser/profile')]"),
                                        By.xpath("//a[contains(.,'View profile')]"),
                                        By.cssSelector("a.view-profile"));

                        safeClick(profile);

                        System.out.println(
                                        "View Profile clicked.");

                } catch (Exception e) {

                        try {

                                WebElement body = driver.findElement(By.tagName("body"));

                                new Actions(driver)
                                                .moveToElement(body, 50, 50)
                                                .click()
                                                .perform();

                                Thread.sleep(2000);

                                WebElement profile = findElementDynamic(
                                                By.xpath("//a[contains(@href,'/mnjuser/profile')]"),
                                                By.xpath("//a[contains(.,'View profile')]"));

                                safeClick(profile);

                        } catch (Exception ex) {

                                throw new RuntimeException(
                                                "Failed to click View Profile",
                                                ex);
                        }
                }
        }

        // ==========================
        // CLICK UPDATE RESUME
        // ==========================
        public void clickUpdateResume() {

                try {

                        WebElement updateButton = findElementDynamic(
                                        By.xpath("//input[@value='Update resume']"),
                                        By.xpath("//button[contains(.,'Update resume')]"),
                                        By.xpath("//*[contains(text(),'Update resume')]"));

                        // ==========================
                        // JS CLICK
                        // ==========================
                        JavascriptExecutor js = (JavascriptExecutor) driver;

                        js.executeScript(
                                        "arguments[0].click();",
                                        updateButton);

                        System.out.println(
                                        "Update Resume clicked.");

                } catch (Exception e) {

                        throw new RuntimeException(
                                        "Failed to click Update Resume",
                                        e);
                }
        }

        // ==========================
        // UPLOAD RESUME
        // ==========================
        public void uploadResume(String filePath) {

                try {

                        Thread.sleep(5000);

                        WebElement uploadInput = null;

                        int attempts = 0;

                        while (attempts < 10) {

                                try {

                                        // ==========================
                                        // NORMAL LOCATORS
                                        // ==========================
                                        uploadInput = findElementDynamic(
                                                        By.xpath("//input[@type='file']"),
                                                        By.cssSelector("input[type='file']"),
                                                        By.xpath("//input[contains(@accept,'.pdf')]"),
                                                        By.xpath("//input[contains(@accept,'.doc')]"));

                                        if (uploadInput != null) {

                                                System.out.println(
                                                                "Upload input found normally.");

                                                break;
                                        }

                                } catch (Exception ignored) {
                                }

                                // ==========================
                                // JS FALLBACK
                                // ==========================
                                try {

                                        JavascriptExecutor js = (JavascriptExecutor) driver;

                                        uploadInput = (WebElement) js.executeScript(
                                                        "return document.querySelector('input[type=file]')");

                                        if (uploadInput != null) {

                                                System.out.println(
                                                                "Upload input found using JS.");

                                                break;
                                        }

                                } catch (Exception ignored) {
                                }

                                attempts++;

                                System.out.println(
                                                "Retrying upload input detection : "
                                                                + attempts);

                                Thread.sleep(2000);
                        }

                        // ==========================
                        // FINAL CHECK
                        // ==========================
                        if (uploadInput == null) {

                                System.out.println(
                                                driver.getPageSource());

                                throw new RuntimeException(
                                                "Upload input not found even after retries.");
                        }

                        // ==========================
                        // MAKE ELEMENT VISIBLE
                        // ==========================
                        JavascriptExecutor js = (JavascriptExecutor) driver;

                        js.executeScript(
                                        "arguments[0].style.display='block';",
                                        uploadInput);

                        js.executeScript(
                                        "arguments[0].style.visibility='visible';",
                                        uploadInput);

                        js.executeScript(
                                        "arguments[0].removeAttribute('hidden');",
                                        uploadInput);

                        // ==========================
                        // SCROLL INTO VIEW
                        // ==========================
                        js.executeScript(
                                        "arguments[0].scrollIntoView(true);",
                                        uploadInput);

                        Thread.sleep(1000);

                        // ==========================
                        // UPLOAD FILE
                        // ==========================
                        uploadInput.sendKeys(filePath);

                        System.out.println(
                                        "Resume uploaded successfully.");

                } catch (Exception e) {

                        throw new RuntimeException(
                                        "Resume upload failed.",
                                        e);
                }
        }
}
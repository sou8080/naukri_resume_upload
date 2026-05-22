package pom;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class login_page_pom {

        private final WebDriver driver;

        private final WebDriverWait wait;

        public login_page_pom(WebDriver driver) {

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
                                "Element not found using dynamic locators.");
        }

        // ==========================
        // LOGIN BUTTON
        // ==========================
        public void clickLoginButton() {

                WebElement loginButton = findElementDynamic(
                                By.xpath("//a[contains(text(),'Login')]"),
                                By.xpath("//a[contains(.,'Sign in')]"),
                                By.cssSelector("a[title='Jobseeker Login']"),
                                By.cssSelector("a.login"));

                safeClick(loginButton);
        }

        // ==========================
        // ENTER LOGIN DETAILS
        // ==========================
        public void enterLoginDetails(
                        String email,
                        String password) {

                WebElement emailField = findElementDynamic(
                                By.id("usernameField"),
                                By.name("email"),
                                By.cssSelector("input[type='email']"),
                                By.xpath("//input[contains(@placeholder,'Email')]"),
                                By.xpath("//input[contains(@placeholder,'Username')]"));

                emailField.clear();

                emailField.sendKeys(email);

                WebElement passwordField = findElementDynamic(
                                By.id("passwordField"),
                                By.name("password"),
                                By.cssSelector("input[type='password']"),
                                By.xpath("//input[contains(@placeholder,'Password')]"));

                passwordField.clear();

                passwordField.sendKeys(password);
        }

        // ==========================
        // CLICK SUBMIT
        // ==========================
        public void clickSubmit() {

                WebElement submitButton = findElementDynamic(
                                By.xpath("//button[contains(.,'Login')]"),
                                By.xpath("//button[contains(.,'Sign in')]"),
                                By.cssSelector("button[type='submit']"),
                                By.xpath("//input[@type='submit']"));

                safeClick(submitButton);
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
        // LOGIN SUCCESS CHECK
        // ==========================
        public boolean isLoginSuccessful() {

                try {

                        return driver.findElements(
                                        By.xpath("//a[contains(@href,'/mnjuser/profile')]"))
                                        .size() > 0;

                } catch (Exception e) {

                        return false;
                }
        }
}
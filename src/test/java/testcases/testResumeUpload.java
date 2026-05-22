package testcases;

import java.io.File;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import base.Test_Base_Class;
import pom.home_page_pom;
import pom.login_page_pom;
import utilities.SessionManager;
import utilities.constants;

public class testResumeUpload
                extends Test_Base_Class {

        @Test
        public void uploadResumeTest()
                        throws Exception {

                login_page_pom loginPage = new login_page_pom(driver);

                home_page_pom homePage = new home_page_pom(driver);

                // ==========================
                // TRY LOADING SESSION
                // ==========================
                boolean sessionLoaded = SessionManager.loadSession(driver);
                if (sessionLoaded) {
                        driver.navigate().refresh();
                        Thread.sleep(3000);
                }

                // ==========================
                // LOGIN CHECK
                // ==========================
                if (loginPage.isLoginSuccessful()) {

                        System.out.println(
                                        "Already logged in.");

                } else {

                        System.out.println(
                                        "Fresh login required.");

                        performFreshLogin(loginPage);

                        // ==========================
                        // SAVE SESSION COOKIES
                        // ==========================
                        SessionManager.saveSession(driver);
                }

                Thread.sleep(5000);

                // ==========================
                // PROFILE FLOW
                // ==========================
                homePage.clickViewProfile();

                Thread.sleep(5000);

                homePage.clickUpdateResume();

                // ==========================
                // WAIT FOR REACT RENDER
                // ==========================
                Thread.sleep(7000);

                // ==========================
                // FILE CHECK
                // ==========================
                File file = new File(constants.RESUME_PATH);

                System.out.println(
                                "Resume Exists : "
                                                + file.exists());

                if (!file.exists()) {

                        throw new RuntimeException(
                                        "Resume file not found.");
                }

                // ==========================
                // UPLOAD RESUME
                // ==========================
                homePage.uploadResume(
                                file.getAbsolutePath());

                Thread.sleep(5000);

                System.out.println(
                                "Resume upload test completed successfully.");
        }

        // ==========================
        // LOGIN FLOW
        // ==========================
        private void performFreshLogin(
                        login_page_pom loginPage)
                        throws Exception {

                WebDriverWait wait = new WebDriverWait(
                                driver,
                                Duration.ofSeconds(40));

                wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.tagName("body")));

                // ==========================
                // CLICK LOGIN
                // ==========================
                loginPage.clickLoginButton();

                System.out.println(
                                "Login button clicked.");

                Thread.sleep(3000);

                // ==========================
                // ENTER DETAILS
                // ==========================
                loginPage.enterLoginDetails(
                                constants.EMAIL,
                                constants.PASSWORD);

                // ==========================
                // SUBMIT LOGIN
                // ==========================
                loginPage.clickSubmit();

                // ==========================
                // WAIT LOGIN SUCCESS
                // ==========================
                wait.until(ExpectedConditions.or(
                                ExpectedConditions.urlContains(
                                                "mnjuser"),
                                ExpectedConditions.presenceOfElementLocated(
                                                By.xpath(
                                                                "//a[contains(@href,'/mnjuser/profile')]"))));

                System.out.println(
                                "Login successful.");
        }
}
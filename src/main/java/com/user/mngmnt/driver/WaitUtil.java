package com.user.mngmnt.driver;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.driver;

public class WaitUtil {

    public static boolean waitForJSandJQueryToLoad(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    Long r = (Long)((JavascriptExecutor)driver).executeScript("return $.active");
                    return r == 0;
                } catch (Exception e) {
                    System.out.println("no jquery present");
                    return true;
                }
            }
        };

        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor)driver).executeScript("return document.readyState")
                        .toString().equals("complete");
            }
        };

        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }
}

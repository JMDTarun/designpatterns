package com.user.mngmnt.driver;

import com.github.loyada.jdollarx.BasicPath;
import com.github.loyada.jdollarx.Operations;
import com.github.loyada.jdollarx.Path;
import com.user.mngmnt.model.RunnerExecution;
import com.user.mngmnt.model.RunnerExecutionStatus;
import com.user.mngmnt.repository.RunnerExecutionRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.loyada.jdollarx.BasicPath.anchor;
import static com.github.loyada.jdollarx.BasicPath.button;
import static com.github.loyada.jdollarx.BasicPath.div;
import static com.github.loyada.jdollarx.BasicPath.input;
import static com.github.loyada.jdollarx.BasicPath.select;
import static com.github.loyada.jdollarx.BasicPath.table;
import static com.github.loyada.jdollarx.BasicPath.td;
import static com.github.loyada.jdollarx.BasicPath.tr;
import static com.github.loyada.jdollarx.ElementProperties.hasAggregatedTextContaining;
import static com.github.loyada.jdollarx.ElementProperties.hasAncesctor;
import static com.github.loyada.jdollarx.ElementProperties.hasAttribute;
import static com.github.loyada.jdollarx.ElementProperties.hasId;
import static com.github.loyada.jdollarx.ElementProperties.hasName;
import static com.github.loyada.jdollarx.ElementProperties.hasParent;
import static com.github.loyada.jdollarx.ElementProperties.hasText;
import static com.github.loyada.jdollarx.ElementProperties.isNthSibling;
import static com.github.loyada.jdollarx.ElementProperties.isSiblingOf;
import static com.github.loyada.jdollarx.Operations.*;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.clickAt;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.clickOn;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.driver;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.find;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.sendKeys;
import static java.util.concurrent.TimeUnit.*;

@Component
public class FastwayRunner {

    public static final String URL = "http://bssnew.myfastway.in:9003/oapservice";
    public static final String USERNAME = "KAMALJIT.SINGH";
    public static final String PASSWORD = "Kamal@123";

    @Autowired
    private DriverConfig driverConfig;

    @Autowired
    private RunnerExecutionRepository runnerExecutionRepository;

    public void run() {
        // List<RunnerExecution> executions = runnerExecutionRepository.findByStatus(RunnerExecutionStatus.IN_PROGRESS);
        List<RunnerExecution> executions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(executions)) {
            throw new RuntimeException("A instance of runner execution is already in progress.");
        }

        RunnerExecution currentExcution = runnerExecutionRepository.save(RunnerExecution.builder()
                .status(RunnerExecutionStatus.IN_PROGRESS)
                .startTime(Instant.now())
                .build());
        try {

            //Get action performed data till time that are not processed

            login();
            searchDevice("56331345071201");
            addOrChangePack("HD", "BRONZE");

            //On execution complete
            currentExcution.setEndTime(Instant.now());
            currentExcution.setStatus(RunnerExecutionStatus.COMPLETED);

        } catch (Exception e) {
            e.printStackTrace();
            currentExcution.setStatus(RunnerExecutionStatus.ERROR);
            currentExcution.setErrorMsg(e.getMessage());
        } finally {
            runnerExecutionRepository.save(currentExcution);
            //close();
        }
    }

    private void login() throws IOException, URISyntaxException {
        driver = driverConfig.driver();
        driver.manage().timeouts().implicitlyWait(30, SECONDS).pageLoadTimeout(30, SECONDS);
        driver.get(URL);
        sendKeys(USERNAME).to(input.that(hasName("username")));
        sendKeys(PASSWORD).to(input.that(hasName("password")));
        clickAt(input.that(hasAttribute("type", "submit")));
    }

    public boolean waitForJSandJQueryToLoad() {
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
    
    private void addOrChangePack(String newPack, String existingPackIfAny) throws Exception {
        openShowDetails();
        if(!StringUtils.isEmpty(existingPackIfAny)) {
            cancelExistingPack(existingPackIfAny);
            openShowDetails();

        }
        openAddPlan();
        selectSubmitPack(newPack);
    }

    private void cancelExistingPack(String existingPack) throws OperationFailedException {
        perform(() -> clickOn(
                anchor
                    .that(hasAggregatedTextContaining("Cancel"))
                    .inside(td
                            .that(isSiblingOf(td
                                                .that(hasAggregatedTextContaining(existingPack))
                            ))
                            .descendantOf(table.that(hasId("service_plandetail1")))
                    )
        ));
        Path cancelModal = div.that(hasId("scheduledialog_cancel"));
        sendKeysWhenClickable(select.that(hasId("subscription-plan-cancel-select-reason")).inside(cancelModal), "Non-Payment");
        perform(() -> clickOn(button.that(hasAggregatedTextContaining("Submit")).inside(cancelModal)));
        perform(() -> clickOn(button.that(hasAggregatedTextContaining("Yes")).inside(div.withClass("bootbox-confirm"))));
    }

    public static  void perform(Runnable action){
        doWithRetries(action, 30, 500);
    }

    public static void sendKeysWhenClickable(Path path, String keys) throws OperationFailedException {
        WebElement found = find(path);
        Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(6, TimeUnit.SECONDS)
                .pollingEvery(100, TimeUnit.MILLISECONDS)
                .ignoring(java.util.NoSuchElementException.class);
        wait.until(ExpectedConditions.elementToBeClickable(found));
        sendKeys(keys).to(path);
    }

    private void searchDevice(String serialNumber) throws OperationFailedException {
        Path searchSection = div.withClass("summary_search");
        sendKeysWhenClickable(select.withClass("inner_custom").inside(searchSection), "serialno");
        sendKeysWhenClickable(input.withClass("nav-search-input").inside(searchSection), serialNumber);
        perform(() -> clickOn(button.withClass("btn btn-sm btn-danger btn-round").inside(searchSection)));
    }

    private void openShowDetails(){
        waitForJSandJQueryToLoad();
        perform(() -> clickOn(button.that(hasAggregatedTextContaining("Show Details")
                .and(hasAncesctor(tr.that(isNthSibling(0)
                        .and(hasAncesctor(table.that(hasId("activeservice"))))))))));
    }

    private void openAddPlan(){
        perform(() -> find(button.that(hasText("Add Plan").and(hasAncesctor(div.that(hasId("activeservice1")))))).click());
 }


    private void selectSubmitPack(String name) throws Exception {
       Path addPlanModal = div.that(hasId("add_change_plandialog"));
        sendKeysWhenClickable(select.that(hasName("subscription-plan-list-name").and(hasAncesctor(addPlanModal))), "SUGGESTIVE PACKS");
        sendKeysWhenClickable(select.that(hasName("subscription-plan-list-select-reason")).inside(addPlanModal), "Recovery");
        perform(() -> clickOn(tr.that(hasAggregatedTextContaining(name)).inside(table.that(hasId("subscription-plan-details")))));
        perform(() -> clickOn(button.that(hasAggregatedTextContaining("Submit")).inside(addPlanModal)));
    }

    private void close() {
        driver.quit();
    }


}

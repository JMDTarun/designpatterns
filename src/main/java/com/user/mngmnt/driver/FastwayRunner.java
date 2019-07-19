package com.user.mngmnt.driver;

import com.github.loyada.jdollarx.BasicPath;
import com.github.loyada.jdollarx.Operations;
import com.github.loyada.jdollarx.Path;
import com.user.mngmnt.model.RunnerExecution;
import com.user.mngmnt.model.RunnerExecutionStatus;
import com.user.mngmnt.repository.RunnerExecutionRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import static com.github.loyada.jdollarx.Operations.*;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.clickAt;
import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.driver;
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
            addPack("HD");

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

    private boolean login() throws IOException, URISyntaxException {
        driver = driverConfig.driver();
        driver.manage().timeouts().implicitlyWait(60, SECONDS).pageLoadTimeout(60, SECONDS);
        driver.get(URL);
        sendKeys(USERNAME).to(input.that(hasName("username")));
        sendKeys(PASSWORD).to(input.that(hasName("password")));
        clickAt(input.that(hasAttribute("type", "submit")));
        return true;
    }

    private boolean searchDevice(String serialNumber) throws OperationFailedException, InterruptedException {
        doWithRetries(() -> {
            try {
                Path searchSection = div.withClass("summary_search");
                sendKeys("serialno").to(select.withClass("inner_custom").inside(searchSection));
                sendKeys(serialNumber).to(input.withClass("nav-search-input").inside(searchSection));
                clickAt(button.withClass("btn btn-sm btn-danger btn-round").inside(searchSection));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 60, 1000);
        return true;
    }


    private boolean addPack(String name) throws InterruptedException, OperationFailedException, Exception {
        // Thread.sleep(3000);
        doWithRetries(() ->
                        clickAt(button.that(hasText("Show Details")
                                .and(hasAncesctor(tr.that(isNthSibling(0)
                                        .and(hasAncesctor(table.that(hasId("activeservice")))))))))
                , 60, 1000);
//    clickAt(button.that(hasText("Show Details")
//            .and(hasAncesctor(tr.that(isNthSibling(0)
//                    .and(hasAncesctor(table.that(hasId("activeservice")))))))));
//    Thread.sleep(3000);
        doWithRetries(() ->
                        clickAt(button.that(hasText("Add Plan").and(hasAncesctor(div.that(hasId("activeservice1"))))))
                , 60, 1000);
//    Thread.sleep(3000);

        doWithRetries(() -> {
                    Path addPlanModal = div.that(hasId("add_change_plandialog"));
                    try {
                        sendKeys("SUGGESTIVE PACKS").to(select.that(hasName("subscription-plan-list-name").and(hasAncesctor(addPlanModal))));
                        sendKeys("Recovery").to(select.that(hasName("subscription-plan-list-select-reason")).inside(addPlanModal));
                    } catch (OperationFailedException e) {
                        e.printStackTrace();
                    }
                }
                , 60, 1000);

//    Thread.sleep(3000);


//    Thread.sleep(1000);
        doWithRetries(() -> {
                    Path addPlanModal = div.that(hasId("add_change_plandialog"));
                    clickAt(button.that(hasAggregatedTextContaining("Submit")).inside(addPlanModal));
                }
                , 60, 1000);

        return true;
    }

    private void close() {
        driver.quit();
    }


}

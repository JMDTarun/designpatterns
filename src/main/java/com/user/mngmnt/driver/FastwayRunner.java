package com.user.mngmnt.driver;

import com.user.mngmnt.model.RunnerExecution;
import com.user.mngmnt.model.RunnerExecutionStatus;
import com.user.mngmnt.repository.RunnerExecutionRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.driver;

@Component
public class FastwayRunner{

  public static final String URL = "http://bssnew.myfastway.in:9003/oapservice";
  public static final String USERNAME = "";
  public static final String PASSWORD = "";


  @Autowired
  private DriverConfig driverConfig;

  @Autowired
  private RunnerExecutionRepository runnerExecutionRepository;

  public void run(){

    List<RunnerExecution> executions = runnerExecutionRepository.findByStatus(RunnerExecutionStatus.IN_PROGRESS);

    if(CollectionUtils.isNotEmpty(executions)){
      throw new RuntimeException("A instance of runner execution is already in progress.");
    }

    RunnerExecution currentExcution = runnerExecutionRepository.save(RunnerExecution.builder()
      .status(RunnerExecutionStatus.IN_PROGRESS)
      .startTime(Instant.now())
    .build());
    try {

      //Get action performed data till time that are not processed

      login();

      //On execution complete
      currentExcution.setEndTime(Instant.now());
      currentExcution.setStatus(RunnerExecutionStatus.COMPLETED);

    }
    catch (Exception e){
      e.printStackTrace();
      currentExcution.setStatus(RunnerExecutionStatus.ERROR);
      currentExcution.setErrorMsg(e.getMessage());
    }
    finally {
      runnerExecutionRepository.save(currentExcution);
      close();
    }
  }

  private boolean login() throws IOException, URISyntaxException {
    driver = driverConfig.driver();
    driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
    driver.get(URL);
    return true;
  }

  private void close(){
    driver.quit();
  }




}

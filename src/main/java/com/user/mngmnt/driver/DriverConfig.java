package com.user.mngmnt.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.github.loyada.jdollarx.singlebrowser.InBrowserSinglton.driver;
import static java.util.concurrent.TimeUnit.SECONDS;

//@Configuration
@Component
public class DriverConfig {

    private File driverFile;

    @PostConstruct
    public void extractDrivver() throws IOException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("chromedriver.exe");
        File f = new File("Driver");
        if (!f.exists()) {
            f.mkdirs();
        }
        driverFile= new File("Driver" + File.separator + "chromedriver.exe");
        if (!driverFile.exists()) {
            driverFile.createNewFile();
        }
        Files.copy(Paths.get(resource.toURI()), driverFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // @Bean
    public WebDriver driver() {

      //System.setProperty("webdriver.chrome.driver", driverFile.getAbsolutePath());
      System.setProperty("webdriver.chrome.driver", "/Users/rohitsrivastava/Codebase/user-management-master/src/main/resources/chromedriver");

      ChromeOptions chromeOptions = new ChromeOptions();
      //chromeOptions.addArguments("headless");
      WebDriver driver = new ChromeDriver(chromeOptions);
      driver.manage().timeouts().implicitlyWait(30, SECONDS).pageLoadTimeout(60, SECONDS);
      return driver;
    }
}

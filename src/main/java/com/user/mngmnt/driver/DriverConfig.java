package com.user.mngmnt.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//@Configuration
@Component
public class DriverConfig {

    // @Bean
    public WebDriver driver() throws IOException, URISyntaxException {
      ClassLoader classLoader = getClass().getClassLoader();
      URL resource = classLoader.getResource("chromedriver.exe");
      File f = new File("Driver");
      if (!f.exists()) {
        f.mkdirs();
      }
      File chromeDriver = new File("Driver" + File.separator + "chromedriver.exe");
      if (!chromeDriver.exists()) {
        chromeDriver.createNewFile();
      }
      Files.copy(Paths.get(resource.toURI()), chromeDriver.toPath(), StandardCopyOption.REPLACE_EXISTING);
      System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());

      ChromeOptions chromeOptions = new ChromeOptions();
      //chromeOptions.addArguments("headless");
      return new ChromeDriver(chromeOptions);
    }
}

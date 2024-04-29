package resume.config;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

/**
 * 浏览器配置
 *
 * @author：周杰
 * @date: 2024/4/25
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class WebDriverConfig {

    /**
     * 呼起浏览器驱动
     */
    public static WebDriver openWebDriver(){
        // 设置 GeckoDriver 路径
        // System.setProperty("webdriver.gecko.driver", "/Users/zhoujie/Desktop/geckodriver");
        // 创建 Firefox 浏览器实例
        FirefoxProfile myprofile = new FirefoxProfile(new File("/Users/zhoujie/Library/Application Support/Firefox/Profiles/xgjmfsmz.default-release"));
        FirefoxOptions options = new FirefoxOptions();
        //   options.setHeadless(true); // 无头模式（可选）
        options.setBinary("/Applications/Firefox.app/Contents/MacOS/firefox");
        options.setProfile(myprofile);
   //     options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        WebDriver driver = new FirefoxDriver(options);
        //   driver.manage().window().maximize();//窗口最大化
        // 打开网页
        driver.get("https://employer.58.com/main/resumesearch");
        return driver;
    }

}

package resume.script;

import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import resume.config.WebDriverConfig;

import java.util.concurrent.CompletableFuture;

/**
 * @Description: 智联招聘简历
 * @author: 周杰
 * @date: 2024/5/15 星期三
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
public class ZlCrawlerResume {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        WebDriver driver = null;
        CompletableFuture<Void> voidCompletableFuture = null;
        try {
            //呼起浏览器操作
            driver = WebDriverConfig.openWebDriver();
            // 打开网页
            driver.get("https://www.zhaopin.com/");

        } catch (Exception e){
            e.printStackTrace();
        }


    }

}

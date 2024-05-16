package resume.script;

import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import resume.api.ApiZl;
import resume.config.WebDriverConfig;
import resume.script.split.ReptileResumeSplit;

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
            //获个人账号
            WebElement usernameElement = driver.findElement(By.className("user-name"));
            //心跳
            logger.info("账号名称：{}", usernameElement.getText());
            voidCompletableFuture = ReptileResumeSplit.openHeartBeat(usernameElement.getText(), 2);
            ApiZl.getZlTime();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            assert driver != null;
            driver.quit();
            //关闭心跳
            assert voidCompletableFuture != null;
            ReptileResumeSplit.closeHeartBeat(voidCompletableFuture);
        }


    }

}

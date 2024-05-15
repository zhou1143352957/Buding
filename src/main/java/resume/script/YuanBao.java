package resume.script;

import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import resume.config.WebDriverConfig;
import resume.script.split.ReptileResumeSplit;
import resume.script.split.YuanBaoSplit;

import java.util.concurrent.CompletableFuture;

/**
 * @Description: 58元宝信息获取
 * @author: 周杰
 * @date: 2024/5/14 星期二
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
public class YuanBao {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        WebDriver driver = null;
        CompletableFuture<Void> voidCompletableFuture = null;
        try {
            //呼起浏览器操作
            driver = WebDriverConfig.openWebDriver();
            driver.manage().window().maximize();//窗口最大化
            // 打开网页
            driver.get("https://employer.58.com/index");
            //获个人账号
            WebElement usernameElement = driver.findElement(By.className("user-name"));
            //心跳
            logger.info("58元宝脚本，执行账号名称：{}", usernameElement.getText());
            voidCompletableFuture = ReptileResumeSplit.openHeartBeat(usernameElement.getText());
            YuanBaoSplit.yuanBaoPage(driver, usernameElement.getText());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert driver != null;
            driver.quit();
            //关闭心跳
            assert voidCompletableFuture != null;
            ReptileResumeSplit.closeHeartBeat(voidCompletableFuture);
        }
    }

}

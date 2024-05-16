package resume.script;

import com.sun.tools.javac.Main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import resume.config.WebDriverConfig;
import resume.entity.dto.VirtualConfig58DTO;
import resume.api.Api58;
import resume.script.split.ReptileResumeSplit;
import resume.util.CommonUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

/**
 * 爬取58简历信息
 *
 * @author：周杰
 * @date: 2024/4/25
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class CrawlerResume {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        WebDriver driver = null;
        CompletableFuture<Void> voidCompletableFuture = null;
        try {
            //呼起浏览器操作
            driver = WebDriverConfig.openWebDriver();
            // 打开网页
            driver.get("https://employer.58.com/main/resumesearch");
            //获个人账号
            WebElement usernameElement = driver.findElement(By.className("user-name"));
            //获取 58 配置信息
            VirtualConfig58DTO accountInfo = Api58.get58AccountInfo(usernameElement.getText());
            sleep(CommonUtil.getRandomMillisecond());
            //触发点击事件
            JavascriptExecutor cateSearchJs = (JavascriptExecutor) driver;
            sleep(CommonUtil.getRandomMillisecond(3, 6));
            //心跳
            logger.info("账号名称：{}", usernameElement.getText());
            voidCompletableFuture = ReptileResumeSplit.openHeartBeat(usernameElement.getText(), 1);
            //头部筛选框 操作代码
            ReptileResumeSplit.searchItem(driver, accountInfo, cateSearchJs);
            //简历处理部分
            while (true) {
                int size = ReptileResumeSplit.resumePart(driver, accountInfo, cateSearchJs);
                if (size < 50) {
                    break;
                }
                sleep(CommonUtil.getRandomMillisecond());
                //分页元素
                WebElement resumePage = driver.findElement(By.className("resume-page"));
                logger.info("resume-page元素数量：{}", driver.findElements(By.className("resume-page")).size());
                //下一页
                List<WebElement> antBtnLink = resumePage.findElements(By.className("ant-btn-link"));
                logger.info("antBtnLink次数 : {}", antBtnLink.size());
                antBtnLink.get(1).click();
                //下一页之后 点击置顶
                sleep(CommonUtil.getRandomMillisecond());
                driver.findElements(By.className("quick-entry-content-item")).get(3).click();
            }
            driver.close();
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

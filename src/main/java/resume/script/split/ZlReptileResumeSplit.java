package resume.script.split;

import com.sipaote.common.exception.BusinessException;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import resume.api.ApiZl;
import resume.entity.dto.ZlVirtualConfigDTO;
import resume.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

/**
 * 智联招聘 外调方法
 *
 * @author：周杰
 * @date: 2024/5/17
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class ZlReptileResumeSplit {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * 智联基本简历方法
     *
     * @author: 周杰
     * @date: 2024/5/18
     * @version: 1.0.0
     */
    public static void searchItem(WebDriver driver, JavascriptExecutor cateSearchJs) {
        CompletableFuture<Void> voidCompletableFuture = null;
        try {
            //验证账号是否登陆
            sleep(CommonUtil.getRandomMillisecond());
            WebDriver appIndexWebDriver = isLogin(driver);
            sleep(CommonUtil.getRandomMillisecond());
            Actions appIndexWebActions = new Actions(appIndexWebDriver);
            //获取个人账号
            String userName = getUserName(appIndexWebDriver, appIndexWebActions);
            //心跳
            logger.info("账号名称：{}", userName);
            voidCompletableFuture = ReptileResumeSplit.openHeartBeat(userName, 2);
            //获取账号配置信息
            ZlVirtualConfigDTO zlVirtualConfigDTO = ApiZl.ziConfig(userName);


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            //关闭心跳
            assert voidCompletableFuture != null;
            ReptileResumeSplit.closeHeartBeat(voidCompletableFuture);
        }

    }


    /**
     * 验证是否登陆
     *
     * @author: 周杰
     * @date: 2024/5/18
     * @version: 1.0.0
     */
    private static WebDriver isLogin(WebDriver driver) {
        try {
            List<WebElement> kmTabNavInners = driver.findElements(By.className("km-tab__nav-inner"));
            if (kmTabNavInners.isEmpty()) {
                //登陆成功后打开app/index页面
                driver.switchTo().newWindow(WindowType.TAB);
                driver.get("https://rd6.zhaopin.com/app/index");
                return driver;
            }
            String text = kmTabNavInners.get(1).getText();
            if (!"账号登陆".equals(text)) {
                return driver;
            }
            WebElement kmTabContent = driver.findElement(By.className("km-tab__content"));
            List<WebElement> kmIcons = kmTabContent.findElements(By.className("km-icon"));
            kmIcons.get(1).click();
            sleep(CommonUtil.getRandomMillisecond());
            //获取企业登陆按钮
            driver.findElement(By.className("rd6-account-login__button--inner")).click();
            //登陆成功后打开app/index页面
            WebDriver appIndexDriver = driver.switchTo().newWindow(WindowType.TAB);
            appIndexDriver.get("https://rd6.zhaopin.com/app/index");

            return appIndexDriver;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 获取智联账号个人信息
     *
     * @author: 周杰
     * @date: 2024/5/18
     * @version: 1.0.0
     */
    public static String getUserName(WebDriver appIndexWebDriver, Actions appIndexWebActions) throws Exception {
        String appIndexOriginalWindow = appIndexWebDriver.getWindowHandle();
        WebElement accountEntry = appIndexWebDriver.findElement(By.className("account-entry"));
        appIndexWebActions.moveToElement(accountEntry).pause(CommonUtil.getRandom()).perform();
        sleep(CommonUtil.getRandomMillisecond());

        List<WebElement> kmPopoverInners = appIndexWebDriver.findElements(By.className("km-popover__inner"));
        if (kmPopoverInners.isEmpty()) {
            throw new RuntimeException("账号名称获取异常");
        }
        WebElement kmPopoverInner = kmPopoverInners.get(0);
        List<WebElement> linkItems = kmPopoverInner.findElements(By.className("link-item"));
        //个人信息
        linkItems.get(0).click();

        // 获取所有窗口句柄
        List<String> tabs = new ArrayList<>(appIndexWebDriver.getWindowHandles());
        // 切换到新打开的选项卡
        appIndexWebDriver.switchTo().window(tabs.get(tabs.size() - 1));
        sleep(CommonUtil.getRandomMillisecond());
        List<WebElement> kmFormItemContents = appIndexWebDriver.findElements(By.className("km-form-item__content"));
        //获取第二个用户名
        WebElement userNameElement = kmFormItemContents.get(1);
        String userName = userNameElement.getText();
        //获取到用户名称后, 关闭选项卡, 切换到原来跳转的 选项卡
        appIndexWebDriver.close();
        appIndexWebDriver.switchTo().window(appIndexOriginalWindow);
        return userName;

    }


}

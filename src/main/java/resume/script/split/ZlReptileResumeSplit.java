package resume.script.split;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sipaote.common.validator.ValidatorUtil;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
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
            //获取 当前智联币 和 今日剩余邀请投敌次数
            ZlAppIndexSplit.getZlbAndSize(userName, appIndexWebDriver);
            //推荐人才
            ZlReferTalentsSplit.searchItem(userName, appIndexWebDriver, appIndexWebActions);

            //搜索人才
         //   appSearchResume(userName, appIndexWebDriver, appIndexWebActions);

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

    /**
     * 智联搜索人才
     *
     * @author: 周杰
     * @date: 2024/5/20
     * @version: 1.0.0
     */
    public static void appSearchResume(String account, WebDriver driver, Actions appIndexWebActions) throws InterruptedException {
        //获取账号配置信息
        ZlVirtualConfigDTO zlVirtualConfigDTO = ApiZl.ziConfig(account);
        if (ValidatorUtil.isNull(zlVirtualConfigDTO.getContent())) {
            return;
        }
        List<WebElement> appNavItems = driver.findElements(By.className("app-nav__item"));
        appIndexWebActions.pause(CommonUtil.getRandom()).moveToElement(appNavItems.get(3)).pause(CommonUtil.getRandom()).click().perform();
        sleep(CommonUtil.getRandomMillisecond());
        //定位城市 筛选框元素
        WebElement keywordPanelCity = driver.findElement(By.className("keyword-panel-city"));
        appIndexWebActions.moveToElement(keywordPanelCity).click().perform();
        //配置信息
        JSONObject jsonObject = JSON.parseObject(zlVirtualConfigDTO.getContent());
        //城市
        String[] cityArr = JSON.parseObject(jsonObject.getString("city_arr"), String[].class);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cityArr.length; i++) {
            sb.append(cityArr[i]);
            if (i < cityArr.length - 1) {
                sb.append("、");
            }
        }
        //定位 到城市输入框
        List<WebElement> sinputInners = driver.findElements(By.className("s-input__inner"));
        appIndexWebActions.sendKeys(sinputInners.get(sinputInners.size() - 1), sb.toString()).pause(CommonUtil.getRandom()).perform();
        sleep(CommonUtil.getRandomMillisecond());
        List<WebElement> sOptions = driver.findElements(By.className("s-option"));
        appIndexWebActions.moveToElement(sOptions.get(0)).pause(CommonUtil.getRandom()).click().perform();
        sleep(CommonUtil.getRandomMillisecond());
        //确定
        List<WebElement> sButtons = driver.findElements(By.className("s-button"));
        sButtons.get(23).click();

        sleep(CommonUtil.getRandomMillisecond());
        //筛选年龄
        List<WebElement> searchLabelWrapperNewContents = driver.findElements(By.className("search-label-wrapper-new__content"));
        //年龄
        WebElement searchLabelAge = searchLabelWrapperNewContents.get(1);
        List<WebElement> buttonGroupItemList = searchLabelAge.findElements(By.className("button-group__list-item"));
        for (WebElement ageItem : buttonGroupItemList) {
            if (!"自定义".equals(ageItem.getText())) {
                continue;
            }
            appIndexWebActions.moveToElement(ageItem).click().pause(CommonUtil.getRandom()).perform();
            break;
        }
        List<WebElement> searchSelectTwos = driver.findElements(By.className("search-select-two-new"));
        WebElement searchSelectAgeInput = searchSelectTwos.get(1);
        WebElement startAgeInput = searchSelectAgeInput.findElement(By.className("search-select-two-new__start"));
        appIndexWebActions.moveToElement(startAgeInput).click().pause(CommonUtil.getRandom()).perform();
        //最小年龄
        String ageMin = jsonObject.getString("age_min");
        getAgeInput(driver, ageMin, appIndexWebActions, 1);
        sleep(CommonUtil.getRandomMillisecond());
        //最大年龄
        String ageMax = jsonObject.getString("age_max");
        WebElement endAgeInput = searchSelectAgeInput.findElement(By.className("search-select-two-new__end"));
        appIndexWebActions.moveToElement(endAgeInput).click().pause(CommonUtil.getRandom()).perform();
        getAgeInput(driver, ageMax, appIndexWebActions, 2);




    }

    /**
     * 筛选年龄下滑框
     */
    private static void getAgeInput(WebDriver driver, String age, Actions appIndexWebActions, int index) throws InterruptedException {
        List<WebElement> kmScrollbarViews = driver.findElements(By.className("km-scrollbar__view"));
        WebElement kmScrollbarView = kmScrollbarViews.get(index);
        List<WebElement> startKmAgeOptions = kmScrollbarView.findElements(By.className("km-option"));
        if (Integer.parseInt(age) >= 44){
            for (WebElement startOrEndAge : startKmAgeOptions) {
                if (!startOrEndAge.getText().contains(age)) {
                    continue;
                }
                sleep(CommonUtil.getRandomMillisecond());
                appIndexWebActions.moveToElement(startOrEndAge).click().perform();
                return;
            }
        }
        //最小 / 最大 年龄
        for (int i = 0; i < startKmAgeOptions.size(); i++) {
            WebElement startOrEndAge = startKmAgeOptions.get(i);
            if (!startOrEndAge.getText().contains(age)) {
                WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement(startOrEndAge);
                appIndexWebActions.moveToElement(startOrEndAge).scrollFromOrigin(scrollOrigin, 0, i > 10 ? 8 + i : 35 + i).perform();
                continue;
            }
            appIndexWebActions.moveToElement(startOrEndAge).pause(CommonUtil.getRandom()).click().perform();
            break;
        }
    }


}

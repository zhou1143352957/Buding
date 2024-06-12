package resume.script.split;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import resume.entity.vo.ZlIndexInfoVO;
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
            ZlIndexInfoVO zlbAndSize = ZlAppIndexSplit.getZlbAndSize(userName, appIndexWebDriver);

            //获取账号配置信息
            ZlVirtualConfigDTO zlVirtualConfigDTO = ApiZl.ziConfig(userName);
            if (ValidatorUtil.isNull(zlVirtualConfigDTO.getContent())) {
                return;
            }
            // 脚本类型1->推荐人才，2->搜索人才
            if (zlVirtualConfigDTO.getType().equals(1)) {
                //推荐人才
                ZlReferTalentsSplit.searchItem(zlVirtualConfigDTO, appIndexWebDriver, appIndexWebActions, zlbAndSize, cateSearchJs);
            } else {
                //搜索人才
                appSearchResume(zlVirtualConfigDTO, appIndexWebDriver, appIndexWebActions, zlbAndSize, cateSearchJs);
            }
            //聊天功能
            ZlChatSplit.zlChatIm(appIndexWebDriver, appIndexWebActions, cateSearchJs);
            //人才管理
            CandicateSplit.candicate(driver, cateSearchJs, appIndexWebActions, zlVirtualConfigDTO);

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
        sleep(CommonUtil.getRandomMillisecond(12, 15));

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
    public static void appSearchResume(ZlVirtualConfigDTO zlVirtualConfigDTO, WebDriver driver, Actions appIndexWebActions, ZlIndexInfoVO zlbAndSize, JavascriptExecutor cateSearchJs) throws InterruptedException {
        List<WebElement> appNavItems = driver.findElements(By.className("app-nav__item"));
        appIndexWebActions.pause(CommonUtil.getRandom()).moveToElement(appNavItems.get(3)).pause(CommonUtil.getRandom()).click().perform();
        sleep(CommonUtil.getRandomMillisecond());

        cateSearchJs.executeScript("if (document.getElementsByClassName(\"km-modal km-modal--open km-modal--v-centered km-modal--normal km-modal--no-icon\").length > 0 ) {" +
                "                    document.getElementsByClassName(\"km-modal km-modal--open km-modal--v-centered km-modal--normal km-modal--no-icon\")[" +
                "                    0].getElementsByClassName(\"km-ripple\")[1].click()" +
                "            }");
        sleep(CommonUtil.getRandomMillisecond());

        //  预留10个打招呼次数，用于人工操作
        int totalSize = Integer.parseInt(zlbAndSize.getSize());
        if (totalSize <= 10) {
            return;
        }
        logger.info("------今日可用剩余{}次------", totalSize);
        //筛选条件设置
        searchSetting(zlVirtualConfigDTO, driver, appIndexWebActions, cateSearchJs);
        //简历列表爬取
        getResumeList(driver, appIndexWebActions, cateSearchJs);

    }


    /**
     * 筛选条件配置
     */
    private static void searchSetting(ZlVirtualConfigDTO zlVirtualConfigDTO, WebDriver driver, Actions appIndexWebActions, JavascriptExecutor cateSearchJs) throws InterruptedException {
        JSONArray contentJsonArray = JSON.parseArray(zlVirtualConfigDTO.getContent());
        for (int a = 0; a < contentJsonArray.size(); a++) {
            JSONObject contentJsonObject = contentJsonArray.getJSONObject(a);

            //期望城市
            JSONArray cityArr = contentJsonObject.getJSONArray("hope_city_arr");
            if (!cityArr.isEmpty()) {
                driver.findElement(By.className("keyword-panel__city")).click();
                String[] citys = JSON.parseObject(String.valueOf(cityArr), String[].class);
                for (int i = 0; i < citys.length; i++) {
                    String city = citys[i];
                    //定位 到城市输入框
                    List<WebElement> sinputInners = driver.findElements(By.className("s-input__inner"));
                    appIndexWebActions.sendKeys(sinputInners.get(sinputInners.size() - 1), city).pause(CommonUtil.getRandom()).perform();
                    sleep(CommonUtil.getRandomMillisecond());

                    List<WebElement> sOptions = driver.findElements(By.className("s-option"));
                    appIndexWebActions.moveToElement(sOptions.get(0)).pause(CommonUtil.getRandom()).click().perform();
                    sleep(CommonUtil.getRandomMillisecond());
                }
                //确定
                List<WebElement> sButtons = driver.findElements(By.className("s-button"));
                sButtons.get(23).click();
                sleep(CommonUtil.getRandomMillisecond());
            }
            //筛选年龄
            if (ValidatorUtil.isNotNull(contentJsonObject.getString("age_min")) && ValidatorUtil.isNotNull(contentJsonObject.getString("age_max"))) {
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
                String ageMin = contentJsonObject.getString("age_min");
                getAgeInput(driver, ageMin, appIndexWebActions, 1, cateSearchJs);
                sleep(CommonUtil.getRandomMillisecond());
                //最大年龄
                String ageMax = contentJsonObject.getString("age_max");
                WebElement endAgeInput = searchSelectAgeInput.findElement(By.className("search-select-two-new__end"));
                appIndexWebActions.moveToElement(endAgeInput).click().perform();
                getAgeInput(driver, ageMax, appIndexWebActions, 2, cateSearchJs);
                sleep(CommonUtil.getRandomMillisecond());
            }
            //筛选行业
            if (ValidatorUtil.isNotNull(contentJsonObject.getString("hope_industry_arr"))) {
                JSONArray hopeIndustryArrs = contentJsonObject.getJSONArray("hope_industry_arr");
                List<WebElement> industrys = driver.findElements(By.className("industry"));
                appIndexWebActions.moveToElement(industrys.get(1)).click().perform();
                sleep(CommonUtil.getRandomMillisecond());

                String[] industryArray = JSON.parseObject(hopeIndustryArrs.toJSONString(), String[].class);
                for (int i = 0; i < industryArray.length; i++) {
                    String industry = industryArray[i];

                    List<WebElement> regionSearchs = driver.findElements(By.className("s-region__search"));
                    // 定位到输入框
                    appIndexWebActions.sendKeys(regionSearchs.get(regionSearchs.size() - 1), industry).pause(CommonUtil.getRandom()).perform();
                    sleep(CommonUtil.getRandomMillisecond());

                    List<WebElement> sOptions = driver.findElements(By.className("s-option"));
                    appIndexWebActions.moveToElement(sOptions.get(0)).pause(CommonUtil.getRandom()).click().perform();
                    sleep(CommonUtil.getRandomMillisecond());
                }
                //确定
                List<WebElement> sButtons = driver.findElements(By.className("s-button"));
                sButtons.get(23).click();
                sleep(CommonUtil.getRandomMillisecond());
            }
            //性别
            String sex = contentJsonObject.getString("sex");
            if (ValidatorUtil.isNotNull(sex) && !sex.contains("不限")) {
                cateSearchJs.executeScript(" document.getElementsByClassName(\"filter-other__item\")[1].children[0].click()");
                sleep(CommonUtil.getRandomMillisecond());

                Integer maxOptions = Integer.valueOf((cateSearchJs.executeScript("return document.getElementsByClassName(\"km-select__options\").length - 1")).toString());

                List<String> itemArr = (List<String>) cateSearchJs.executeScript(" var item = []" +
                        "                        var option = document.getElementsByClassName(\"km-select__options\")[" + maxOptions + "].getElementsByClassName(\"km-option\")" +
                        "                        for (var i=0; i<option.length; i++) {" +
                        "                            item.push(option[i].innerText)" +
                        "                        }" +
                        "                        return item");
                if (itemArr.contains(sex)) {
                    int num = itemArr.indexOf(sex);
                    cateSearchJs.executeScript(
                            "document.getElementsByClassName('km-select__options')[" + maxOptions + "].getElementsByClassName('km-option')[" + num + "].click();"
                    );
                    sleep(CommonUtil.getRandomMillisecond());
                }
            }
            //学历要求（自定义）
            cateSearchJs.executeScript("document.getElementsByClassName(\"search-education-new-custom__label\")[0].click()");
            sleep(CommonUtil.getRandomMillisecond());
            String educationMin = contentJsonObject.getString("education_min");
            //最低学历
            if (ValidatorUtil.isNotNull(educationMin) && !educationMin.contains("不限")) {
                cateSearchJs.executeScript("document.getElementsByClassName(\"search-education-new-custom__select\")[0].getElementsByClassName(\"km-input__original\")[0].click()");
                sleep(CommonUtil.getRandomMillisecond());
                Integer maxOptions = Integer.valueOf((cateSearchJs.executeScript("return document.getElementsByClassName(\"km-select__options\").length - 1")).toString());
                List<String> itemArr = (List<String>) cateSearchJs.executeScript(" var item = []" +
                        "                        var option = document.getElementsByClassName(\"km-select__options\")[\" + maxOptions + \"].getElementsByClassName(\"km-option\")" +
                        "                        for (var i=0; i<option.length; i++) {" +
                        "                            item.push(option[i].innerText)" +
                        "                        }" +
                        "                        return item");
                if (itemArr.contains(educationMin)) {
                    int num = itemArr.indexOf(educationMin);
                    cateSearchJs.executeScript(
                            "document.getElementsByClassName('km-select__options')[" + maxOptions + "].getElementsByClassName('km-option')[" + num + "].click();"
                    );
                    sleep(CommonUtil.getRandomMillisecond());
                }
            }
            //最高学历
            String educationMax = contentJsonObject.getString("education_max");
            if (ValidatorUtil.isNotNull(educationMax) && !educationMax.contains("不限")) {
                cateSearchJs.executeScript(" document.getElementsByClassName(\"search-education-new-custom__select\")[0].getElementsByClassName(\"km-input__original\")[1].click()");
                sleep(CommonUtil.getRandomMillisecond());
                Integer maxOptions = Integer.valueOf((cateSearchJs.executeScript("return document.getElementsByClassName(\"km-select__options\").length - 1")).toString());
                List<String> itemArr = (List<String>) cateSearchJs.executeScript("var item = []" +
                        "                        var option = document.getElementsByClassName(\"km-select__options\")[%d].getElementsByClassName(\"km-option\")" +
                        "                        for (var i=0; i<option.length; i++) {" +
                        "                            item.push(option[i].innerText)" +
                        "                        }" +
                        "                        return item");

                if (itemArr.contains(educationMax)) {
                    int num = itemArr.indexOf(educationMax);
                    cateSearchJs.executeScript(
                            "document.getElementsByClassName('km-select__options')[" + maxOptions + "].getElementsByClassName('km-option')[" + num + "].click();"
                    );
                    sleep(CommonUtil.getRandomMillisecond());
                }
            }
            //经验要求（自定义）
            cateSearchJs.executeScript("document.getElementsByClassName(\"search-work-year-new\")[0].getElementsByClassName(\"button-group__list-item\")[5].click()");
            sleep(CommonUtil.getRandomMillisecond());

            int experienceMinValue = 0;
            String experienceMin = contentJsonObject.getString("experience_min");
            if (ValidatorUtil.isNotNull(experienceMin) && experienceMin.matches("\\d+")) {
                experienceMinValue = Integer.parseInt(experienceMin);
            }
            if (experienceMinValue > 0) {
                cateSearchJs.executeScript(
                        "document.getElementsByClassName('search-work-year-new')[0].getElementsByClassName('km-input__original')[0].click();"
                );
                sleep(CommonUtil.getRandomMillisecond());

                int maxOptions = ((Long) cateSearchJs.executeScript("return document.getElementsByClassName('km-select__options').length - 1;")).intValue();

                List<String> itemArr = (List<String>) cateSearchJs.executeScript(
                        "var item = [];" +
                                "var option = document.getElementsByClassName('km-select__options')[" + maxOptions + "].getElementsByClassName('km-option');" +
                                "for (var i = 0; i < option.length; i++) {" +
                                "    item.push(option[i].innerText);" +
                                "}" +
                                "return item;"
                );
                String experienceMinStr = experienceMinValue + "年";
                if (itemArr.contains(experienceMinStr)) {
                    int num = itemArr.indexOf(experienceMinStr);
                    cateSearchJs.executeScript(
                            "document.getElementsByClassName('km-select__options')[" + maxOptions + "].getElementsByClassName('km-option')[" + num + "].click();"
                    );
                    sleep(CommonUtil.getRandomMillisecond());
                }
            }
            //最大经验
            int experienceMaxValue = 0;
            String experienceMax = contentJsonObject.getString("experience_max");
            if (ValidatorUtil.isNotNull(experienceMax) && experienceMax.matches("\\d+")) {
                experienceMaxValue = Integer.parseInt(experienceMax);
            }
            if (experienceMaxValue > 0) {
                cateSearchJs.executeScript(
                        "document.getElementsByClassName('search-work-year-new')[0].getElementsByClassName('km-input__original')[1].click();"
                );
                sleep(CommonUtil.getRandomMillisecond());

                int maxOptions = ((Long) cateSearchJs.executeScript("return document.getElementsByClassName('km-select__options').length - 1;")).intValue();

                List<String> itemArr = (List<String>) cateSearchJs.executeScript(
                        "var item = [];" +
                                "var option = document.getElementsByClassName('km-select__options')[" + maxOptions + "].getElementsByClassName('km-option');" +
                                "for (var i = 0; i < option.length; i++) {" +
                                "    item.push(option[i].innerText);" +
                                "}" +
                                "return item;"
                );
                String experienceMaxStr = experienceMaxValue + "年";
                if (itemArr.contains(experienceMaxStr)) {
                    int num = itemArr.indexOf(experienceMaxStr);
                    cateSearchJs.executeScript(
                            "document.getElementsByClassName('km-select__options')[" + maxOptions + "].getElementsByClassName('km-option')[" + num + "].click();"
                    );
                    sleep(CommonUtil.getRandomMillisecond());
                }
            }
            //院校要求
            String schoolArrString = contentJsonObject.getString("school_arr");
            if (ValidatorUtil.isNotNull(schoolArrString) && !schoolArrString.contains("不限")) {
                List<String> itemArr = (List<String>) cateSearchJs.executeScript(" var item = []" +
                        "                        var option = document.getElementsByClassName(\"search-school-nature-new\")[0].getElementsByClassName(\"search-school-nature-new__item\")" +
                        "                        for (var i=0; i<option.length; i++) {" +
                        "                            item.push(option[i].innerText)" +
                        "                        }" +
                        "                        return item");

                if (itemArr.contains(schoolArrString)) {
                    int num = itemArr.indexOf(schoolArrString);
                    cateSearchJs.executeScript(
                            "document.getElementsByClassName(\"search-school-nature-new\")[0].getElementsByClassName(\"search-school-nature-new__item\")[" + num + "].click()"
                    );
                    sleep(CommonUtil.getRandomMillisecond());
                }
            }
            //搜素关键词
            String searchKeyword = contentJsonObject.getString("search_keyword");
            if (ValidatorUtil.isNotNull(searchKeyword)) {
                WebElement keyWordPanelInput = driver.findElement(By.className("keyword-panel__input"));
                appIndexWebActions.sendKeys(keyWordPanelInput, searchKeyword).pause(CommonUtil.getRandom()).perform();
                sleep(CommonUtil.getRandomMillisecond());
                cateSearchJs.executeScript("document.getElementsByClassName(\"keyword-panel__input\")[0].getElementsByTagName(\"button\")[0].click()");
                sleep(CommonUtil.getRandomMillisecond());
            }
        }
    }

    /**
     * 获取简历列表
     */
    private static void getResumeList(WebDriver driver, Actions actions, JavascriptExecutor js) {
        Integer resumeSize = (Integer) js.executeScript("return document.getElementsByClassName(\"search-resume-list\").length");
        if (resumeSize.equals(0)){
            logger.info("------没有符合条件的人才------");
            return;
        }
        


    }


    /**
     * 筛选年龄下滑框
     */
    private static void getAgeInput(WebDriver driver, String age, Actions appIndexWebActions, int index, JavascriptExecutor js) throws InterruptedException {
        if (ValidatorUtil.isNull(age)) {
            return;
        }
        List<WebElement> kmScrollbarViews = driver.findElements(By.className("km-scrollbar__view"));
        WebElement kmScrollbarView = kmScrollbarViews.get(index);
        List<WebElement> startKmAgeOptions = kmScrollbarView.findElements(By.className("km-option"));
        if (Integer.parseInt(age) >= 44) {
            for (WebElement startOrEndAge : startKmAgeOptions) {
                if (!startOrEndAge.getText().contains(age)) {
                    continue;
                }
                sleep(CommonUtil.getRandomMillisecond());
                appIndexWebActions.moveToElement(startOrEndAge).click().perform();
                return;
            }
        }

        //最小  年龄
        if (index == 1) {
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
        int ageMax = 0;
        if (age.matches("\\d+")) {
            ageMax = Integer.parseInt(age);
        }
        /// 最大 年龄
        int maxOptions = ((Long) js.executeScript("return document.getElementsByClassName('km-select__options').length - 1;")).intValue();
        int num = ((Long) js.executeScript(
                "var option = document.getElementsByClassName('km-select__options')[" + maxOptions + "].children;" +
                        "var num = option.length - 1;" +
                        "for (var i = 0; i < option.length; i++) {" +
                        "    if (option[i].innerText.includes('" + ageMax + "')) {" +
                        "        num = i;" +
                        "        break;" +
                        "    }" +
                        "}" +
                        "return num;")).intValue();
        js.executeScript("document.getElementsByClassName('km-select__options')[" + maxOptions + "].children[" + num + "].click();");
        sleep(CommonUtil.getRandomMillisecond());

    }


}

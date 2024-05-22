package resume.script.split;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sipaote.common.validator.ValidatorUtil;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import resume.api.ApiZl;
import resume.entity.dto.ZlVirtualConfigDTO;
import resume.util.CommonUtil;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 智联 推荐人才 模块
 *
 * @author：周杰
 * @date: 2024/5/21
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class ZlReferTalentsSplit {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * 推荐人才
     *
     * @author: 周杰
     * @date: 2024/5/21
     * @version: 1.0.0
     */
    public static void searchItem(String account, WebDriver driver, Actions appIndexWebActions) throws InterruptedException {
        //获取账号配置信息
        ZlVirtualConfigDTO zlVirtualConfigDTO = ApiZl.ziConfig(account);
        if (ValidatorUtil.isNull(zlVirtualConfigDTO.getContent())) {
            return;
        }
        List<WebElement> appNavItems = driver.findElements(By.className("app-nav__item"));
        appIndexWebActions.pause(CommonUtil.getRandom()).moveToElement(appNavItems.get(2)).pause(CommonUtil.getRandom()).click().perform();
        sleep(CommonUtil.getRandomMillisecond());
        List<WebElement> trFilterTriggers = driver.findElements(By.className("tr-filter-trigger"));
        //点击筛选
        appIndexWebActions.moveToElement(trFilterTriggers.get(0)).click().perform();
        sleep(CommonUtil.getRandomMillisecond());
        List<WebElement> fiterCheckboxGroups = driver.findElements(By.className("tr-talent-filter-item"));
        //年龄筛选
        JSONObject contentJsonObject = JSON.parseObject(zlVirtualConfigDTO.getContent());
        siftAge(fiterCheckboxGroups, appIndexWebActions, driver, contentJsonObject);
        //求职状态
        siftJobStatus(fiterCheckboxGroups, appIndexWebActions, contentJsonObject);
        //活跃日期
        siftActiveDate(driver, appIndexWebActions, contentJsonObject);
        // 确定
        WebElement footerOperation = driver.findElement(By.className("footer-operation"));
        List<WebElement> kmButtons = footerOperation.findElements(By.className("km-button"));
        appIndexWebActions.moveToElement(kmButtons.get(1)).click().perform();


    }


    /**
     * 年龄筛选
     */
    private static void siftAge(List<WebElement> fiterCheckboxGroups, Actions appIndexWebActions, WebDriver driver, JSONObject contentJsonObject) throws InterruptedException {
        WebElement ageGroup = fiterCheckboxGroups.get(0);
        List<WebElement> ageList = ageGroup.findElements(By.className("fiter-checkbox-group__item"));
        for (WebElement ageItem : ageList) {
            if (!"自定义".equals(ageItem.getText())) {
                continue;
            }
            appIndexWebActions.moveToElement(ageItem).click().perform();
            break;
        }
        sleep(CommonUtil.getRandomMillisecond());

        WebElement ageInputGroup = driver.findElement(By.className("fiter-checkbox-group__selector"));
        WebElement startAgeInput = ageInputGroup.findElement(By.className("filter-select-two__start"));
        appIndexWebActions.moveToElement(startAgeInput).click().perform();

        String ageMin = contentJsonObject.getString("age_min");
        getAgeInput(driver, ageMin, appIndexWebActions, 2);

        sleep(CommonUtil.getRandomMillisecond());

        WebElement endAgeInput = ageInputGroup.findElement(By.className("filter-select-two__end"));
        appIndexWebActions.moveToElement(endAgeInput).click().perform();
        String ageMax = contentJsonObject.getString("age_max");
        getAgeInput(driver, ageMax, appIndexWebActions, 3);

        sleep(CommonUtil.getRandomMillisecond());
    }

    /**
     * 筛选年龄下滑框
     */
    private static void getAgeInput(WebDriver driver, String age, Actions appIndexWebActions, int index) throws InterruptedException {
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

    /**
     * 求职状态
     */
    private static void siftJobStatus(List<WebElement> fiterCheckboxGroups, Actions appIndexWebActions, JSONObject contentJsonObject) throws InterruptedException {
        WebElement siftActiveElement = fiterCheckboxGroups.get(1);
        List<WebElement> fiterCheckboxGroupItems = siftActiveElement.findElements(By.className("fiter-checkbox-group__item"));
        String[] jobStatusArrs = JSON.parseObject(contentJsonObject.getString("job_status_arr"), String[].class);
        for (WebElement fiterCheckboxGroupItem : fiterCheckboxGroupItems) {
            for (String jobStatusText : jobStatusArrs) {
                if (!fiterCheckboxGroupItem.getText().contains(jobStatusText)) {
                    continue;
                }
                appIndexWebActions.moveToElement(fiterCheckboxGroupItem).click().perform();
                sleep(CommonUtil.getRandomMillisecond());
                break;
            }
        }
    }

    /**
     * 活跃日期
     */
    private static void siftActiveDate(WebDriver driver, Actions appIndexWebActions, JSONObject contentJsonObject) throws InterruptedException {
        WebElement fiterCheckboxActiveTime= driver.findElement(By.className("filter-item-activeTime"));
        List<WebElement> kmButtons = fiterCheckboxActiveTime.findElements(By.className("km-button"));
        System.out.println(kmButtons.size());
        String activeDate = contentJsonObject.getString("active_date");
        for (WebElement kmButton : kmButtons) {
            if (!kmButton.getText().contains(activeDate)) {
                continue;
            }
            appIndexWebActions.moveToElement(kmButton).click().perform();
            sleep(CommonUtil.getRandomMillisecond());
            break;
        }
    }


}

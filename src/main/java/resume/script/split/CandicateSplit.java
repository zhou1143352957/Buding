package resume.script.split;

import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import resume.util.CommonUtil;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 人才管理 模块
 *
 * @author：周杰
 * @date: 2024/5/29
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class CandicateSplit {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void candicate(WebDriver driver, JavascriptExecutor cateSearchJs, Actions appIndexWebActions) throws InterruptedException {
        sleep(CommonUtil.getRandomMillisecond());
        List<WebElement> appNavItems = driver.findElements(By.className("app-nav__item"));
        appIndexWebActions.pause(CommonUtil.getRandom()).moveToElement(appNavItems.get(5)).pause(CommonUtil.getRandom()).click().perform();
        sleep(CommonUtil.getRandomMillisecond());

        List<WebElement> candidateTabsItems = driver.findElements(By.className("candidate-tabs__item"));
        for (WebElement tab : candidateTabsItems) {
            if ("沟通中".contains(tab.getText())) {
                appIndexWebActions.moveToElement(tab).click().perform();
                sleep(CommonUtil.getRandomMillisecond());
                break;
            }
        }

        //人才来源
        WebElement sourceSelector = driver.findElement(By.className("source-selector"));
        appIndexWebActions.moveToElement(sourceSelector).click().perform();
        sleep(CommonUtil.getRandomMillisecond());
        //点击投递
        List<WebElement> kmSelectDropdowns = driver.findElements(By.className("km-select__dropdown"));
        System.out.println("kmSelectDropdowns" + kmSelectDropdowns.size());
        if (!kmSelectDropdowns.isEmpty()) {
            WebElement kmSelectDropdown = kmSelectDropdowns.get(0);
            //人才来源下拉框 选择列表
            List<WebElement> conditionSelectorItemLabels = kmSelectDropdown.findElements(By.className("condition-selector__item-label"));
            appIndexWebActions.moveToElement(conditionSelectorItemLabels.get(1)).click().perform();
            sleep(CommonUtil.getRandomMillisecond(3, 5));
            //点击确定
            WebElement conditionSelectorFooter = driver.findElement(By.className("condition-selector__footer"));
            List<WebElement> kmButtions = conditionSelectorFooter.findElements(By.className("km-button"));
            appIndexWebActions.moveToElement(kmButtions.get(1)).click().perform();
            sleep(CommonUtil.getRandomMillisecond());
        }

        //联系方式
        WebElement contactSelector = driver.findElement(By.className("contact-selector"));
        appIndexWebActions.moveToElement(contactSelector).click().perform();
        sleep(CommonUtil.getRandomMillisecond());
        List<WebElement> telDropdowns = driver.findElements(By.className("km-select__dropdown"));
        System.out.println("telDropdowns" + kmSelectDropdowns.size());
        if (!telDropdowns.isEmpty()) {
            WebElement kmSelectDropdown = telDropdowns.get(0);
            //联系方式下拉框 选择列表
            List<WebElement> conditionSelectorItemLabels = kmSelectDropdown.findElements(By.className("condition-selector__item-label"));
            appIndexWebActions.moveToElement(conditionSelectorItemLabels.get(1)).click().perform();
            sleep(CommonUtil.getRandomMillisecond(3, 5));
        }


    }

}

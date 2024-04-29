package resume.script.split;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import resume.entity.dto.VirtualConfig58DTO;
import resume.util.CommonUtil;

import java.time.Duration;
import java.util.List;

/**
 * 58简历爬虫拆分代码
 *
 * @author：周杰
 * @date: 2024/4/26
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class ReptileResumeSplit {

    /**
     * 头部筛选框 操作代码
     */
    public static void searchItem(WebDriver driver, VirtualConfig58DTO accountInfo){
        //鼠标移动到类别
        WebElement cateSearch = driver.findElement(By.className("cate-search"));
        /*     new Actions(driver).moveToElement(cateSearch, 0 ,0).pause(Duration.ofSeconds(2)).click().perform();*/
        //触发点击事件
        JavascriptExecutor cateSearchJs = (JavascriptExecutor) driver;
        cateSearchJs.executeScript("document.getElementsByClassName('" + accountInfo.getJobCate() + "')[0].click()", cateSearch);
        List<WebElement> searchItem = driver.findElements(By.className("search-item"));
        int hdy = 30 * 10;
        for (int i = 0; i < searchItem.size(); i++) {
            WebElement topPlement = searchItem.get(i);
            //城市
            if (i == 0) {
                //点击触发下拉框
                new Actions(driver).moveToElement(topPlement).pause(Duration.ofSeconds(2)).click(topPlement).pause(Duration.ofSeconds(2)).perform();
                new Actions(driver).moveByOffset(-20, 30 * 2).pause(Duration.ofSeconds(3)).click().perform();
                new Actions(driver).moveByOffset(-20, 30 * 5).pause(Duration.ofSeconds(2)).click().perform();
                //鼠标移动到滑动区域
                WebElement cityChecker = topPlement.findElement(By.className("tab-city"));
                new Actions(driver).pause(Duration.ofSeconds(2)).moveToElement(cityChecker).perform();
                WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement(cityChecker);
                new Actions(driver).moveToElement(cityChecker).scrollFromOrigin(scrollOrigin, 0, hdy).pause(Duration.ofSeconds(2)).perform();
                new Actions(driver).moveToElement(cityChecker).scrollFromOrigin(scrollOrigin, 0, hdy * 2).pause(Duration.ofSeconds(2)).perform();
                cateSearchJs.executeScript("document.getElementsByClassName('" + accountInfo.getCity() + "')[0].click()", cityChecker);
                continue;
            }
            //区域
            if (i == 1) {
                new Actions(driver).moveToElement(topPlement).pause(Duration.ofSeconds(2)).click().pause(Duration.ofSeconds(2)).perform();
                WebElement tabArea = topPlement.findElement(By.className("tab-area"));
                cateSearchJs.executeScript("document.getElementsByClassName('" + accountInfo.getArea() + "')[0].click()", tabArea);
                continue;
            }
            //商圈
            if (i == 2) {
                new Actions(driver).moveToElement(topPlement).pause(Duration.ofSeconds(2)).click().pause(Duration.ofSeconds(2)).perform();
                continue;
            }
            //性别
            if (i == 3) {
                new Actions(driver).moveToElement(topPlement).pause(Duration.ofSeconds(3)).click().pause(Duration.ofSeconds(2)).perform();
                List<WebElement> searchList = topPlement.findElements(By.className("list-item"));
                for (int j = 0; j < searchList.size(); j++) {
                    WebElement listItem = searchList.get(j);
                    if (listItem.getText().contains(accountInfo.getSex())) {
                        new Actions(driver).moveToElement(listItem).pause(Duration.ofSeconds(2)).click().pause(Duration.ofSeconds(2)).perform();
                        break;
                    }
                }
            }
            //年龄
            if (i == 4) {
                new Actions(driver).moveToElement(topPlement).pause(Duration.ofSeconds(2)).click().pause(Duration.ofSeconds(2)).perform();
                List<WebElement> searchList = topPlement.findElements(By.className("list-item"));
                //直接取最后自定义
                WebElement lastItem = searchList.get(searchList.size() - 1);
                new Actions(driver).moveToElement(lastItem).perform();
                //获取 两个输入框。然后输入年龄
                List<WebElement> antInputs = lastItem.findElements(By.className("ant-input"));
                //最小年龄
                new Actions(driver).sendKeys(antInputs.get(0), accountInfo.getAgeMin().toString()).pause(Duration.ofSeconds(2)).perform();
                //最大年龄
                new Actions(driver).sendKeys(antInputs.get(1), accountInfo.getAgeMax().toString()).pause(Duration.ofSeconds(2)).perform();
                //确定
                WebElement button = lastItem.findElement(By.tagName("button"));
                new Actions(driver).click(button).perform();
                continue;
            }
            //学历
            if (i == 5) {
                new Actions(driver).moveToElement(topPlement).pause(Duration.ofSeconds(2)).click().pause(Duration.ofSeconds(2)).perform();
                WebElement itemList = topPlement.findElement(By.className("list-item"));
                //不限
                if (itemList.getText().contains(accountInfo.getEducation())){
                    new Actions(driver).pause(Duration.ofSeconds(1)).click(itemList).perform();
                    continue;
                }
                String[] educations = accountInfo.getEducation().split(",");
                List<WebElement> checkboxList = topPlement.findElements(By.className("ant-checkbox-group-item"));
                for (int j = 0; j < educations.length; j++) {
                    String education = educations[j];
                    //top头学历列表循环比对
                    for (int k = 0; k < checkboxList.size(); k++) {
                        WebElement checkBox = checkboxList.get(k);
                        if (!checkBox.getText().contains(education)){
                            continue;
                        }
                        WebElement boxInput = checkBox.findElement(By.tagName("input"));
                        new Actions(driver).pause(Duration.ofSeconds(2)).click(boxInput).perform();
                        break;
                    }
                }
                //确定
                WebElement button = topPlement.findElement(By.tagName("button"));
                new Actions(driver).pause(Duration.ofSeconds(CommonUtil.getRandom())).click(button).perform();
            }
            if (i >= 5) {
                break;
            }
        }
    }



}

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 说明
 *
 * @author：周杰
 * @date: 2024/4/23
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class Test {
    public static void main(String[] args) throws Exception {
        // 设置 GeckoDriver 路径
        // System.setProperty("webdriver.gecko.driver", "/Users/zhoujie/Desktop/geckodriver");
        // 创建 Firefox 浏览器实例
        FirefoxProfile myprofile = new FirefoxProfile(new File("/Users/zhoujie/Library/Application Support/Firefox/Profiles/xgjmfsmz.default-release"));
        FirefoxOptions options = new FirefoxOptions();
        //   options.setHeadless(true); // 无头模式（可选）
        options.setBinary("/Applications/Firefox.app/Contents/MacOS/firefox");
        options.setProfile(myprofile);

        WebDriver driver = new FirefoxDriver(options);
        //   driver.manage().window().maximize();//窗口最大化
        // 打开网页
        driver.get("https://employer.58.com/main/resumesearch");
        // 要点击的 返回旧版
  /*      List<WebElement> returnOlds = driver.findElements(By.className("search-item"));
        //头部信息
        for (int i = 0; i < returnOlds.size(); i++) {
            WebElement selectItem = returnOlds.get(i);
            if (i == 0) {
                //点击触发下拉框
                new Actions(driver).moveToElement(selectItem).pause(Duration.ofSeconds(2)).click(selectItem).pause(Duration.ofSeconds(2)).perform();
                WebElement cityChecker = selectItem.findElement(By.className("tab-city"));
                new Actions(driver).moveByOffset(-20, 30 * 9).pause(Duration.ofSeconds(3)).click().perform();
                //鼠标移动到滑动区域
                new Actions(driver).pause(Duration.ofSeconds(2)).moveToElement(cityChecker).perform();
                WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement(cityChecker);
                int i1 = 30 * 10;
                new Actions(driver).moveToElement(cityChecker).scrollFromOrigin(scrollOrigin, 0, i1).pause(Duration.ofSeconds(2)).perform();
              //  i1 += 30 * 10;
                new Actions(driver).moveToElement(cityChecker).scrollFromOrigin(scrollOrigin, 0, i1).pause(Duration.ofSeconds(2)).perform();
              //  i1 += 30 * 10;
                new Actions(driver).moveToElement(cityChecker).scrollFromOrigin(scrollOrigin, 0, i1).pause(Duration.ofSeconds(2)).perform();
                System.out.println(i1);
                new Actions(driver).scrollFromOrigin(scrollOrigin, 0, 30 * 2).pause(Duration.ofSeconds(4)).click().perform();
            }
            if (i == 1) {
                new Actions(driver).moveToElement(selectItem).pause(Duration.ofSeconds(2)).click(selectItem).pause(Duration.ofSeconds(2)).perform();
                WebElement tabArea = selectItem.findElement(By.className("tab-area"));
                new Actions(driver).moveToElement(tabArea, 0, -(30 * 4)).pause(Duration.ofSeconds(4)).click().perform();
            }
            //  Thread.sleep(5000);
         *//*   if (i == 2) {
                new Actions(driver).pause(Duration.ofSeconds(2)).moveToElement(selectItem).click(selectItem).perform();
                WebElement tabShangQ = selectItem.findElement(By.className("tab-shangQ"));
                new Actions(driver).moveByOffset(0, 20).click(tabShangQ).perform();
            }*//*

            if (i >= 2) {
                break;
            }
           *//* if (i==0){
                JavascriptExecutor js = (JavascriptExecutor) driver;
             //   js.executeScript("document.getElementsByClassName('search-item')[3].childNodes[1].childNodes[2].click()", selectItem);
                js.executeScript(" document.getElementsByClassName(\"%s\")[0].click()", selectItem);
            }*//*
        }*/
        //定位到简历列表
        List<WebElement> infolist = driver.findElements(By.id("infolist"));
        for (int i = 0; i < infolist.size(); i++) {
            WebElement resumeInfo = infolist.get(i);

            WebElement hoverTrigger = resumeInfo.findElement(By.className("hover-trigger"));
     //       new Actions(driver).moveToElement(hoverTrigger).pause(Duration.ofSeconds(2)).click(selectItem).pause(Duration.ofSeconds(2)).perform();
            System.out.println("hoverTrigger+++==" + hoverTrigger.getText());
            new Actions(driver).moveToElement(hoverTrigger).pause(Duration.ofSeconds(2)).click().perform();
            // 获取所有窗口的句柄
            Set<String> handles = driver.getWindowHandles();
            // 切换到新打开的标签页
            for (String handle : handles) {
                if (!handle.equals(driver.getWindowHandle())) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
            // 例如获取当前页面的标题
            String title = driver.getTitle();
            System.out.println("新标签页的标题是：" + title);
            // 关闭当前标签页
      //    driver.close();
            // 切换回原始标签页
      //     driver.switchTo().window((String) handles.toArray()[0]);

            break;
        }



    /*    //掩盖的div元素
        WebElement ObscureDiv = driver.findElement(By.className("zcm-mask"));
        System.out.println("ObscureDiv=====" + ObscureDiv.getText());
        //使用显示等待，等待掩盖的div消失
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOf(ObscureDiv));
        //等待左侧菜单到可点击状态
        wait.until(ExpectedConditions.elementToBeClickable(returnOld));
        //之后再执行点击
        returnOld.click();*/
     /*   new Actions(driver)
                .moveToElement(returnOld)
                .pause(Duration.ofSeconds(3))
                .click();*/


        //搜索按钮
        //    WebElement submitButton = driver.findElement(By.className("ant-input"));
        //输入关键词
       /* submitButton.sendKeys("淮安");
        submitButton.sendKeys(Keys.RETURN);
*/
        //筛选地区


        // 等待3秒
        /*WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("#id")));*/

      /*  List<WebElement> infolist = driver.findElements(By.id("infolist"));
        for(WebElement info : infolist){
            System.out.println("info" + info.getText());
        }*/

      /*  WebElement element = rootElement.findElement(By.className("search-checked"));

        element.sendKeys(Keys.RETURN);*/
        // 等待3秒
        //
/*
        List<WebElement> elements = submitButton.findElements(By.className("search-checked"));
        System.out.println(elements.size());*/


        //  submitButton.click();

/*
        WebElement cityTabD = driver.findElement(By.cssSelector("div.city-tab:nth-child(5)"));
        System.out.println(cityTabD.getText());

        ((JavascriptExecutor) driver).executeScript("arguments[0].appendChild(arguments[1])", cityTabD, cityTabActive);*/
        // 输出页面标题
        //  System.out.println(driver.getTitle());
        // 关闭浏览器
        //driver.quit();


    }


    /**
     * 获取属性信息
     *
     * @param element
     * @return
     */
    private Map<String, String> getPropertyInfo(WebElement element) {
        String text = element.getText();
        text = text.substring(text.indexOf(' ') + 1);

        return Arrays.stream(text.split(", "))
                .map(s -> s.split(": "))
                .collect(Collectors.toMap(
                        a -> a[0],
                        a -> a[1]
                ));
    }


}

package resume.script;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import resume.config.WebDriverConfig;
import resume.entity.dto.VirtualConfig58DTO;
import resume.api.Api58;
import resume.script.split.ReptileResumeSplit;
import resume.util.CommonUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

/**
 * 爬取58简历信息
 *
 * @author：周杰
 * @date: 2024/4/25
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class ReptileResume {

    public static void main(String[] args) {
        try {
            //呼起浏览器操作
            WebDriver driver = WebDriverConfig.openWebDriver();
            //获个人账号
            WebElement usernameElement = driver.findElement(By.className("user-name"));
            //获取 58 配置信息
            VirtualConfig58DTO accountInfo = Api58.get58AccountInfo(usernameElement.getText());
            sleep(CommonUtil.getRandomMillisecond());
            //头部筛选框 操作代码
            //     ReptileResumeSplit.searchItem(driver, accountInfo);
            //简历处理部分
            while (true){
                int size = ReptileResumeSplit.resumePart(driver, accountInfo);
                if (size < 50){
                    break;
                }
                sleep(CommonUtil.getRandomMillisecond());
                //分页元素
                WebElement resumePage = driver.findElement(By.className("resume-page"));
                System.out.println("resume-page元素数量：" + driver.findElements(By.className("resume-page")).size());
                //下一页
                List<WebElement> antBtnLink = resumePage.findElements(By.className("ant-btn-link"));
                System.out.println("antBtnLink次数 = " + antBtnLink.size());
                antBtnLink.get(1).click();
                //下一页之后 点击置顶
                sleep(CommonUtil.getRandomMillisecond());
                driver.findElements(By.className("quick-entry-content-item")).get(3).click();
            }
            driver.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取属性信息
     *
     * @param element
     * @return
     */
    private static Map<String, String> getPropertyInfo(WebElement element) {
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

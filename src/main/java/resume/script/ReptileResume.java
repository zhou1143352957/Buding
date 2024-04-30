package resume.script;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import resume.config.WebDriverConfig;
import resume.entity.dto.VirtualConfig58DTO;
import resume.api.Api58;
import resume.script.split.ReptileResumeSplit;
import resume.util.CommonUtil;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            Thread.sleep(3000);
            //头部筛选框 操作代码
            //     ReptileResumeSplit.searchItem(driver, accountInfo);
            //简历处理部分
            ReptileResumeSplit.resumePart(driver, accountInfo);

            //  webDriver.close();
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

package resume.script;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import resume.config.WebDriverConfig;
import resume.entity.dto.VirtualConfig58DTO;
import resume.api.Api58;
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

    public static void main(String[] args) throws Exception {
        //呼起浏览器操作
        WebDriver driver = WebDriverConfig.openWebDriver();
        //获个人账号
        WebElement usernameElement = driver.findElement(By.className("user-name"));
        //获取 58 配置信息
        VirtualConfig58DTO accountInfo = Api58.get58AccountInfo(usernameElement.getText());
        Thread.sleep(3000);

        //头部筛选框 操作代码
   //     ReptileResumeSplit.searchItem(driver, accountInfo);
        //临时存取 简历列表的 infoid， seriesid 加入list中

        //获取简历列表
        List<WebElement> resumeList = driver.findElements(By.className("resume-item"));
        System.out.println(resumeList.size());
        for (int i = 0; i < resumeList.size(); i++) {
            WebElement resumeInfo = resumeList.get(i);
            String infoid = resumeInfo.getAttribute("infoid");
            String seriesid = resumeInfo.getAttribute("seriesid");
            new Actions(driver).moveToElement(resumeInfo).pause(Duration.ofSeconds(CommonUtil.getRandom())).click().perform();
            //整个页面回到原点 顺着 y轴 滑动
            new Actions(driver).scrollByAmount(0, 210).pause(Duration.ofSeconds(CommonUtil.getRandom())).perform();
            WebDriver resumeInfoDriver = driver.switchTo().newWindow(WindowType.TAB);
            resumeInfoDriver.get("https://jianli.58.com/resumedetail/single/" + infoid + "?seriesid=" + seriesid);
          //  resumeInfoDriver.get("https://jianli.58.com/resumedetail/single/3_netanEtXnEys_EOQnE6slEHpnvtN_eHaTEdQTGZXlEOvTenuneH5Thsvl-NkTEysnErflEyuTvtQ?seriesid=%257B%2522sver%2522%253A%25228%2522%252C%2522slotid%2522%253A%2522pc_rencai_list_hx_rec%2522%252C%2522pid%2522%253A%2522792e04e771cc4b818aceff2fcdadbfeb%2522%252C%2522uuid%2522%253A%252291e284bada77407c8939a7f88e195f09%2522%252C%2522sid%2522%253A%252291e284bada77407c8939a7f88e195f09%2522%257D&nameStyle=1");
            //获取简历信息的 一系列操作】操作
            WebElement name = resumeInfoDriver.findElement(By.id("name"));

            WebElement baseDetail = resumeInfoDriver.findElement(By.className("base-detail"));
            //截图
            File scrFile = baseDetail.getScreenshotAs(OutputType.FILE);
         //   String filePath = "../java/resume/pic/image.png";
            String filePath = "../java/pic/image.png";
            FileUtils.copyFile(scrFile, new File(filePath));
            CommonUtil.openCvOCR(filePath);

            // 获取页面源代码
            String pageSource = resumeInfoDriver.getPageSource();
            // 检查页面源代码中是否包含目标文本  虚拟号码
            if (!pageSource.contains("获取通话密号")) {
                Api58.saveResumeCount(accountInfo.getAccount(), 1);
                System.out.println("页面中|不包含|目标文本：获取通话密号");
                //    resumeInfoDriver.close();
                continue;
            }

            //存在虚拟号码
      //      Api58.getByNameAndBasic(1, name.getText(), null, "");
            //    resumeInfoDriver.close();
            if (i >= 0){
                break;
            }
            Thread.sleep(3000);

        }


        System.out.println(JSONObject.toJSONString(accountInfo));
        //  webDriver.close();
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

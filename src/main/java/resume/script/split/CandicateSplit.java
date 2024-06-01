package resume.script.split;

import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import resume.entity.dto.ZlVirtualConfigDTO;
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

    public static void candicate(WebDriver driver, JavascriptExecutor cateSearchJs, Actions appIndexWebActions, ZlVirtualConfigDTO zlVirtualConfigDTO) throws InterruptedException {
        sleep(CommonUtil.getRandomMillisecond());
        List<WebElement> appNavItems = driver.findElements(By.className("app-nav__item"));
        appIndexWebActions.pause(CommonUtil.getRandom()).moveToElement(appNavItems.get(5)).pause(CommonUtil.getRandom()).click().perform();
        sleep(CommonUtil.getRandomMillisecond());

        //沟通中
        WebElement tabGT = driver.findElement(By.cssSelector("div.candidate-tabs__item:nth-child(5)"));
        appIndexWebActions.moveToElement(tabGT).click().perform();
        sleep(CommonUtil.getRandomMillisecond());

        //人才来源
        WebElement sourceSelector = driver.findElement(By.className("source-selector"));
        appIndexWebActions.moveToElement(sourceSelector).click().perform();
        sleep(CommonUtil.getRandomMillisecond());
        //点击投递
        List<WebElement> kmSelectDropdowns = driver.findElements(By.className("km-select__dropdown"));
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

        WebElement tel = driver.findElement(By.cssSelector("div.km-popover:nth-child(35) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > a:nth-child(2)"));
        appIndexWebActions.moveToElement(tel).click().perform();
        sleep(CommonUtil.getRandomMillisecond());
        //有电话的人才数量
        WebElement number = driver.findElement(By.className("number"));
        logger.info("------有电话的人才数量------:{}个", number.getText());

        // 视图类型
        Integer viewType = (Integer) (cateSearchJs.executeScript("if (document.getElementsByClassName(\"candidate-view-type\")[0].getElementsByTagName(\"label\")[0].classList.contains(\"km-radio--checked\")) {" +
                "                    return 1" +
                "                } else {" +
                "                    return 2" +
                "                }"));

        //简历列表
        while (true) {
            List<WebElement> resumeListInners;
            if (viewType.equals(1)) {
                resumeListInners = driver.findElements(By.className("resume-list__inner"));
            } else {
                resumeListInners = driver.findElements(By.className("resume-table-item"));
            }
            if (resumeListInners.isEmpty()) {
                return;
            }
            for (int i = 0; i < resumeListInners.size(); i++) {
                WebElement resume = resumeListInners.get(i);
                //点击简历
                appIndexWebActions.moveToElement(resume).click().perform();
                sleep(CommonUtil.getRandomMillisecond());

                //获取电话
                WebElement resumeTel = resume.findElement(By.cssSelector("div.has-text > div:nth-child(2)"));
                if (resumeTel.getText().matches("\\\\d+")) {
                    logger.info("------电话------:{}", resumeTel.getText());
                }
                String resumeInfo = (String) (cateSearchJs.executeScript(getResumeInfoJs()));
                //调用人才伯乐接口


            }

        }
    }


    private static String getResumeInfoJs(){
        return "var obj = {}" +
                "                            obj.name = document.getElementsByClassName(\"resume-basic__name\")[0].innerText" +
                "                            var todayActive = document.getElementsByClassName(\"resume-basic__state\")[0].innerText" +
                "                            if (todayActive == \"今日活跃\") {" +
                "                                obj.todayActive = 1" +
                "                            } else {" +
                "                                obj.todayActive = 0" +
                "                            }" +
                "                            obj.zlJobName = document.getElementsByClassName(\"resume-communicate-job__label cursor-pointer\")[0].innerText " +
                "                            var basic = document.getElementsByClassName(\"resume-basic-tips\")[0].childNodes[1].innerHTML.replace(/<\\/?span[^>]*>/g, \"|\").replaceAll(\"||\",\"|\")" +
                "                            obj.basic = basic.substring(1, basic.length - 1)" +
                "                            var email = \"\"" +
                "                            if (document.getElementsByClassName(\"resume-basic__contact\").length > 0" +
                "                                    && document.getElementsByClassName(\"resume-basic__contact\")[0].children.length > 1) {" +
                "                                email = document.getElementsByClassName(\"resume-basic__contact\")[0].children[1].innerText" +
                "                            }" +
                "                            obj.email = email" +
                "                            var content_len = document.getElementsByClassName(\"resume-content\")[0].children.length" +
                "                            var object = {}" +
                "                            for (var i=0; i<content_len; i++) {" +
                "                                var title = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__title\")[0].innerText" +
                "                                var body = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].innerText" +
                "                                if (title == \"求职意向\" || title == \"求职期望\") {" +
                "                                    var params = []" +
                "                                    var exp = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children" +
                "                                    for (var j=0; j<exp.length; j++) {" +
                "                                        var expect = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[j].innerHTML.replaceAll(\"\\x3C!---->\",\"\").replace(/<\\/?div[^>]*>/g, \"\").replace(/<\\/?span[^>]*>/g,\"|\").replaceAll(\" \",\"\").replaceAll(\"||||、||\",\"、\").replaceAll(\"||||\",\"|\").replaceAll(\"||\",\"|\")" +
                "                                        params.push(expect.substring(1, expect.length - 1))" +
                "                                    }" +
                "                                    body = JSON.stringify(params)" +
                "                                }" +
                "                                if (title == \"所获证书\") {" +
                "                                    var params = []" +
                "                                    var exp = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children" +
                "                                    for (var j=0; j<exp.length; j++) {" +
                "                                        params.push(document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[j].innerText)" +
                "                                    }" +
                "                                    body = JSON.stringify(params)" +
                "                                }" +
                "                                if (title == \"教育经历\") {" +
                "                                    var params = []" +
                "                                    var exp = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children" +
                "                                    for (var j=0; j<exp.length; j++) {" +
                "                                        var school = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].innerHTML.replace(/<\\/?div[^>]*>/g, \"\").replace(/<\\/?span[^>]*>/g, \"|\").replaceAll(\" \",\"\").replaceAll(\"||\",\"|\")" +
                "                                        params.push(school.substring(1, school.length - 1))" +
                "                                    }" +
                "                                    body = JSON.stringify(params)" +
                "                                }" +
                "                                if (title == \"工作经历\") {" +
                "                                    var params = []" +
                "                                    var exp = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children" +
                "                                    for (var j=0; j<exp.length; j++) {" +
                "                                        var work = {}" +
                "                                        var company = \"\"" +
                "                                        var have_company = 0" +
                "                                        if (document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"resume-content__main\").length > 0" +
                "                                                && document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"resume-content__main\")[0].getElementsByClassName(\"is-mr-16\").length > 0) {" +
                "                                            have_company = 1" +
                "                                            company = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"resume-content__main\")[0].getElementsByClassName(\"is-mr-16\")[0].innerText" +
                "                                        }" +
                "                                        work['company'] = company" +
                "                                        var job = \"\"" +
                "                                        if (document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"resume-content__main\").length > 0) {" +
                "                                            var main_length = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"resume-content__main\")[0].children.length" +
                "                                            for (var k=have_company; k<main_length; k++) {" +
                "                                                job = job + document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"resume-content__main\")[0].children[k].innerText" +
                "                                            }" +
                "                                        }" +
                "                                        work['job'] = job" +
                "                                        var content = \"\"" +
                "                                        if (document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"is-mt-12\").length > 0) {" +
                "                                            content = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"is-mt-12\")[0].innerText" +
                "                                        }" +
                "                                        work['content'] = content" +
                "                                        var period = \"\"" +
                "                                        if (document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"is-mt-4\").length > 0) {" +
                "                                            period = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"is-mt-4\")[0].innerHTML.replaceAll(\"\\x3C!---->\",\"\").replace(/<\\/?span[^>]*>/g,\"|\").replaceAll(\" \",\"\").replaceAll(\"||\",\"|\")" +
                "                                            period = period.substring(1, period.length - 1)" +
                "                                        }" +
                "                                        work['period'] = period" +
                "                                        params.push(work)" +
                "                                    }" +
                "                                    body = JSON.stringify(params)" +
                "                                }" +
                "                                if (title == \"项目经历\") {" +
                "                                    var params = []" +
                "                                    var exp = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children" +
                "                                    for (var j=0; j<exp.length; j++) {" +
                "                                        var project = {}" +
                "                                        project['project'] = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"km-timeline__item-content\")[0].children[0].innerText" +
                "                                        project['period'] = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"km-timeline__item-content\")[0].children[1].innerText.replaceAll(\" \",\"\")" +
                "                                        project['content'] = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-content__body\")[0].children[0].children[j].getElementsByClassName(\"km-timeline__item-content\")[0].children[2].innerText" +
                "                                        params.push(project)" +
                "                                    }" +
                "                                    body = JSON.stringify(params)" +
                "                                }" +
                "                                object[title] = body" +
                "                            }" +
                "                            obj.content = JSON.stringify(object)" +
                "                            return obj";
    }




}

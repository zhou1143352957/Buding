package resume.script.split;

import com.alibaba.fastjson.JSON;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import resume.api.ApiZl;
import resume.entity.dto.ResumePythonDTO;
import resume.entity.dto.ZlVirtualConfigDTO;
import resume.util.CommonUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        cateSearchJs.executeScript("document.getElementsByClassName(\"source-selector\")[0].click()");
        sleep(CommonUtil.getRandomMillisecond());
        //点击投递
        cateSearchJs.executeScript(" if (document.getElementsByClassName(\"km-select__dropdown\").length > 0" +
                "                            && document.getElementsByClassName(\"km-select__dropdown\")[0].getElementsByClassName(\"condition-selector__item\").length > 1) {" +
                "                        document.getElementsByClassName(\"km-select__dropdown\")[0].getElementsByClassName(\"condition-selector__item\")[1].click()" +
                "                    }");
        sleep(CommonUtil.getRandomMillisecond());

        cateSearchJs.executeScript("if (document.getElementsByClassName(\"km-select__dropdown\").length > 0" +
                "                            && document.getElementsByClassName(\"km-select__dropdown\")[0].getElementsByClassName(\"km-button--filled\").length > 0) {" +
                "                        document.getElementsByClassName(\"km-select__dropdown\")[0].getElementsByClassName(\"km-button--filled\")[0].click()" +
                "                    }");
        sleep(CommonUtil.getRandomMillisecond());

        //联系方式
        cateSearchJs.executeScript("document.getElementsByClassName(\"contact-selector\")[0].click()");
        sleep(CommonUtil.getRandomMillisecond());

        cateSearchJs.executeScript("if (document.getElementsByClassName(\"km-select__dropdown\").length > 1" +
                "                            && document.getElementsByClassName(\"km-select__dropdown\")[1].getElementsByClassName(\"condition-selector__item\").length > 1) {" +
                "                        document.getElementsByClassName(\"km-select__dropdown\")[1].getElementsByClassName(\"condition-selector__item\")[1].click()" +
                "                    }");
        sleep(CommonUtil.getRandomMillisecond());
        //有电话的人才数量
        WebElement number = driver.findElement(By.className("number"));
        logger.info("------有电话的人才数量------:{}个", number.getText());

        // 视图类型
        Integer viewType = Integer.valueOf((cateSearchJs.executeScript("if (document.getElementsByClassName(\"candidate-view-type\")[0].getElementsByTagName(\"label\")[0].classList.contains(\"km-radio--checked\")) {" +
                "                    return 1" +
                "                } else {" +
                "                    return 2" +
                "                }")).toString());

        Set<String> phoneSet = new HashSet<>();
        int size = Integer.parseInt(number.getText());
        //当前点击到的简历数量
        int locationSize = 1;
        //预留10个打招呼次数，用于人工操作
        int page = 1;

        //简历列表
        while (true) {
            List<WebElement> resumeListInners;
            if (viewType.equals(1)) {
                if (page == 1) {
                    appIndexWebActions.scrollByAmount(0, 87).perform();
                }
                resumeListInners = driver.findElements(By.className("resume-list__inner"));
            } else {
                resumeListInners = driver.findElements(By.className("resume-table-item"));
            }
            if (resumeListInners.isEmpty()) {
                return;
            }
            int y;
            for (int i = 0; i < resumeListInners.size(); i++) {
                WebElement resume = resumeListInners.get(i);

                locationSize++;
                Dimension dimension = resume.getSize();
                y = dimension.getHeight();
                //点击简历
                if (viewType.equals(1)) {
                    cateSearchJs.executeScript("document.getElementsByClassName(\"resume-list__inner\")[" + i + "].getElementsByClassName(\"resume-item__left\")[0].click()");
                } else {
                    cateSearchJs.executeScript(" document.getElementsByClassName(\"resume-table-item\")[" + i + "].getElementsByClassName(\"resume-table-item__name\")[0].click()");
                }
                sleep(CommonUtil.getRandomMillisecond(7, 9));

                if (driver.findElements(By.cssSelector("div.has-text > div:nth-child(2)")).isEmpty()) {
                    appIndexWebActions.scrollByAmount(0, y).perform();
                    continue;
                }

                //获取电话
                String resumeTel = driver.findElement(By.cssSelector("div.has-text > div:nth-child(2)")).getText();
                logger.info("------电话------:{}", resumeTel);
                if (!resumeTel.matches("\\\\d+")) {
                    //关闭个人简历
                    driver.findElement(By.cssSelector(".km-modal--open > div:nth-child(1) > button:nth-child(2) > div:nth-child(1)")).click();
                    appIndexWebActions.scrollByAmount(0, y).perform();
                    continue;
                }
                if (phoneSet.contains(resumeTel)) {
                    driver.findElement(By.cssSelector(".km-modal--open > div:nth-child(1) > button:nth-child(2) > div:nth-child(1)")).click();
                    appIndexWebActions.scrollByAmount(0, y).perform();
                    continue;
                }
                String resumeInfo = (String) (cateSearchJs.executeScript(getResumeInfoJs()));
                //调用人才伯乐接口
                ResumePythonDTO resumePythonDTO = JSON.parseObject(resumeInfo, ResumePythonDTO.class);
                resumePythonDTO.setPhone(resumeTel);
                resumePythonDTO.setEnterpriseId(zlVirtualConfigDTO.getHrEnterpriseId());
                resumePythonDTO.setAccountName(zlVirtualConfigDTO.getAccount());
                ApiZl.saveByzhilian(resumePythonDTO);
                phoneSet.add(resumeTel);
                sleep(CommonUtil.getRandomMillisecond());

                //关闭个人简历
                driver.findElement(By.cssSelector(".km-modal--open > div:nth-child(1) > button:nth-child(2) > div:nth-child(1)")).click();
                sleep(CommonUtil.getRandomMillisecond(3, 5));
                appIndexWebActions.scrollByAmount(0, y).perform();
                sleep(CommonUtil.getRandomMillisecond());
            }
            //每次结束减去20条
            //分页点击处理
            WebElement kmPaginationPagers = driver.findElement(By.className("km-pagination__pagers"));
            List<WebElement> kmPaginationPagerArrows = kmPaginationPagers.findElements(By.className("km-pagination__pager--arrow"));
            if (kmPaginationPagerArrows.isEmpty()) {
                return;
            }
            if (size <= locationSize) {
                logger.info("------已经到底啦------");
                return;
            }
            appIndexWebActions.moveToElement(kmPaginationPagerArrows.get(1)).click().perform();
            sleep(CommonUtil.getRandomMillisecond());
            page++;

        }
    }


    private static String getResumeInfoJs() {
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
                "                                var body = document.getElementsByClassName(\"resume-content\")[0].children[i].getElementsByClassName(\"resume-con tent__body\")[0].innerText" +
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

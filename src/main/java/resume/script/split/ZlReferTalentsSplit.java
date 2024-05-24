package resume.script.split;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
    public static void searchItem(ZlVirtualConfigDTO zlVirtualConfigDTO, WebDriver driver, Actions appIndexWebActions, ZlIndexInfoVO zlbAndSize, JavascriptExecutor cateSearchJs) throws InterruptedException {
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
        //简历列表部分
        resumeListCodeCopyPy(driver, zlbAndSize, zlVirtualConfigDTO.getAccount(), cateSearchJs);
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
        WebElement fiterCheckboxActiveTime = driver.findElement(By.className("filter-item-activeTime"));
        List<WebElement> kmButtons = fiterCheckboxActiveTime.findElements(By.className("km-button"));
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

    /**
     * 简历列表部分 功能 (废弃)
     */
    private static void resumeList(WebDriver driver, ZlIndexInfoVO zlbAndSize, String account, Actions appIndexWebActions) throws InterruptedException {
        sleep(CommonUtil.getRandomMillisecond());

        int size = Integer.parseInt(zlbAndSize.getSize());
        //预留10个打招呼次数，用于人工操作
        int todayTimes = 163;
        int page = 1;
        Integer paddingY = 0;


        while (true) {
            if (page == 1) {
                appIndexWebActions.scrollByAmount(0, 87).perform();
                paddingY += 87;
            }
            sleep(CommonUtil.getRandomMillisecond(9, 15));
            List<WebElement> listitems = driver.findElements(By.xpath("//div[@role='listitem']"));

            int y;
            for (int i = 0; i < listitems.size(); i++) {
                WebElement resumeItem = listitems.get(i);
                sleep(CommonUtil.getRandomMillisecond());
                //滑轮分页 每次刷新 20个
                if (size <= todayTimes) {
                    return;
                }
                // 获取 resumeItem 元素的高度
                Dimension dimension = resumeItem.getSize();
                y = dimension.getHeight();
                System.out.println("resumeItem循环位置i=" + i);
                //分页之后获取 y 轴位置
                if (page > 1 && i == 0) {
                    paddingY += y - 165;
                    //计算出当前停留的 原点位置, 并且减去 元素一半的y轴距离
                    // int locationY = (resumeItem.getRect().y) - (y / 2);
                    System.out.println("计算出当前停留的 原点位置:" + paddingY);
                    appIndexWebActions.scrollByAmount(0, y).perform();
                    sleep(CommonUtil.getRandomMillisecond());
                }

                String name = resumeItem.findElement(By.className("talent-basic-info__name--inner")).getText().replace("\n", "|");
                String basicInfo = resumeItem.findElement(By.className("talent-basic-info__basic")).getText().replace("\n", "|");
                String expectInfo = resumeItem.findElement(By.className("talent-basic-info__extra")).getText().replace("\n", "|");
                logger.info("account:{} , name:{},  basicInfo:{} , expectInfo:{}", account, name, basicInfo, expectInfo);
                //检测是否已经打过招呼
         /*           Boolean isGreeted = ApiZl.checkIsGreeted(account, name, basicInfo, expectInfo);
                if (!isGreeted) {
                    appIndexWebActions.scrollByAmount(0, y).perform();
                    continue;
                }*/
                //定位到邀请投递按钮
                WebElement deliveryButton = resumeItem.findElement(By.className("cv-test-recommend-chat"));
                if (deliveryButton.getText().contains("继续聊天")) {
                    appIndexWebActions.scrollByAmount(0, y).perform();
                    continue;
                }

           /*     appIndexWebActions.moveToElement(deliveryButton).click().perform();
                sleep(CommonUtil.getRandomMillisecond());*/
                // 发送邀请投递

                if (!driver.findElements(By.cssSelector("button.km-button--primary:nth-child(3)")).isEmpty()) {
                    WebElement sendIm = driver.findElement(By.cssSelector("button.km-button--primary:nth-child(3)"));
                    appIndexWebActions.moveToElement(sendIm).click().perform();
                    sleep(CommonUtil.getRandomMillisecond());
                }

                // 点击我知道了 弹框
                if (!driver.findElements(By.cssSelector(".set-greet-success-modal__btn > button:nth-child(1)")).isEmpty()) {
                    WebElement iKnow = driver.findElement(By.cssSelector(".set-greet-success-modal__btn > button:nth-child(1)"));
                    appIndexWebActions.moveToElement(iKnow).click().perform();
                    sleep(CommonUtil.getRandomMillisecond());
                }

                appIndexWebActions.scrollByAmount(0, y).perform();
                sleep(CommonUtil.getRandomMillisecond());

                todayTimes--;
            }

            if (listitems.size() < 20) {
                return;
            }
            page++;
        }
    }


    private static void resumeListCodeCopyPy(WebDriver driver, ZlIndexInfoVO zlbAndSize, String account, JavascriptExecutor cateSearchJs) throws InterruptedException {
        //职位数量
        int jobNum = ((Long) cateSearchJs.executeScript(
                "return document.getElementsByClassName(\"job-pane__item\").length"
        )).intValue();
        //今日剩余次数/ 职位数量 = 平均次数
        int eachTimes = Integer.parseInt(zlbAndSize.getSize()) / jobNum;
        if (eachTimes <= 0) {
            return;
        }

        for (int i = 0; i < jobNum; i++) {
            int thisTimes = eachTimes;

            cateSearchJs.executeScript("document.getElementsByClassName(\"job-pane__item\")[" + i + "].click()");
            sleep(CommonUtil.getRandomMillisecond());
            String jobName = (String) cateSearchJs.executeScript("return document.getElementsByClassName(\"job-pane__item\")[" + i + "].innerText");
            if ("已下线".contains(jobName)) {
                continue;
            }

            for (int ind = 1; ind <= 50; ind++) {
                int todayTimesStr = Integer.parseInt(zlbAndSize.getSize());
                if (todayTimesStr < 10) {
                    logger.info("------今日次数已经用完------");
                    return;
                }

                if (ind % 5 == 0) {
                    logger.info("------等待------");
                    sleep(60000);
                }

                logger.info("------第" + ind + "页------");
                driver.navigate().refresh();
                sleep(CommonUtil.getRandomMillisecond());

                int resumeNum = ((Long) cateSearchJs.executeScript(
                        "return document.getElementsByClassName('recommend-list')[0].children[0].children[0].children.length"
                )).intValue();
                logger.info("------resumeNum------: " + resumeNum);

                sleep(CommonUtil.getRandomMillisecond());


                for (int j = 0; j < resumeNum; j++) {
                    String ageText = (String) cateSearchJs.executeScript(
                            "var resume_child = document.getElementsByClassName('recommend-list')[0].children[0].children[0].children[" + j + "];" +
                                    "if (resume_child.getElementsByClassName('talent-basic-info__basic').length > 0 && " +
                                    "resume_child.getElementsByClassName('talent-basic-info__basic')[0].children.length > 0) {" +
                                    "    return resume_child.getElementsByClassName('talent-basic-info__basic')[0].children[0].innerText;" +
                                    "}"
                    );

                    if (ageText != null && ageText.contains("岁")) {
                        ageText = ageText.replace("岁", "");
                    }

                    if (ageText != null && ageText.matches("\\d+") && Integer.parseInt(ageText) < 18) {
                        continue;
                    }
                    try {
                        String basicInfoName = ((String) cateSearchJs.executeScript(
                                "return document.getElementsByClassName('recommend-list')[0].children[0].children[0].children[" + j + "]" +
                                        ".getElementsByClassName('talent-basic-info__name--inner')[0].innerText;"
                        )).replace("\n", "|");
                        String basicInfo = ((String) cateSearchJs.executeScript(
                                "return document.getElementsByClassName('recommend-list')[0].children[0].children[0].children[" + j + "]" +
                                        ".getElementsByClassName('talent-basic-info__basic')[0].innerText;"
                        )).replace("\n", "|");
                        String expectInfo = ((String) cateSearchJs.executeScript(
                                "return document.getElementsByClassName('recommend-list')[0].children[0].children[0].children[" + j + "]" +
                                        ".getElementsByClassName('talent-basic-info__extra')[0].innerText;"
                        )).replace("\n", "|");

                        // Check if already greeted
                       //   logger.info("account:{} , name:{},  basicInfo:{} , expectInfo:{}", account, basicInfoName, basicInfo, expectInfo);
                        //检测是否已经打过招呼
                        Boolean isGreeted = ApiZl.checkIsGreeted(account, basicInfoName, basicInfo, expectInfo);
                        if (!isGreeted) {
                            continue;
                        }

                        // Click greet button
                        cateSearchJs.executeScript(
                                "var resume_btn = document.getElementsByClassName('recommend-list')[0].children[0].children[0].children[" + j + "].getElementsByClassName('resume-btn-small');" +
                                        "for (var i = 0; i < resume_btn.length; i++) {" +
                                        "    if (resume_btn[i].innerText == '邀请投递' || resume_btn[i].innerText == '打招呼') {" +
                                        "        resume_btn[i].getElementsByClassName('resume-btn-small__icon')[0].click();" +
                                        "    }" +
                                        "}"
                        );

                        sleep(CommonUtil.getRandomMillisecond());

                        // Set default greet message
                        String greet = (String) cateSearchJs.executeScript(
                                "if (document.getElementsByClassName('chat-set-greet').length > 0 && " +
                                        "document.getElementsByClassName('chat-set-greet')[0].getElementsByClassName('km-button--filled').length > 0) {" +
                                        "    document.getElementsByClassName('chat-set-greet')[0].getElementsByClassName('km-button--filled')[0].click();" +
                                        "    return 'greet';" +
                                        "}"
                        );

                        if ("greet".equals(greet)) {
                            sleep(CommonUtil.getRandomMillisecond());
                        }

                        // Decrease remaining times
                        // Assuming `thisTimes` is declared and initialized elsewhere
                        thisTimes -= 1;
                        if (thisTimes == 0) {
                            break;
                        }
                        todayTimesStr--;
                    } catch (Exception e) {
                        logger.info("------获取基本信息异常------: " + e.getMessage());
                    }
                }

                if (thisTimes == 0) {
                    logger.info("------平均次数已用完------");
                    break;
                }

                Long res = (Long) cateSearchJs.executeScript(
                        "return document.getElementsByClassName('recommend-indicator').length;"
                );

                if (res == 0) {
                    cateSearchJs.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                    sleep(CommonUtil.getRandomMillisecond());
                } else {
                    logger.info("------已经到底啦------");
                    break;
                }
            }
        }

    }

}

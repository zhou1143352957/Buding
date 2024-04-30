package resume.script.split;

import com.alibaba.fastjson.JSON;
import com.sipaote.common.api.ResponseInfo;
import com.sipaote.common.exception.BusinessException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import resume.api.Api58;
import resume.entity.dto.ResumeContentBy58DTO;
import resume.entity.dto.Virtual58DTO;
import resume.entity.dto.VirtualConfig58DTO;
import resume.entity.dto.Work58DTO;
import resume.util.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 58简历爬虫拆分代码
 *
 * @author：周杰
 * @date: 2024/4/26
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
@Log4j
public class ReptileResumeSplit {

    /**
     * 头部筛选框 操作代码
     */
    public static void searchItem(WebDriver driver, VirtualConfig58DTO accountInfo) {
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
                if (itemList.getText().contains(accountInfo.getEducation())) {
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
                        if (!checkBox.getText().contains(education)) {
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


    /**
     * @Description: 简历列表部分处理
     * @Author: 周杰
     * @Date: 2024/4/30 星期二
     * @version: dev
     **/
    public static void resumePart(WebDriver driver, VirtualConfig58DTO accountInfo) throws Exception {
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
            //获取简历信息的 一系列操作】操作

            // 获取页面源代码
            String pageSource = resumeInfoDriver.getPageSource();
            // 检查页面源代码中是否包含目标文本  虚拟号码
            if (!pageSource.contains("获取通话密号")) {
                Api58.saveResumeCount(accountInfo.getAccount(), 1);
                System.out.println("页面中|不包含|目标文本：获取通话密号");
                resumeInfoDriver.close();
                continue;
            }
            WebElement baseDetail = resumeInfoDriver.findElement(By.className("base-detail"));
            //截图
            File scrFile = baseDetail.getScreenshotAs(OutputType.FILE);
            String filePath = imageUrl("image.png", scrFile);
            String resumeSexOrAgeInfo = CommonUtil.openCvOCR(filePath);
            //存在虚拟号码
            String sex = resumeSexOrAgeInfo.split("|")[0];
            WebElement name = resumeInfoDriver.findElement(By.id("name"));
            ResponseInfo byNameAndBasic = Api58.getByNameAndBasic(1, name.getText(), sex.equals('男') || sex.equals("女") ? sex : null, resumeSexOrAgeInfo);
            if (!byNameAndBasic.isSuccess()) {
                log.info("调用校验是否拨打过异常；类型：" + 1);
                //     resumeInfoDriver.close();
                continue;
            }
            //未拨打过保存虚拟号
            saveResumeInfo(resumeInfoDriver, name.getText(), accountInfo.getAccount(), resumeSexOrAgeInfo);


            //    resumeInfoDriver.close();
            if (i >= 0) {
                break;
            }
            Thread.sleep(3000);

        }
    }

    /**
     * @param resumeInfoDriver   简历详情驱动
     * @param name               简历名称
     * @param accountName        58登录账号名称
     * @param resumeSexOrAgeInfo 男|27岁|高中以下1-3年工作经验
     * @Description: 保存虚拟号以及简历的一些基本信息
     * @Author: 周杰
     * @Date: 2024/4/30 星期二
     * @version: dev
     **/
    public static void saveResumeInfo(WebDriver resumeInfoDriver, String name, String accountName, String resumeSexOrAgeInfo) {
        Virtual58DTO dto = new Virtual58DTO();
        dto.setType(1);
        String[] infoText = resumeSexOrAgeInfo.split("|");
        dto.setSex(infoText[0].equals('男') || infoText[0].equals("女") ? infoText[0] : null);
        dto.setBasicInfo(resumeSexOrAgeInfo);
        //部分隐藏的真实号码
        WebElement telPwd = resumeInfoDriver.findElement(By.className("tel-pwd"));
        File telPwdFile = telPwd.getScreenshotAs(OutputType.FILE);
        String telPwdText = CommonUtil.openCvOCR(imageUrl("telPwd.png", telPwdFile));
        dto.setRealNum(telPwdText);

        //处理简历内容
        ResumeContentBy58DTO by58DTO = new ResumeContentBy58DTO();
        //截图  期望职位
        WebElement expectJob = resumeInfoDriver.findElement(By.id("expectJob"));
        //期望职位
        by58DTO.setExpectLocation(expectJob.getText());
        //期望地区
        WebElement expectLocation = resumeInfoDriver.findElement(By.id("expectLocation"));
        by58DTO.setExpectLocation(expectLocation.getText());
        //求职状态
        WebElement jobStatus = resumeInfoDriver.findElement(By.id("Job-status"));
        by58DTO.setJobStatus(jobStatus.getText());

        List<Work58DTO> work58DTOList = new ArrayList<>();
        //工作经验
        List<WebElement> experienceDetails = resumeInfoDriver.findElements(By.className("experience-detail"));
        for (int i = 0; i < experienceDetails.size(); i++) {
            WebElement experienceDetail = experienceDetails.get(i);
            Work58DTO work58DTO = new Work58DTO();
            //公司名称
            WebElement company = experienceDetail.findElement(By.className("itemName"));
            work58DTO.setCompany(company.getText());
            List<WebElement> ps = experienceDetail.findElements(By.tagName("p"));
            Work58DTO.getTextContent(work58DTO, ps);
            WebElement duty = experienceDetail.findElement(By.className("item-content"));
            work58DTO.setDuty(duty.getText());
            work58DTOList.add(work58DTO);
        }
        by58DTO.setWork(JSON.toJSONString(work58DTOList));
        //学历
        List<WebElement> educations = resumeInfoDriver.findElements(By.className("edu-detail"));
        for (int i = 0; i < educations.size(); i++) {
            WebElement educationDetail = educations.get(i);

        }

        //自我介绍
        WebElement eduDetail = resumeInfoDriver.findElement(By.className("edu-detail"));
        by58DTO.setAboutMe(eduDetail.getText());
        dto.setContent(JSON.toJSONString(by58DTO));
        ResponseInfo responseInfo = Api58.saveVirtual(dto);
        if (!responseInfo.isSuccess()) {
            log.error(responseInfo.getMsg());
        }


    }


    public static String imageUrl(String picName, File scrFile) {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("windows");
        //图片路径文件夹
        String image;
        if (isWindows) {
            image = "E://image";
        } else {
            image = "/Users/zhoujie/Desktop";
        }
        File folder = new File(image);
        // 判断文件夹是否存在
        if (!folder.exists()) {
            // 如果文件夹不存在，则创建文件夹
            folder.mkdirs();
        }
        String filePath = image + "/image.png";
        try {
            FileUtils.copyFile(scrFile, new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image + "/" + picName;
    }




}

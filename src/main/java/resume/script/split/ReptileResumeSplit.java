package resume.script.split;

import com.alibaba.fastjson.JSON;
import com.sipaote.common.api.ResponseInfo;
import com.sipaote.common.constant.StringPoolConstant;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import resume.api.Api58;
import resume.entity.dto.*;
import resume.util.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

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
    public static Integer resumePart(WebDriver driver, VirtualConfig58DTO accountInfo) throws Exception {
        //主窗口手柄
        String windowHandle = driver.getWindowHandle();
        //获取简历列表
        List<WebElement> resumeList = driver.findElements(By.className("resume-item"));
        if (resumeList.isEmpty()){
            return 0;
        }
        List<ResumeItemDTO> resumeItemDTOS = new ArrayList<>();
        JavascriptExecutor driverJs = (JavascriptExecutor) driver;
        //关闭聊天样式
        driverJs.executeScript("document.querySelector('.t-im-bottom-window-count-container').style.display = 'none';");
        Actions actions = new Actions(driver);
        for (int i = 0; i < resumeList.size(); i++) {
            WebElement resumeItem = resumeList.get(i);
            StringBuilder sb = new StringBuilder();
            //获取想找的信息
            WebElement resumeWanted = resumeItem.findElement(By.className("resume-wanted"));
            sb.append(resumeWanted.getText());

            WebElement hoverResume = resumeItem.findElement(By.className("hover-resume"));
            //鼠标悬停在元素上触发 hover-resume 元素展示
            //  actions.moveToElement(hoverResume).perform();
            driverJs.executeScript("arguments[0].classList.add('resume-bottom')", hoverResume);
            // 或者使用 JavaScript 直接设置元素的 style 属性
            driverJs.executeScript("arguments[0].style.display = 'block'", hoverResume);
            //     Thread.sleep(2000);// 等待
            if (!resumeItem.findElements(By.className("hover-resume-bottom")).isEmpty()) {
                WebElement hoverResumeBottom = resumeItem.findElement(By.className("hover-resume-bottom"));
                sb.append(hoverResumeBottom.getText());
            }
            driverJs.executeScript("arguments[0].classList.remove('resume-bottom')", hoverResume);
            driverJs.executeScript("arguments[0].style.display = ''", hoverResume);
            //通过api接口判断该简历有没有打过电话
            String extraInfo = sb.toString().replace(" ", "");
           // log.info("个人附加信息-----" + extraInfo);
            System.out.println("个人附加信息-----" + extraInfo);
            resumeItemDTOS.add(new ResumeItemDTO(resumeItem.getAttribute("infoid"), resumeItem.getAttribute("seriesid"), extraInfo));
        }
       /* resumeItemDTOS.add(new ResumeItemDTO("3_neOp_enXTvmaTeH5Tey5lEHpnvtN_eHaTEdQTGZXlEOvTGnQnGdpTpsvl-NkTEysnErflEyaTe65",
                "%257B%2522sver%2522%253A%25228%2522%252C%2522slotid%2522%253A%2522pc_rencai_list_hx_rec%2522%252C%2522pid%2522%253A%252219005aca1fdc411cb929beb25ec" +
                        "df5e3%2522%252C%2522uuid%2522%253A%252296783e6e81f14c99ba72e01ae4de8715%2522%252C%2522sid%2522%253A%252296783e6e81f14c99ba72e01ae4de8715%2522%257D", ""));*/
        for (int i = 0; i < resumeItemDTOS.size(); i++) {
            ResumeItemDTO resumeInfo = resumeItemDTOS.get(i);
             /*  var responseInfo = Api58.getVirtual(extraInfo);
            if (!responseInfo.isSuccess()) {
                log.error(responseInfo.getMsg());
                continue;
            }*/
            String infoid = resumeInfo.getInfoId();
            String seriesid = resumeInfo.getSeriesId();
            //整个页面回到原点 顺着 y轴 滑动
            actions.scrollByAmount(0, i == 0 ? 210 : 120).pause(Duration.ofSeconds(CommonUtil.getRandom())).perform();
            WebDriver resumeInfoDriver = driver.switchTo().newWindow(WindowType.TAB);
            JavascriptExecutor resumeInfoDriverJs = (JavascriptExecutor) resumeInfoDriver;
            String url = "https://jianli.58.com/resumedetail/single/" + infoid + "?seriesid=" + seriesid;
            resumeInfoDriver.get(url);
            //获取简历信息的 一系列操作】操作

            // 获取页面源代码
            String pageSource = resumeInfoDriver.getPageSource();
            // 检查页面源代码中是否包含目标文本  虚拟号码
            if (!pageSource.contains("通话密号")) {
                Api58.saveResumeCount(accountInfo.getAccount(), 1);
                System.out.println("页面中|不包含|目标文本：获取通话密号/通话密号");
                // 切换回主窗口
                sleep(CommonUtil.getRandomMillisecond());
                resumeInfoDriver.close();
                driver.switchTo().window(windowHandle);
                continue;
            }
            WebElement baseDetail = resumeInfoDriver.findElement(By.className("base-detail"));
            resumeInfoDriverJs.executeScript("document.querySelectorAll('.icon-split-small').forEach(i => {i.setAttribute('class', '');i.innerText = '$'})", baseDetail);
            //截图
            File scrFile = baseDetail.getScreenshotAs(OutputType.FILE);
            String filePath = imageUrl("image.png", scrFile);
            String resumeSexOrAgeInfo = CommonUtil.openCvOCR(filePath);
            //存在虚拟号码
            String sex = resumeSexOrAgeInfo.split("|")[0];
            WebElement name = resumeInfoDriver.findElement(By.id("name"));
            var byNameAndBasic = Api58.getByNameAndBasic(1, name.getText(), sex.equals('男') || sex.equals("女") ? sex : null, resumeSexOrAgeInfo);
            if (!byNameAndBasic.isSuccess()) {
                log.info("调用校验是否拨打过异常；类型：" + 1);
                // 切换回主窗口
                sleep(CommonUtil.getRandomMillisecond());
                resumeInfoDriver.close();
                driver.switchTo().window(windowHandle);
                continue;
            }
            //未拨打过保存虚拟号
            saveResumeInfo(resumeInfoDriver, name.getText(), accountInfo.getAccount(), resumeSexOrAgeInfo, url, pageSource, resumeInfo.getExtraInfo());
            // 切换回主窗口
            sleep(9000);
            resumeInfoDriver.close();
            driver.switchTo().window(windowHandle);
        }

        return resumeList.size();
    }

    /**
     * @param resumeInfoDriver   简历详情驱动
     * @param name               简历名称
     * @param accountName        58登录账号名称
     * @param resumeSexOrAgeInfo 男|27岁|高中以下1-3年工作经验
     * @param extraInfo          附加信息
     * @Description: 保存虚拟号以及简历的一些基本信息
     * @Author: 周杰
     * @Date: 2024/4/30 星期二
     * @version: dev
     **/
    public static void saveResumeInfo(WebDriver resumeInfoDriver, String name, String accountName, String resumeSexOrAgeInfo, String url, String pageSource, String extraInfo) throws InterruptedException {
        sleep(CommonUtil.getRandomMillisecond());
        String windowResumeInfoHandle = resumeInfoDriver.getWindowHandle();
        Virtual58DTO dto = new Virtual58DTO();
        dto.setType(1);
        String[] infoText = resumeSexOrAgeInfo.split("|");
        dto.setSex(infoText[0].equals('男') || infoText[0].equals("女") ? infoText[0] : null);
        dto.setBasicInfo(resumeSexOrAgeInfo);
        //部分隐藏的真实号码
        if (pageSource.contains("获取通话密号")) {
            WebElement telPwd = resumeInfoDriver.findElement(By.className("tel-pwd"));
            File telPwdFile = telPwd.getScreenshotAs(OutputType.FILE);
            String telPwdText = CommonUtil.openCvOCR(imageUrl("telPwd.png", telPwdFile));
            dto.setRealNum(telPwdText);
        }
        //处理简历内容
        ResumeContentBy58DTO by58DTO = new ResumeContentBy58DTO();
        //工资 stonefont
        WebElement expectInfo = resumeInfoDriver.findElement(By.className("expectInfo"));
        WebElement stoneFont = expectInfo.findElement(By.className("stonefont"));
        String expectSalaryText = "面议";
        if (!stoneFont.getText().contains("面议")){
            File stoneFontFile = stoneFont.getScreenshotAs(OutputType.FILE);
            expectSalaryText = CommonUtil.openCvOCR(imageUrl("expectSalary.png", stoneFontFile));
            if (expectSalaryText.contains(":")) {
                int index = expectSalaryText.indexOf(":");
                expectSalaryText = expectSalaryText.substring(index + 1);
            }
        }
        System.out.println("期望薪资:" + expectSalaryText);
        by58DTO.setExpectSalary(expectSalaryText);
        //期望职位
        WebElement expectJob = expectInfo.findElement(By.id("expectJob"));
        by58DTO.setExpectLocation(expectJob.getText());
        //期望地区
        WebElement expectLocation = expectInfo.findElement(By.id("expectLocation"));
        by58DTO.setExpectLocation(expectLocation.getText());
        //求职状态
        WebElement jobStatus = expectInfo.findElement(By.id("Job-status"));
        by58DTO.setJobStatus(jobStatus.getText());

        List<Work58DTO> work58DTOList = new ArrayList<>();
        //工作经验
        List<WebElement> experienceDetails = resumeInfoDriver.findElements(By.className("experience-detail"));
        for (int i = 0; i < experienceDetails.size(); i++) {
            WebElement experienceDetail = experienceDetails.get(i);
            Work58DTO work58DTO = new Work58DTO();
            //公司名称
            WebElement company = experienceDetail.findElement(By.className("itemName"));
            work58DTO.setCompany(company.isDisplayed() ? company.getText() : StringPoolConstant.EMPTY);
            List<WebElement> ps = experienceDetail.findElements(By.tagName("p"));
            Work58DTO.getTextContent(work58DTO, ps);
            if (!experienceDetail.findElements(By.className("item-content")).isEmpty()){
                work58DTO.setDuty(experienceDetail.findElement(By.className("item-content")).getText());
            }
            work58DTOList.add(work58DTO);
        }
        by58DTO.setWork(JSON.toJSONString(work58DTOList));
        //学历
        List<WebElement> educationList = resumeInfoDriver.findElements(By.className("education"));
        if (!educationList.isEmpty()) {
            WebElement education = educationList.get(0);
            List<EducationDTO> educationDTOList = new ArrayList<>();
            List<WebElement> educations = education.findElements(By.className("edu-detail"));
            for (int i = 0; i < educations.size(); i++) {
                WebElement educationDetail = educations.get(i);
                EducationDTO educationDTO = new EducationDTO();
                educationDTO.setCollegeName(educationDetail.findElement(By.className("college-name")).getText());
                educationDTO.setGraduateTime(educationDetail.findElement(By.className("graduate-time")).getText());
                educationDTO.setProfessional(educationDetail.findElement(By.className("professional")).getText());
                educationDTOList.add(educationDTO);
            }
            by58DTO.setEducation(JSON.toJSONString(educationDTOList));
        }
        //自我介绍
        WebElement aboutMe = resumeInfoDriver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div[5]"));
        if (!aboutMe.findElements(By.className("edu-detail")).isEmpty()) {
            WebElement aboutMeDetail = aboutMe.findElement(By.className("edu-detail"));
            by58DTO.setAboutMe(aboutMeDetail.isEnabled() && aboutMeDetail.isDisplayed() ? aboutMeDetail.getText() : StringPoolConstant.EMPTY);
        }
        dto.setContent(JSON.toJSONString(by58DTO));
        //获取虚拟号
        if (!resumeInfoDriver.findElements(By.className("getSecret-btn")).isEmpty()) {
            WebElement getSecretNumBtn = resumeInfoDriver.findElement(By.className("getSecret-btn"));
            new Actions(resumeInfoDriver).moveToElement(getSecretNumBtn).pause(Duration.ofSeconds(CommonUtil.getRandom())).click().perform();
        }
        List<WebElement> boxList = resumeInfoDriver.findElements(By.id("_58MessageBoxFrame"));
        //如果有弹框 点击
        if (!boxList.isEmpty()) {
            System.out.println("如果有弹框 点击:" + boxList.size());
            sleep(CommonUtil.getRandomMillisecond());
            //如果您的框架或iframe具有id或名称属性，则可以改用这个属性。如果页面上的名称或ID不是唯一的，那么第一个找到的将被切换到。
            WebDriver messageBoxFrameDriver = resumeInfoDriver.switchTo().frame("_58MessageBoxFrame");
            messageBoxFrameDriver.findElement(By.id("btn_ok2")).click();
         //   new Actions(messageBoxFrameDriver).moveToElement(messageBoxFrameDriver.findElement(By.id("btn_ok2"))).pause(Duration.ofSeconds(CommonUtil.getRandom())).click().perform();
            System.out.println("已经点过了");
            sleep(CommonUtil.getRandomMillisecond());
            //切换到简历详情页面
            resumeInfoDriver.switchTo().window(windowResumeInfoHandle);
            sleep(CommonUtil.getRandomMillisecond());
        }
        //获取虚拟号码位置截图 OCR识别
        WebElement telephone = resumeInfoDriver.findElement(By.className("telephone"));
        WebElement secretNum = telephone.findElement(By.className("secretNum"));
        WebElement virtualTelPwd = secretNum.findElement(By.className("tel-pwd"));
        File secretFile = virtualTelPwd.getScreenshotAs(OutputType.FILE);
        String virtualTelPath = imageUrl("virtualTel.png", secretFile);
        String virtualTel = CommonUtil.openCvOCR(virtualTelPath);
        dto.setVirtualTel(virtualTel);
        dto.setName(name);
        dto.setAccountName(accountName);
        //获取失效时间
        WebElement failTimeEm = resumeInfoDriver.findElement(By.className("failTime-em"));
        dto.setExpireTime(failTimeEm.getText());
        //跳转链接
        dto.setUrl(url);
        dto.setExtraInfo(extraInfo);
        var responseInfo = Api58.saveVirtual(dto);
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
        String filePath = image + "/" + picName;
        try {
            FileUtils.copyFile(scrFile, new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image + "/" + picName;
    }


}

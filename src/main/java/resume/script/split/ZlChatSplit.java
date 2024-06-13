package resume.script.split;

import com.sipaote.common.constant.StringPoolConstant;
import com.sipaote.common.validator.ValidatorUtil;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import resume.util.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * 智联聊天 模块
 *
 * @author：周杰
 * @date: 2024/5/28
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class ZlChatSplit {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /***
     * 聊天话术
     */
    private static Map<String, String> populateChatObj() {
        Map<String, String> chatObj = new HashMap<>();
        chatObj.put("公司做什么|做啥", "我们是一家专门为你推荐工作的公司，解决企业面临的人才短缺问题。如果您对此岗位有兴趣，我们希望能够进一步与您沟通。欢迎投递。");
        chatObj.put("职位|岗位", "您关注的岗位有详细的介绍，如果您对此有兴趣，我们希望能够进一步与您沟通。欢迎投递");
        chatObj.put("不想|其他|有没有", "那您想从事哪方面的工作？我们公司的话也有其他类型的岗位，我现在和您沟通。");
        chatObj.put("工资|待遇", "待遇详情，烦请投递下简历，我和您具体阐述下。");
        chatObj.put("上班时间|放假", "关于上班时间，岗位描述的很详细，如果您还有疑问，烦请投递下简历，我和您具体阐述下。");
        chatObj.put("哪里看到我的简历", "智联招聘网站为我公司推荐您的信息，我们认为您是一个很好的候选人，欢迎投递。");
        chatObj.put("没有时间|没空", "要不这样吧，烦请投递下简历，我现在和您沟通。");
        chatObj.put("已投递", "谢谢您的信任和提供，我们已经核对您的信息并确认。稍后会有同事联系您。");
        chatObj.put("抱歉|不考虑", "好的，打扰了，祝您早日找到满意的工作。");
        chatObj.put("你好|您好", "您好，我们希望与您进一步联系，向您介绍更多关于职位的信息，欢迎投递。|您好，非常感谢您的关注。我们目前正在招聘此岗位，如果您对此有兴趣，我们希望能够进一步与您沟通。欢迎投递|您好，我们认为您是一个很好的候选人。为了能够更好地了解您的期望和需求，我们希望能够与您取得联系。欢迎投递");
        return chatObj;
    }

    private static final String commonChat = "您好，岗位目前在招聘中。因平台消息较多回复可能不及时，您这边有意向可以交换一下电话，这边看到后主动跟您联系。";

    /**
     * 聊天外调方法
     *
     * @author: 周杰
     * @date: 2024/5/28
     * @version: 1.0.0
     */
    public static void zlChatIm(WebDriver driver, Actions appIndexWebActions, JavascriptExecutor cateSearchJs) throws InterruptedException {
        sleep(CommonUtil.getRandomMillisecond());
        List<WebElement> appImUnreads = driver.findElements(By.className("app-im-unread"));
        if (appImUnreads.isEmpty()) {
            logger.info("------未读消息------:{} 条", 0);
            return;
        }
        Integer chatSize = Integer.valueOf(appImUnreads.get(0).getText());
        logger.info("------未读消息------:{} 条", chatSize);

        List<WebElement> appMenuIndexItems = driver.findElements(By.className("app-nav__item"));
        appIndexWebActions.moveToElement(appMenuIndexItems.get(4)).pause(CommonUtil.getRandom()).click().perform();
        sleep(CommonUtil.getRandomMillisecond());

        //当前选中的
        Long isActive = (Long) cateSearchJs.executeScript("return document.getElementsByClassName(\"im-session-item km-list__item is-active\").length");
        if (isActive.equals(1L)) {
            String activeDescription = cateSearchJs.executeScript("if (document.getElementsByClassName(\"im-session-item km-list__item is-active\")[0].getElementsByClassName(\"km-list-item__description\").length > 0) {\n" +
                    "                        return document.getElementsByClassName(\"im-session-item km-list__item is-active\")[0].getElementsByClassName(\"km-list-item__description\")[0].innerText\n" +
                    "                    }") + "";

            if (activeDescription.contains("智联送您免费电话券") || activeDescription.contains("[人才推荐]")) {
                logger.info("------系统发送的消息------");
            } else {
                String need = (cateSearchJs.executeScript(" if (document.getElementsByClassName(\"im-session-detail__footer\").length > 0 && document.getElementsByClassName(\"im-session-detail__footer\")[0].style.display == \"\"" +
                        "                                && document.getElementsByClassName(\"im-sender\").length > 0 && document.getElementsByClassName(\"im-sender\")[0].style.display == \"\") {" +
                        "                            return \"need\" " +
                        "                        }")).toString();
                if ("need".equals(need)) {
                    Long messageLength = (Long) cateSearchJs.executeScript(
                            "return document.getElementsByClassName('im-message').length;"
                    );
                    String msgText = StringPoolConstant.EMPTY;
                    for (int i = messageLength.intValue() - 1; i >= 0; i--) {
                        Long textLength = (Long) cateSearchJs.executeScript(
                                "return document.getElementsByClassName('im-message')[" + i + "].getElementsByClassName('im-message__text').length;"
                        );

                        if (textLength > 0) {
                            msgText = (String) cateSearchJs.executeScript(
                                    "return document.getElementsByClassName('im-message')[" + i + "].getElementsByClassName('im-message__text')[0].innerText;"
                            );
                            break;
                        }
                    }
                    if (ValidatorUtil.isNotNull(msgText)) {
                        logger.info("------msg_text------:" + msgText);
                        String reply = StringPoolConstant.EMPTY;
                        boolean msgFlag = false;

                        Map<String, String> chatObj = populateChatObj();
                        for (Map.Entry<String, String> entry : chatObj.entrySet()) {
                            String k = entry.getKey();
                            String[] keys = k.split("\\|");
                            for (String key : keys) {
                                if (msgText.contains(key)) {
                                    String[] values = chatObj.get(k).split("\\|");
                                    reply = values[new Random().nextInt(values.length)];
                                    msgFlag = true;
                                    break;
                                }
                            }
                            if (msgFlag) {
                                break;
                            }
                        }
                        if (ValidatorUtil.isNull(reply)) {
                            reply = commonChat;
                        }
                        logger.info("------回答------:" + reply);

                        WebElement hoverElement = driver.findElements(By.className("is-autoresize")).get(0);
                        Actions actions = new Actions(driver);
                        actions.moveToElement(hoverElement).perform();
                        hoverElement.sendKeys(reply);

                        sleep(CommonUtil.getRandomMillisecond());
                        cateSearchJs.executeScript("document.getElementsByClassName('is-mb-20')[0].click();");
                        sleep(CommonUtil.getRandomMillisecond());
                    }
                }
            }
        }
        //点击未读
        WebElement kmCheckboxIcon = driver.findElement(By.className("km-checkbox__icon"));
        appIndexWebActions.moveToElement(kmCheckboxIcon).click().perform();
        sleep(CommonUtil.getRandomMillisecond());

        List<WebElement> imSessionItemBox = driver.findElements(By.className("im-session-item__box"));
        logger.info("------未读对话------:{}", imSessionItemBox.size());

        for (int i = 0; i < imSessionItemBox.size(); i++) {
            String thisUnreadStr = (String) cateSearchJs.executeScript(
                    "return document.getElementsByClassName('im-session-item__box')[" + i + "].getElementsByClassName('km-badge__item--fixed')[0].innerText;"
            );
            int thisUnread = Integer.parseInt(thisUnreadStr);

            if (thisUnread == 0) {
                continue;
            }

            String thisDescription = (String) cateSearchJs.executeScript(
                    "return document.getElementsByClassName('im-session-item__box')[" + i + "].getElementsByClassName('km-list-item__description')[0].innerText;"
            );

            cateSearchJs.executeScript(
                    "document.getElementsByClassName('im-session-item__box')[" + i + "].getElementsByClassName('km-list-item__meta')[0].click();"
            );
            sleep(CommonUtil.getRandomMillisecond());

            if (thisDescription.startsWith("智联送您免费电话券") || thisDescription.startsWith("[人才推荐]")) {
                continue;
            }

            String need = (String) cateSearchJs.executeScript(
                    "if (document.getElementsByClassName('im-session-detail__footer').length > 0 && " +
                            "document.getElementsByClassName('im-session-detail__footer')[0].style.display == '' && " +
                            "document.getElementsByClassName('im-sender').length > 0 && " +
                            "document.getElementsByClassName('im-sender')[0].style.display == '') {" +
                            "    return 'need';" +
                            "}"
            );

            if ("need".equals(need)) {
                Long messageLength = (Long) cateSearchJs.executeScript(
                        "return document.getElementsByClassName('im-message').length;"
                );

                StringBuilder messageBuilder = new StringBuilder();
                for (int j = messageLength.intValue() - 1; j >= 0; j--) {
                    if (thisUnread == 0) {
                        break;
                    }

                    Long textLength = (Long) cateSearchJs.executeScript(
                            "return document.getElementsByClassName('im-message')[" + j + "].getElementsByClassName('im-message__text').length;"
                    );

                    if (textLength > 0) {
                        String msgText = (String) cateSearchJs.executeScript(
                                "return document.getElementsByClassName('im-message')[" + j + "].getElementsByClassName('im-message__text')[0].innerText;"
                        );
                        messageBuilder.append(msgText);
                        thisUnread--;
                    }
                }

                if (!messageBuilder.isEmpty()) {
                    String message = messageBuilder.toString();
                    String reply = StringPoolConstant.EMPTY;
                    boolean msgFlag = false;

                    Map<String, String> chatObj = populateChatObj();
                    for (Map.Entry<String, String> entry : chatObj.entrySet()) {
                        String k = entry.getKey();
                        String[] keys = k.split("\\|");
                        for (String key : keys) {
                            if (message.contains(key)) {
                                String[] values = entry.getValue().split("\\|");
                                reply = values[new Random().nextInt(values.length)];
                                msgFlag = true;
                                break;
                            }
                        }
                        if (msgFlag) {
                            break;
                        }
                    }
                    if (reply.isEmpty()) {
                        reply = commonChat;
                    }
                    WebElement hoverElement = driver.findElements(By.className("is-autoresize")).get(0);
                    Actions actions = new Actions(driver);
                    actions.moveToElement(hoverElement).perform();
                    hoverElement.sendKeys(reply);

                    sleep(CommonUtil.getRandomMillisecond());
                    cateSearchJs.executeScript(
                            "document.getElementsByClassName('is-mb-20')[0].click();"
                    );
                    sleep(CommonUtil.getRandomMillisecond());
                }
            }
        }

    }


}

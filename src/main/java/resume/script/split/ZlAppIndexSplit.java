package resume.script.split;

import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import resume.api.ApiZl;
import resume.entity.vo.ZlIndexInfoVO;
import resume.util.CommonUtil;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 智联首页
 *
 * @author：周杰
 * @date: 2024/5/22
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class ZlAppIndexSplit {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * 获取剩余 智联币 和 今日剩余邀请投递次数
     *
     * @author: 周杰
     * @date: 2024/5/22
     * @version: 1.0.0
     */
    public static ZlIndexInfoVO getZlbAndSize(String account, WebDriver driver) throws InterruptedException {
        sleep(CommonUtil.getRandomMillisecond());
        WebElement zlbElement = driver.findElement(By.className("index-card__content--item"));
        //获取智联币
        WebElement zlbText = zlbElement.findElement(By.tagName("a"));
        //获取今日剩余邀请投递次数
        WebElement indexRightsDetail = driver.findElement(By.className("index-rights__detail"));
        List<WebElement> indexRightsDetailItems = indexRightsDetail.findElements(By.className("index-rights__detail-item"));
        WebElement sizeElement = indexRightsDetailItems.get(1).findElement(By.tagName("a"));

        ZlIndexInfoVO zlIndexInfoVO = new ZlIndexInfoVO(zlbText.getText(), sizeElement.getText());
        //api
        ApiZl.saveRemainTimes(account, zlIndexInfoVO.getMoney(), zlIndexInfoVO.getSize());
        sleep(CommonUtil.getRandomMillisecond());
        return zlIndexInfoVO;
    }


}
package resume.script.split;

import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import resume.api.Api58;
import resume.util.CommonUtil;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * @Description: 元宝方法
 * @author: 周杰
 * @date: 2024/5/14 星期二
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
public class YuanBaoSplit {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * @Description: 跳转到元宝页面
     * @Author: 周杰
     * @Date: 2024/5/14 星期二
     * @version: dev_
     **/
    public static void yuanBaoPage(WebDriver driver, String account) {
        try {
            sleep(CommonUtil.getRandomMillisecond(14, 20));
            List<WebElement> navItemBoxs = driver.findElements(By.className("nav-item-box"));
            navItemBoxs.get(navItemBoxs.size() - 2).click();
            sleep(CommonUtil.getRandomMillisecond());
            WebElement homeCoinInfoLeft = driver.findElement(By.className("home_coin_info_left"));
            List<WebElement> nums = homeCoinInfoLeft.findElements(By.className("num"));
            //元宝总个数
            WebElement yuanBaoTotalNum = nums.get(0);
            //今日领取（个）
            WebElement yuanBaoToDayNum = nums.get(1);
            //保存每日元宝数量，领取元宝数量
            Api58.saveYuanBao(account, Integer.valueOf(yuanBaoTotalNum.getText()), Integer.valueOf(yuanBaoToDayNum.getText()));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}

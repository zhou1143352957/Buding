package resume.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sipaote.common.api.ResponseInfo;
import com.sipaote.common.exception.BusinessException;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resume.config.UrlConstant;
import resume.entity.dto.ZlVirtualConfigDTO;
import resume.entity.vo.ZlTimeVO;
import resume.script.split.ApiRetryMechanism;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智联招聘API
 *
 * @author：周杰
 * @date: 2024/5/16
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class ApiZl {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * 智联心跳接口
     *
     * @author: 周杰
     * @date: 2024/5/16
     * @version: 1.0.0
     * post
     */
    public static ResponseInfo heartBeatZl(String account) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        HttpRequest request = HttpUtil.createPost(UrlConstant.HEART_BEAT_ZL + "?account=" + account);
        // 设置请求头
        request.header("Content-Type", "application/json");
        String body = request.execute().body();
        var responseInfo = JSONObject.parseObject(body, ResponseInfo.class);
        if (!responseInfo.isSuccess()) {
            logger.error("脚本心跳body返回异常: {}", body);
        }
        return responseInfo;
    }


    /**
     * 获取智联时间段
     *
     * @author: 周杰
     * @date: 2024/5/16
     * @version: 1.0.0
     * return true->在时间范围内 false->不在时间范围内
     */
    public static Boolean getZlTime() {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        String configValue = HttpUtil.get(UrlConstant.GET_VIRTUAL_ZL_TIME);
        // [{"startTime":"08:30","endTime":"21:00"}]
        var responseInfo = JSONObject.parseObject(configValue, ResponseInfo.class);
        if (!responseInfo.isSuccess()) {
            logger.error("智联获取时间段接口异常:{}", responseInfo.getMsg());
        }
        List<ZlTimeVO> zlTimeVos = JSONArray.parseArray(responseInfo.getData().toString(), ZlTimeVO.class);
        // 获取当前时间
        LocalTime now = LocalTime.now();
        // 遍历时间段列表，判断当前时间是否在任意一个时间段内
        for (ZlTimeVO zlTimeVo : zlTimeVos) {
            LocalTime startTime = LocalTime.parse(zlTimeVo.getStartTime());
            LocalTime endTime = LocalTime.parse(zlTimeVo.getEndTime());
            if (now.isAfter(startTime) && now.isBefore(endTime)) {
                return true;
            }
        }
        throw new BusinessException("不在时间范围内");
    }

    /**
     * 获取智联用户 表头筛选配置信息
     *
     * @author: 周杰
     * @date: 2024/5/17
     * @version: 1.0.0
     */
    public static ZlVirtualConfigDTO ziConfig(String account) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        Map<String, Object> param = new HashMap<>();
        param.put("account", account);
        String body = HttpUtil.get(UrlConstant.GET_CONFIG_ZL, param);

        var responseInfo = JSON.parseObject(body, ResponseInfo.class);
        if (!responseInfo.isSuccess()) {
            throw new BusinessException("获取智联筛选配置信息异常");
        }
        logger.info("获取智联筛选配置信息: {}", body);
        return JSON.parseObject(responseInfo.getData().toString(), ZlVirtualConfigDTO.class);
    }

    /**
     * 保存打招呼次数,剩余智联币
     *
     * @param account     账号
     * @param remainCoin  剩余智联币
     * @param remainTimes 今日剩余邀请投递次数
     * @author: 周杰
     * @date: 2024/5/22
     * @version: 1.0.0
     */
    public static void saveRemainTimes(String account, String remainCoin, String remainTimes) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        logger.info("账号:{} 剩余智联币:{} 今日剩余邀请投递次数:{}", account, remainCoin, remainTimes);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", account);
        jsonObject.put("remainCoin", remainCoin);
        jsonObject.put("remainTimes", remainTimes);
        HttpRequest request = HttpUtil.createPost(UrlConstant.SAVE_REMAIN_TIMES);
        // 设置请求头
        request.body(jsonObject.toJSONString());
        request.header("Content-Type", "application/json");
        String body = request.execute().body();
        var responseInfo = JSON.parseObject(body, ResponseInfo.class);
        if (!responseInfo.isSuccess()) {
            throw new BusinessException("保存打招呼次数,剩余智联币异常");
        }
    }


    /**
     * 检测是否已经打过招呼
     *
     * @author: 周杰
     * @date: 2024/5/23
     * @version: 1.0.0
     * @param account
     * @param name
     * @param basicInfo
     * @param expectInfo
     */
    public static Boolean checkIsGreeted(String account, String name, String basicInfo, String expectInfo){
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        logger.info("account:{} , name:{},  basicInfo:{} , expectInfo:{}", account, name, basicInfo, expectInfo);

        JSONObject param = new JSONObject();
        param.put("accountType", 1);
        param.put("account", account);
        param.put("name", name);
        param.put("basicInfo", basicInfo);
        param.put("expectInfo", expectInfo);
        HttpRequest request = HttpUtil.createPost(UrlConstant.CHECK_IS_GREETED);
        // 设置请求头
        request.body(param.toJSONString());
        request.header("Content-Type", "application/json");
        String body = request.execute().body();
        var responseInfo = JSON.parseObject(body, ResponseInfo.class);
        if (!responseInfo.isSuccess()) {
            logger.error("检测是否已经打过招呼接口异常:{}", responseInfo.getMsg());
            return false;
        }
        return true;
    }


}

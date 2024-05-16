package resume.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sipaote.common.api.ResponseInfo;
import com.sipaote.common.exception.BusinessException;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resume.config.UrlConstant;
import resume.entity.vo.ZlTimeVo;
import resume.script.split.ApiRetryMechanism;

import java.time.LocalTime;
import java.util.List;

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
        List<ZlTimeVo> zlTimeVos = JSONArray.parseArray(responseInfo.getData().toString(), ZlTimeVo.class);
        // 获取当前时间
        LocalTime now = LocalTime.now();
        // 遍历时间段列表，判断当前时间是否在任意一个时间段内
        for (ZlTimeVo zlTimeVo : zlTimeVos) {
            LocalTime startTime = LocalTime.parse(zlTimeVo.getStartTime());
            LocalTime endTime = LocalTime.parse(zlTimeVo.getEndTime());
            if (now.isAfter(startTime) && now.isBefore(endTime)) {
                return true;
            }
        }
        throw new BusinessException("不在时间范围内");
    }



}

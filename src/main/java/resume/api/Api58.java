package resume.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sipaote.common.api.ResponseInfo;
import com.sipaote.common.validator.ValidatorUtil;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resume.config.UrlConstant;
import resume.entity.dto.Virtual58DTO;
import resume.entity.dto.VirtualConfig58DTO;
import resume.script.split.ApiRetryMechanism;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取api基础信息
 *
 * @author：周杰
 * @date: 2024/4/25
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class Api58 {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * 根据账号 获取58账号配置筛选条件
     *
     * @author: 周杰
     * @date: 2024/4/25
     * @version: 1.0.0
     */
    public static VirtualConfig58DTO get58AccountInfo(String accountName) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        Map<String, Object> param = new HashMap<>();
        param.put("account", accountName);
        String value = HttpUtil.get(UrlConstant.GET_CONFIG_58, param);
        JSONObject jsonObject = JSON.parseObject(value);
        if (!jsonObject.getString("code").equals("200")) {
            return null;
        }
        VirtualConfig58DTO virtualConfig58DTO = JSON.parseObject(jsonObject.getString("data"), VirtualConfig58DTO.class);
        return virtualConfig58DTO;
    }

    /**
     * 保存每日简历查看数
     *
     * @param account 账号
     * @param type    类型 1：查看简历加1 2：查看虚拟号加1 3：再次爬取加1
     * @author: 周杰
     * @date: 2024/4/26
     * @version: 1.0.0
     */
    public static void saveResumeCount(String account, Integer type) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("account", account);
        jsonParam.put("type", type);
        HttpRequest request = HttpUtil.createPost(UrlConstant.SAVE_RESUME_COUNT + "?account=" + account);
        // 设置请求头
        request.header("Content-Type", "application/json");
        // 添加请求体（例如JSON数据）
        request.body(jsonParam.toJSONString());
        String value = request.execute().body();
        JSONObject jsonObject = JSON.parseObject(value);
        if (!jsonObject.getString("code").equals("200")) {
            logger.info("{}账号 保存每日简历查看数异常；类型：{}", account, type);
        }
    }

    /**
     * python调用校验是否拨打过
     *
     * @author: 周杰
     * @date: 2024/4/26
     * @version: 1.0.0
     */
    public static ResponseInfo getByNameAndBasic(Integer type, String name, String sex, String basicInfo) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("name", name);
        if (ValidatorUtil.isNotNull(sex) && !sex.contains("不限")) {
            map.put("sex", sex);
        }
        map.put("basicInfo", basicInfo);
        String body = HttpUtil.get(UrlConstant.GET_BY_NAME_AND_BASIC, map);
        // 设置请求头
        logger.info("getByNameAndBasic调用校验是否拨打过:{}", body);
        return JSON.parseObject(body, ResponseInfo.class);
    }

    /**
     * python解析加密文字
     *
     * @author: 周杰
     * @date: 2024/4/28
     * @version: 1.0.0
     */
    public static String pyBaseInfo(String htmlContent) {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("htmlContent", htmlContent);
        HttpRequest request = HttpUtil.createPost(UrlConstant.PY_BASE_INFO);
        // 设置请求头
        request.header("Content-Type", "application/json");
        // 添加请求体（例如JSON数据）
        request.body(jsonParam.toJSONString());
        String body = request.execute().body();
        return body;
    }


    /**
     * @Description: python调用保存虚拟号码
     * @Author: 周杰
     * @Date: 2024/4/30 星期二
     * @version: dev
     **/
    public static void saveVirtual(Virtual58DTO virtual58DTO) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        HttpRequest request = HttpUtil.createPost(UrlConstant.SAVE_VIRTUAL);
        // 设置请求头
        request.header("Content-Type", "application/json");
        // 添加请求体（例如JSON数据）
        String jsonString = JSON.toJSONString(virtual58DTO);
        logger.info("调用保存虚拟号码基本信息: {}", jsonString);
        request.body(jsonString);
        String body = request.execute().body();
        logger.info("保存虚拟号码基本信息返回body: {}", body);
        var responseInfo = JSONObject.parseObject(body, ResponseInfo.class);
        if (!responseInfo.isSuccess()) {
            logger.error("保存虚拟号码基本信息异常:{}", responseInfo.getMsg());
        }

    }


    /**
     * python调用校验是否拨打过（根据列表附加内容） get
     *
     * @param extraInfo 附加信息 （想找：徐州鼓楼|后厨杂工|面议擅长沟通1-3年后厨经验期望的福利有朝九晚五、双休、有五险一金、离家近、长期稳定。）
     */
    public static ResponseInfo getVirtual(String extraInfo) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        Map<String, Object> map = new HashMap<>();
        map.put("extraInfo", extraInfo);
        String body = HttpUtil.get(UrlConstant.GET_BY_EXTRA_INFO, map);
        // 设置请求头
        return JSONObject.parseObject(body, ResponseInfo.class);
    }

    /**
     * 脚本心跳
     * post
     *
     * @author: 周杰
     * @date: 2024/5/11
     * @version: 1.0.0
     */
    public static ResponseInfo heartBeat(String account) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        HttpRequest request = HttpUtil.createPost(UrlConstant.HEART_BEAT + "?account=" + account + "&session_status=true");
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
     * 验证人才伯乐 总后台服务器是否 异常/重启
     *
     * @author: 周杰
     * @date: 2024/5/13
     * @version: 1.0.0
     */
    public static String verifyApi() {
        String value = HttpUtil.get(UrlConstant.GET_CONFIG_58);
        return value;
    }

    /**
     * @param account   账号
     * @param totalCoin 总元宝
     * @param todayGet  今日获得的元宝
     * @Description: 保存每日元宝数量，领取元宝数量
     * @Author: 周杰
     * @Date: 2024/5/14 星期二
     * @version: dev
     **/
    public static void saveYuanBao(String account, Integer totalCoin, Integer todayGet) {
        //验证接口是否异常
        ApiRetryMechanism.callApiWithRetry();

        HttpRequest request = HttpUtil.createPost(UrlConstant.HEART_BEAT + "?account=" + account + "&totalCoin=" + totalCoin + "&todayGet=" + todayGet);
        // 设置请求头
        request.header("Content-Type", "application/json");
        String body = request.execute().body();
        JSONObject jsonObject = JSON.parseObject(body);
        if (!jsonObject.getString("code").equals("200")) {
            logger.error("保存每日元宝数量异常:{}", body);
        }
    }

}

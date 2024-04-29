package resume.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sipaote.common.exception.BusinessException;
import com.sipaote.common.validator.ValidatorUtil;
import netscape.javascript.JSObject;
import resume.config.BaseConfig;
import resume.config.UrlConstant;
import resume.entity.dto.VirtualConfig58DTO;

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

    /**
     * 根据账号 获取58账号配置筛选条件
     *
     * @author: 周杰
     * @date: 2024/4/25
     * @version: 1.0.0
     */
    public static VirtualConfig58DTO get58AccountInfo(String accountName) {
        Map<String, Object> param = new HashMap<>();
        param.put("account", accountName);
        String value = HttpUtil.get(BaseConfig.testUrl + UrlConstant.GET_CONFIG_58, param);
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
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("account", account);
        jsonParam.put("type", type);
        HttpRequest request = HttpUtil.createPost(BaseConfig.testUrl + UrlConstant.SAVE_RESUME_COUNT);
        // 设置请求头
        request.header("Content-Type", "application/json");
        // 添加请求体（例如JSON数据）
        request.body(jsonParam.toJSONString());
        String value = request.execute().body();
        JSONObject jsonObject = JSON.parseObject(value);
        System.out.println(jsonObject.toJSONString());
        if (!jsonObject.getString("code").equals("200")) {
            throw new BusinessException(account + "账号 保存每日简历查看数异常；类型：" + type);
        }
    }

    /**
     * python调用校验是否拨打过
     *
     * @author: 周杰
     * @date: 2024/4/26
     * @version: 1.0.0
     */
    public static void getByNameAndBasic(Integer type, String name, String sex, String basicInfo) {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("type", type);
        jsonParam.put("name", name);
        if (ValidatorUtil.isNotNull(sex) && !sex.contains("不限")) {
            jsonParam.put("sex", sex);
        }
        jsonParam.put("basicInfo", basicInfo);
        HttpRequest request = HttpUtil.createPost(BaseConfig.testUrl + UrlConstant.GET_BY_NAME_AND_BASIC);
        // 设置请求头
        request.header("Content-Type", "application/json");
        // 添加请求体（例如JSON数据）
        request.body(jsonParam.toJSONString());
        String body = request.execute().body();
        JSONObject jsonObject = JSON.parseObject(body);
        System.out.println(jsonObject.toJSONString());
        if (!jsonObject.getString("code").equals("200")) {
            throw new BusinessException("调用校验是否拨打过异常；类型：" + type);
        }
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
        HttpRequest request = HttpUtil.createPost(BaseConfig.locationPyUrl + UrlConstant.PY_BASE_INFO);
        // 设置请求头
        request.header("Content-Type", "application/json");
        // 添加请求体（例如JSON数据）
        request.body(jsonParam.toJSONString());
        String body = request.execute().body();
        return body;
    }

}

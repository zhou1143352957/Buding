import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import resume.api.ApiZl;
import resume.script.split.ApiRetryMechanism;

import java.time.LocalTime;

/**
 * 说明
 *
 * @author：周杰
 * @date: 2024/5/13
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class Test5 {

    public static void main(String[] args) {
        /*Boolean zlTime = ApiZl.getZlTime();
        System.out.println(zlTime);*/
        JSONObject jsonObject = JSON.parseObject("{\n" +
                "    \"code\": 200,\n" +
                "    \"msg\": \"操作成功\",\n" +
                "    \"data\": {\n" +
                "        \"id\": 59,\n" +
                "        \"createUser\": \"沈健\",\n" +
                "        \"createTime\": \"2024-04-07 09:15:53\",\n" +
                "        \"updateUser\": \"胡中林\",\n" +
                "        \"updateTime\": \"2024-05-09 09:05:58\",\n" +
                "        \"accountType\": 1,\n" +
                "        \"account\": \"hahhxxzx622490\",\n" +
                "        \"userName\": \"周杰\",\n" +
                "        \"contactId\": 45644,\n" +
                "        \"sendMessage\": 1,\n" +
                "        \"hrEnterpriseId\": 7830,\n" +
                "        \"type\": 1,\n" +
                "        \"content\": \"{\\\"city_arr\\\":[\\\"泰州\\\",\\\"广州\\\"],\\\"age_min\\\":\\\"18\\\",\\\"age_max\\\":\\\"50\\\",\\\"active_date\\\":\\\"30天内活跃\\\",\\\"job_status_arr\\\":[\\\"在职-找工作\\\",\\\"离职-找工作\\\",\\\"在职-看机会\\\"]}\",\n" +
                "        \"status\": 0,\n" +
                "        \"heartBeatTime\": \"2024-05-13 12:25:52\",\n" +
                "        \"remark\": \"泰州\",\n" +
                "        \"deleted\": 0,\n" +
                "        \"serverType\": 4,\n" +
                "        \"verbalType\": 113,\n" +
                "        \"lineType\": \"67\",\n" +
                "        \"jobId\": 40,\n" +
                "        \"spareLine1\": 79,\n" +
                "        \"spareLine2\": 81,\n" +
                "        \"spareLine3\": 86,\n" +
                "        \"spareLine4\": 87\n" +
                "    }\n" +
                "}");

        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject content = data.getJSONObject("content");
        JSONArray cityArr = content.getJSONArray("city_arr");
        String[] strings = JSON.parseObject(String.valueOf(cityArr), String[].class);
        System.out.println(strings[0]);
    }
}

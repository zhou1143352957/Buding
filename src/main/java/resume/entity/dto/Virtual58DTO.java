package resume.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 58爬取的简历DTO
 * @author: 周杰
 * @date: 2024/4/30 星期二
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
@Data
public class Virtual58DTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型1->58，2->智联，3->boss
     */
    private Integer type;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 基本信息
     */
    private String basicInfo;

    /**
     * 简历内容
     */
    private String content;

    /**
     * 部分隐藏的真实号码
     */
    private String realNum;

    /**
     * 虚拟号码
     */
    private String virtualTel;

    /**
     * 过期时间
     */
    private String expireTime;

    /**
     * 账号名称
     */
    private String accountName;

    /**
     * 跳转链接
     */
    private String url;

    /**
     * 附加信息
     */
    private String extraInfo;

    /**
     * 来源 1:python 2:uibot 3:java 4:油猴 5:app
     */
    private Integer source = 3;


}

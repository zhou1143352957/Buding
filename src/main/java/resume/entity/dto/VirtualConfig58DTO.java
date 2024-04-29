package resume.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 58账号配置筛选条件DTO
 *
 * @author：周杰
 * @date: 2024/4/25
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
@Data
public class VirtualConfig58DTO implements Serializable {

    /**
     * 58账号
     */
    private String account;

    /**
     * 联系人id
     */
    private Long contactId;

    /**
     * 短信提醒 0 否 1 是
     */
    private Integer sendMessage;

    /**
     * 职位类别
     */
    private String jobCate;

    /**
     * 城市
     */
    private String city;

    /**
     * 区域
     */
    private String area;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄最小值
     */
    private Integer ageMin;

    /**
     * 年龄最大值
     */
    private Integer ageMax;

    /**
     * 学历，逗号分隔
     */
    private String education;

    /**
     * 状态 0:已停止 1:运行中
     */
    private Integer status;

    /**
     * 心跳时间
     */
    private LocalDateTime heartBeatTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 当前元宝
     */
    private Integer totalCoin;

    /**
     * 今日领取
     */
    private Integer todayGet;

    /**
     * 查看简历次数限制
     */
    private Integer resumeCountLimit;

    /**
     * 查看虚拟号次数限制
     */
    private Integer virtualCountLimit;

    /**
     * 再次爬取次数限制
     */
    private Integer multiCountLimit;

}

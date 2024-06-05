package resume.entity.dto;

import lombok.Data;

/**
 * 智联爬的简历 DTO
 *
 * @author：周杰
 * @date: 2024/6/4
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
@Data
public class ResumePythonDTO {


    /**
     * 姓名
     */
    private String name;

    /**
     * 基本信息
     */
    private String basic;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 简历内容
     */
    private String content;

    /**
     * 所属人资企业ID
     */
    private Long enterpriseId;

    /**
     * 智联账号
     */
    private String accountName;

    /**
     * 今日活跃=1，默认0
     */
    private Integer todayActive;

    /**
     * 智联招聘岗位
     */
    private String zlJobName;

}

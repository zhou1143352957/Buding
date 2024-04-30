package resume.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 爬的简历基本信息 DTO
 * @author: 周杰
 * @date: 2024/4/30 星期二
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
@Data
public class ResumeContentBy58DTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String aboutMe;

    /**
     * 教育经历
     */
    private String education;

    /**
     * 期望岗位
     */
    private String expectJob;

    /**
     * 期望位置
     */
    private String expectLocation;

    /**
     * 状态
     */
    private String jobStatus;

    /**
     * 工作经历
     */
    private String work;

    /**
     * 期望薪资
     */
    private String expectSalary;

}

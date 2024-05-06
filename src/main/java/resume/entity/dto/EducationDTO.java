package resume.entity.dto;

import lombok.Data;

/**
 * @Description:学历DTO
 * @author: 周杰
 * @date: 2024/5/6 星期一
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
@Data
public class EducationDTO {

    /**
     * 学校名称
     */
    private String collegeName;


    /**
     * 毕业时间
     */
    private String graduateTime;

    /**
     * 专业
     */
    private String professional;


}

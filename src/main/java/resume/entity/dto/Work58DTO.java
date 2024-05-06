package resume.entity.dto;

import lombok.Data;
import org.openqa.selenium.WebElement;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 职位信息DTO
 * @author: 周杰
 * @date: 2024/4/30 星期二
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
@Data
public class Work58DTO implements Serializable {

    //[{\"workTime\":\"2015年08月-2019年01月（3年5个月）\",\"salary\":\"保密\",\"position\":\"气保焊工\",\"company\":\"江苏宗申三轮车有限公司\",\"duty\":\"\"}]
    /**
     * 工作时间
     */
    private String workTime;
    /**
     * 工资
     */
    private String salary;

    /**
     * 职位
     */
    private String position;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 职位描述
     */
    private String duty;


    public Work58DTO() {

    }

    public Work58DTO(String workTime, String salary, String position, String company, String duty) {
        this.workTime = workTime;
        this.salary = salary;
        this.position = position;
        this.company = company;
        this.duty = duty;
    }

    public static void getTextContent(Work58DTO work58DTO, List<WebElement> ps){
        if (ps.isEmpty()) {
            return;
        }
        for (int j = 0; j < ps.size(); j++) {
            WebElement p = ps.get(j);
            String text = p.getText();
            int index = text.indexOf("：");
            // 获取冒号前面的数据
            String beforeData = text.substring(0, index);
            // 获取冒号后面的数据
            String afterData = text.substring(index + 1);
            switch (beforeData){
                case "工作时间" -> work58DTO.setWorkTime(afterData);
                case "薪资水平" -> work58DTO.setSalary(afterData);
                case "在职职位" -> work58DTO.setPosition(afterData);
            }
        }

    }


}

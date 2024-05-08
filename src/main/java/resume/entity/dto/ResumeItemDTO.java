package resume.entity.dto;

import lombok.Data;
import org.openqa.selenium.WebElement;

/**
 * @Description:  简历列表加密 参数
 * @author: 周杰
 * @date: 2024/5/6 星期一
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
@Data
public class ResumeItemDTO {

    private String infoId;

    private String seriesId;

    private WebElement webElement;

    /***
     * 附加信息
     */
    private String extraInfo;

    public ResumeItemDTO(){}

    public ResumeItemDTO(String infoId, String seriesId, String extraInfo) {
        this.infoId = infoId;
        this.seriesId = seriesId;
        this.extraInfo = extraInfo;
    }

    public ResumeItemDTO(String infoId, String seriesId, WebElement webElement) {
        this.infoId = infoId;
        this.seriesId = seriesId;
        this.webElement = webElement;
    }

}

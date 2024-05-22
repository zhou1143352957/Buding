package resume.entity.vo;

import lombok.Data;

/**
 * 智联app/index 基础信息
 *
 * @author：周杰
 * @date: 2024/5/22
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
@Data
public class ZlIndexInfoVO {

    /**
     * 智联币
     */
    private String money;

    /**
     * 今日剩余邀请投递次数
     */
    private String size;

    public ZlIndexInfoVO() {
    }

    public ZlIndexInfoVO(String money, String size) {
        this.money = money;
        this.size = size;
    }
}

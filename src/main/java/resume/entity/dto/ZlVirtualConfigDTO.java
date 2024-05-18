package resume.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 智联配置信息DTO
 *
 * @author：周杰
 * @date: 2024/5/17
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
@Data
public class ZlVirtualConfigDTO implements Serializable {
    /**
     * 账号类型 1:智联 2:boss
     */
    private Integer accountType;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 联系人id
     */
    private Long contactId;

    /**
     * 短信通知0->关闭，1->开通
     */
    private Integer sendMessage;

    /**
     * 人资企业id
     */
    private Long hrEnterpriseId;

    /**
     * 脚本类型1->推荐人才，2->搜索人才
     */
    private Integer type;

    /**
     * 筛选条件
     */
    private String content;

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
     * 服务器id
     */
    private Long serverType;

    /**
     * 话术id
     */
    private Long verbalType;

    /**
     * 线路id
     */
    private String lineType;

    /**
     * 岗位id
     */
    private Long jobId;

    /**
     * 备选线路1
     */
    private Long spareLine1;

    /**
     * 备选线路2
     */
    private Long spareLine2;

    /**
     * 备选线路3
     */
    private Long spareLine3;

    /**
     * 备选线路4
     */
    private Long spareLine4;


}

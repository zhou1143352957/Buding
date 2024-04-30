package resume.config;

/**
 * api接口
 *
 * @author：周杰
 * @date: 2024/4/25
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public interface UrlConstant {

    /**
     * python调用查询58账号配置的筛选条件
     * http://yapi.dinggehuo.com/project/728/interface/api/58642
     * @param account 账号
     */
    String GET_CONFIG_58 = "/job-admin/virtual-deploy/getConfig58";

    /**
     * python调用保存每日简历查看数
     * http://yapi.dinggehuo.com/project/728/interface/api/58648
     * @param account 账号
     * @param type 类型 1：查看简历加1 2：查看虚拟号加1 3：再次爬取加1
     */
    String SAVE_RESUME_COUNT = "/job-admin/virtual-resume-count/saveResumeCount";

    /**
     * python调用校验是否拨打过
     * http://yapi.dinggehuo.com/project/728/interface/api/58636
     * @param type 类型1->58
     * @param name 姓名
     * @param sex  性别
     * @param basicInfo 基本信息，示例：男|25岁|大专|1-3年工作经验
     */
    String GET_BY_NAME_AND_BASIC = "/job-admin/virtual-58/getByNameAndBasic";

    /**
     * python解析加密文字
     * @param htmlcontent  解析内容
     */
    String PY_BASE_INFO = "/baseinfo";

    /**
     * python调用保存虚拟号码
     */
    String SAVE_VIRTUAL = "/job-admin/virtual-58/saveVirtual";


}

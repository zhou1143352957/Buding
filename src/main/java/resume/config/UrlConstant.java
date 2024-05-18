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

    String BASE_URL = BaseConfig.testUrl;

    /**
     * python调用查询58账号配置的筛选条件
     * http://yapi.dinggehuo.com/project/728/interface/api/58642
     *
     * @param account 账号
     */
    String GET_CONFIG_58 = BASE_URL + "/job-admin/virtual-deploy/getConfig58";

    /**
     * python调用保存每日简历查看数
     * http://yapi.dinggehuo.com/project/728/interface/api/58648
     *
     * @param account 账号
     * @param type 类型 1：查看简历加1 2：查看虚拟号加1 3：再次爬取加1
     */
    String SAVE_RESUME_COUNT = BASE_URL + "/job-admin/virtual-resume-count/saveResumeCount";

    /**
     * python调用校验是否拨打过
     * http://yapi.dinggehuo.com/project/728/interface/api/58636
     *
     * @param type 类型1->58
     * @param name 姓名
     * @param sex  性别
     * @param basicInfo 基本信息，示例：男|25岁|大专|1-3年工作经验
     */
    String GET_BY_NAME_AND_BASIC = BASE_URL + "/job-admin/virtual-58/getByNameAndBasic";

    /**
     * python解析加密文字
     *
     * @param htmlcontent  解析内容
     */
    String PY_BASE_INFO = BaseConfig.locationPyUrl + "/baseinfo";

    /**
     * python调用保存虚拟号码
     */
    String SAVE_VIRTUAL = BASE_URL + "/job-admin/virtual-58/saveVirtual";

    /**
     * python调用校验是否拨打过（根据列表附加内容） get
     *
     * @param extraInfo 附加信息 （想找：徐州鼓楼|后厨杂工|面议擅长沟通1-3年后厨经验期望的福利有朝九晚五、双休、有五险一金、离家近、长期稳定。）
     */
    String GET_BY_EXTRA_INFO = BASE_URL + "/job-admin/virtual-58/getVirtual";


    /**
     * puthon58脚本心跳
     *
     * @param account 账号
     */
    String HEART_BEAT = BASE_URL + "/job-admin/virtual-deploy/heartBeat58";


    /**
     * 验证人才招聘总后台 服务器是否异常或者 正在重启中 verifyApi
     */
    String VERIFY_API = BASE_URL + "/job-admin/api/verify";


    /**
     * 58保存元宝
     * post
     *
     * @param account 账号
     * @param totalCoin 总元宝
     * @param todayGet 今日获得的元宝
     */
    String SAVE_YUAN_BAO = BASE_URL + "/job-admin/virtual-deploy/saveYuanBao";


    /**
     * 智联招聘 心跳
     *
     * @param account 账号
     */
    String HEART_BEAT_ZL = BASE_URL + "/job-admin/virtual-config-zl/heartBeatZl";


    /**
     * 获取智联招聘时间段
     * post
     * return String [{"startTime":"08:30","endTime":"21:00"}]
     */
    String GET_VIRTUAL_ZL_TIME = BASE_URL + "/job-admin/common/config/configKey/virtual.zl.time";

    /**
     * 获取 智联 筛选头配置
     * @param account 账号
     *                get
     */
    String GET_CONFIG_ZL = BASE_URL + "/job-admin/virtual-config-zl/getConfigZl";
}

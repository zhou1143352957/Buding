import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * 说明
 *
 * @author：周杰
 * @date: 2024/5/11
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class Test4 {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // 设置Log4j2配置文件路径
        String logConfigFile = "../resources/log4j2.xml";
        Configurator.initialize(null, logConfigFile);

        // 记录日志
        logger.info("这是一个信息级别的日志消息");
        logger.error("这是一个错误级别的日志消息");
    }
}

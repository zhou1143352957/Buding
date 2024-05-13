package resume.script.split;

import com.sun.tools.javac.Main;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resume.api.Api58;

import java.util.concurrent.TimeUnit;

/**
 * API重试机制
 * 当API调用失败时，可以实现一个重试机制，以便在一定时间间隔后重新尝试调用。可以使用循环来实现这个机制，并在每次尝试之间添加一些延迟。这样可以确保在服务器重新启动后，应用程序能够继续尝试与其通信。
 *
 * @author：周杰
 * @date: 2024/5/13
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class ApiRetryMechanism {

    private static final Logger logger = LogManager.getLogger(Main.class);
    //重试次数
    private static final int MAX_RETRIES = 20;
    //延迟 xxx 秒
    private static final long RETRY_DELAY_SECONDS = 60;

    /**
     * 重试调用api
     */
    public static void callApiWithRetry() {
        int retries = 0;
        boolean success = false;
        long startTime = System.currentTimeMillis();

        while (!success && retries < MAX_RETRIES) {
            try {
                // 如果调用失败，抛出了 ApiException
                apiCall();
                // 如果调用成功，则设置 success = true, 如果没有抛出异常，则认为调用成功
                success = true;
            } catch (ApiException e) {
                retries++; // 增加重试次数
                if (retries < MAX_RETRIES) {
                    log("API调用失败，进行重试中...{} {}", startTime);
                    waitBeforeRetry();
                } else {
                    log("API调用失败，达到最大重试次数。{} {}", startTime);
                    // 在这里可以采取其他处理措施，比如记录日志、抛出异常等
                }
            }
        }
    }

    /**
     * 等待 然后重试
     */
    private static void waitBeforeRetry() {
        try {
            // 在重试之前等待一段时间
            TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 假设这是API调用方法
     *
     * @throws ApiException
     */
    private static void apiCall() throws ApiException {
        // 在这里实现调用API的逻辑
        // 如果调用失败，抛出 ApiException
        String apiResults = Api58.verifyApi();
        if (apiResults.contains("502 Bad Gateway")) {
            throw new ApiException(502, "服务器重新或掉线");
        }
    }

    private static void log(String logContent, long startTime) {
        long runTime = (System.currentTimeMillis() - startTime) / 1000;
        if (runTime <= 0L) {
            logger.error(logContent.replace("{}", ""));
            return;
        }

        if (runTime <= 60) {
            logger.error(logContent, runTime, "秒");
        } else {
            logger.error(logContent, runTime / 60, "分钟");
        }
    }

    /**
     * ApiException 可以是你自定义的异常类，用于表示 API 调用失败的情况
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class ApiException extends Exception {
        // 可以添加额外的信息或者构造函数等
        //502服务器重启/掉线, 200正常
        private Integer code;
        //信息
        private String msg;

        public ApiException() {
        }

        public ApiException(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }


}

import resume.script.split.ApiRetryMechanism;

/**
 * 说明
 *
 * @author：周杰
 * @date: 2024/5/13
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class Test5 {

    public static void main(String[] args) {
        String log = "<html>\n" +
                "<head><title>502 Bad Gateway</title></head>\n" +
                "<body>\n" +
                "<center><h1>502 Bad Gateway</h1></center>\n" +
                "<hr><center>nginx/1.19.3</center>\n" +
                "</body>\n" +
                "</html>";

        ApiRetryMechanism.callApiWithRetry();

    }
}

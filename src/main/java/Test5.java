import resume.api.ApiZl;
import resume.script.split.ApiRetryMechanism;

import java.time.LocalTime;

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
        Boolean zlTime = ApiZl.getZlTime();
        System.out.println(zlTime);

    }
}

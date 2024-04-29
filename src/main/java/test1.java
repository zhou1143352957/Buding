import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 说明
 *
 * @author：周杰
 * @date: 2024/4/28
 * @version: 1.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class test1 {

    public static void main(String[] args) throws IOException {
       /* String url = "https://jianli.58.com/resumedetail/single/3_netN_EZsnEZ5TGmunEyalEHpnvtN_eHaTEdQTGZXlEOvTe6XnG6QTpsvl-NkTEysnErflEysnEyv?seriesid=%7B%22sver%22%3A%228%22%2C%22slotid%22%3A%22pc_rencai_list_hx_rec%22%2C%22pid%22%3A%22f9a09ef82c7e48c0a4c8c1cb9982bb92%22%2C%22uuid%22%3A%2237db89c30db146cd9f5cf94600d233b6%22%2C%22sid%22%3A%2237db89c30db146cd9f5cf94600d233b6%22%7D";
        // HTML 实体
        Document document = Jsoup.parse(new URL(url), 200000);
        String specialText = document.outerHtml();
        System.out.println(specialText);*/

        // HTML实体
        String htmlEntity1 = "&#xF10E;";
        String htmlEntity2 = "&#xEC93;";
        String htmlEntity3 = "&#xE2A0;";

        // 解码HTML实体
        String decodedText1 = StringEscapeUtils.unescapeHtml4(htmlEntity1);
        String decodedText2 = StringEscapeUtils.unescapeHtml4(htmlEntity2);
        String decodedText3 = StringEscapeUtils.unescapeHtml4(htmlEntity3);

        // 输出解码后的文本
        System.out.println("Decoded Text 1: " + decodedText1);
        System.out.println("Decoded Text 2: " + decodedText2);
        System.out.println("Decoded Text 3: " + decodedText3);
    }
}

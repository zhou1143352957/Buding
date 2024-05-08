/**
 * @Description:
 * @author: 周杰
 * @date: 2024/5/8 星期三
 * @version: 1.0.0
 * Copyright Ⓒ 2020 思跑特 Computer Corporation Limited All rights reserved.
 */
public class Test3 {
    public static void main(String[] args) {
        /*String sex = "男|30岁|大专|1-3年工作经验";
        String[] infoText = sex.split("|");
        System.out.println(infoText[0].equals("男") || infoText[0].equals("女") ? infoText[0] : null);*/

     //   String str = "\uE969光辉";
        String str = " 光辉";

        // 使用正则表达式匹配非汉字字符
        boolean containsNonChinese = str.matches(".*[^\\u4E00-\\u9FA5]+.*");

        if (containsNonChinese) {
            System.out.println("字符串中包含非汉字字符");
        } else {
            System.out.println("字符串中不包含非汉字字符");
        }
    }
}

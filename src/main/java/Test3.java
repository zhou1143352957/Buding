import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

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

        String baseDirectoryPath = "C:\\Users\\Administrator\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles";
        String fileUrl = "";
        try (Stream<Path> paths = Files.walk(Paths.get(baseDirectoryPath))) {
            Optional<Path> optionalPath = paths.filter(Files::isDirectory)
                    .filter(path -> path.toString().endsWith(".default-release"))
                    .findFirst();

            if (optionalPath.isPresent()) {
                fileUrl = optionalPath.get().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 打印路径
        System.out.println("Found directory: " + fileUrl);
    }
}

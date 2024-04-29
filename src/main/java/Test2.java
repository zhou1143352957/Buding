import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * 说明
 *
 * @author：周杰
 * @date: 2024/4/29
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 **/
public class Test2 {


    public static void main(String[] args) {
         Tesseract tesseract = new Tesseract();
        // 设置 Tesseract 的数据路径
        tesseract.setDatapath("../java/tessdata");
        tesseract.setLanguage("chi_sim");
        try {
            String recognizedText = tesseract.doOCR(new File("../java/pic/image.png"));
            System.out.println("识别文本: " + recognizedText);
        } catch (TesseractException e) {
            System.out.println("识别文本时出错: " + e.getMessage());
        }
    }
}

import com.sipaote.common.exception.BusinessException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import resume.util.CommonUtil;

import java.io.File;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

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
        String fileUrl = "E:\\image\\resumeName.png";
        // 加载 OpenCV 本地库
        System.load("E:\\opencv\\opencv\\build\\java\\x64\\opencv_java490.dll");
        // 读取图像
        Mat image = Imgcodecs.imread(fileUrl);
        // 转换图像为灰度图像
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        // 保存预处理后的图像
        String preprocessedFilePath = fileUrl;
     //   imwrite(preprocessedFilePath, grayImage);
      //  CommonUtil.GaussianBlur(preprocessedFilePath);
           Imgproc.bilateralFilter(image,grayImage, 9, 75, 75); //双边滤波
        //二值化
      //  Imgproc.threshold(image, grayImage, 178, 305, Imgproc.THRESH_BINARY);


        // 保存二值化后的图像
        Imgcodecs.imwrite(fileUrl, grayImage);
        // 使用 Tesseract 进行文字识别
        try {
            Tesseract tesseract = new Tesseract();
            String os = System.getProperty("os.name").toLowerCase();
            boolean isWindows = os.contains("windows");
            //如果是win系统
            //图片路径文件夹
            if (isWindows) {
                // 设置 Tesseract 的数据路径
                tesseract.setDatapath("E:\\TesseractOCR\\tessdata");
            } else {
                tesseract.setDatapath("");
            }
            tesseract.setLanguage("chi_sim");
            String recognizedText = tesseract.doOCR(new File(fileUrl));
            recognizedText =  recognizedText.replace("，", "|").replace(" ","").replace("$", "|");
            System.out.println("识别文本: " + recognizedText);
        } catch (TesseractException e) {
            System.out.println("识别文本时出错: " + e.getMessage());
            throw new BusinessException("");
        }
    }
}

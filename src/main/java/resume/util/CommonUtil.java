package resume.util;

import cn.hutool.core.img.ImgUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sipaote.common.constant.StringPoolConstant;
import com.sipaote.common.exception.BusinessException;
import com.sipaote.common.validator.ValidatorUtil;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.URLDecoder;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * 说明
 *
 * @author: 饶靖
 * @date: 2022/11/22
 * @version: 1.0.0
 * Copyright Ⓒ 2022 恒翔 Computer Corporation Limited All rights reserved.
 */
public class CommonUtil {

    /**
     * @param isNow true表示查询当前日期范围  false表示查询上一个日期范围
     * @return
     */
    public static Map<String, String> getTypeDate(String startTime, String endTime, Integer type, Boolean isNow) {
        Map<String, String> map = new HashMap();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        switch (type) {
            case 0:
                if (!isNow) {
                    LocalDate startDate = LocalDate.parse(startTime, fmt);
                    LocalDate endDate = LocalDate.parse(endTime, fmt);
                    Long days = startDate.until(endDate, ChronoUnit.DAYS);
                    startTime = startDate.minusDays(days + 1).format(fmt);
                    endTime = startDate.minusDays(1).format(fmt);
                }
                break;
            case 1:
                if (!isNow) {
                    now = now.minusDays(7);
                }
                int dayOfWeek = now.getDayOfWeek().getValue();
                startTime = now.minusDays(dayOfWeek - 1).with(LocalTime.MIN).format(fmt);
                endTime = now.plusDays(7 - dayOfWeek).with(LocalTime.MAX).format(fmt);
                break;
            case 2:
                if (!isNow) {
                    now = now.minusMonths(1);
                }
                startTime = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN).format(fmt);
                endTime = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX).format(fmt);
                break;
            case 3:
                if (!isNow) {
                    now = now.minusMonths(3);
                }
                startTime = getStartOrEndDayOfQuarter(now.toLocalDate(), true).format(fmt);
                endTime = getStartOrEndDayOfQuarter(now.toLocalDate(), false).format(fmt);
                break;
            case 4:
                if (!isNow) {
                    now = now.minusYears(1);
                }
                startTime = now.with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN).format(fmt);
                endTime = now.with(TemporalAdjusters.lastDayOfYear()).with(LocalTime.MIN).format(fmt);
                break;
            default:
                break;
        }
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }

    /**
     * 根据上下级 获取实际城市数组信息
     *
     * @param cityCode
     * @return
     */
    public static List<Long> getCityCodeIds(String cityCode) {
        List<Long> result = new ArrayList<>();
        if (ValidatorUtil.isNull(cityCode)) {
            return null;
        }
        JSONArray cityCodes;
        try {
            cityCodes = JSON.parseArray(cityCode);
        } catch (Exception e) {
            if (cityCode.contains(",")) {
                List<Long> citys = Arrays.stream(cityCode.split(",")).map(s -> Long.parseLong(s)).collect(Collectors.toList());
                return citys;
            } else {
                result.add(Long.valueOf(cityCode));
                return result;
            }
        }
        cityCodes.stream().forEach(data -> {
            JSONArray jsonArray = JSON.parseArray(data.toString());
            if (jsonArray.size() < 2) {
                return;
            }
            result.add(Long.valueOf(jsonArray.get(jsonArray.size() - 1).toString()));
        });
        return result;
    }


    /**
     * 获取当前日期所在季度的开始日期和结束日期
     * 季度一年四季， 第一季度：1月-3月， 第二季度：4月-6月， 第三季度：7月-9月， 第四季度：10月-12月
     *
     * @param isFirst true表示查询本季度开始日期  false表示查询本季度结束日期
     * @return
     */
    public static LocalDate getStartOrEndDayOfQuarter(LocalDate today, Boolean isFirst) {
        LocalDate resDate = today;
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
        }
        return resDate;
    }

    /**
     * 绘制文字边框（标签）
     *
     * @param texts 绘制文本 String texts ="你好", "世界", "Java绘图", "Graphics2D";
     **/
    public static int textFrame(Graphics2D g2d, Integer fontSize, String fontColor, String texts, String rectangleBackgroundColor,
                                String frameColor, Integer maxWidth, Integer x, Integer y) {
        return textFrame(g2d, fontSize, fontColor, texts.split(StringPoolConstant.COMMA), rectangleBackgroundColor, frameColor, maxWidth, x, y);
    }

    /**
     * 绘制文字边框（标签）
     *
     * @param g2d                      画布
     * @param fontSize                 字体大小
     * @param fontColor                字体颜色
     * @param texts                    绘制文本 String[] texts = {"你好", "世界", "Java绘图", "Graphics2D"};
     * @param rectangleBackgroundColor 矩形背景颜色
     * @param frameColor               边框颜色
     * @param maxWidth                 固定长度，达到此长度时自动换行
     * @param x                        x轴
     * @param y                        y轴
     * @author: 周杰
     * @date: 2023/3/31
     * @version: 1.0.0
     */
    public static int textFrame(Graphics2D g2d, Integer fontSize, String fontColor, String[] texts, String rectangleBackgroundColor,
                                String frameColor, Integer maxWidth, Integer x, Integer y) {
        //换行初始化X轴
        Integer initializationX = x;
        // 设置字体和颜色
        g2d.setFont(new Font("黑体", Font.PLAIN, fontSize));
        // 绘制文本
        int padding = 10; // 边框距离文本的距离
        int borderThickness = 2; // 边框的厚度
        //     int borderOffset = borderThickness / 2; // 边框的偏移量
        int cornerRadius = 6; // 圆角半径
        int lineHeight = 0; // 当前行的高度
        for (String text : texts) {
            FontMetrics fontMetrics = g2d.getFontMetrics();
            Rectangle2D rect = fontMetrics.getStringBounds(text, g2d);
            int textWidth = (int) rect.getWidth();
            int textHeight = (int) rect.getHeight();
            if (x + textWidth + 2 * padding > maxWidth) {
                // 换行
                x = initializationX;
                //换行上下间距 加10
                y += lineHeight + 10;
                lineHeight = 0;
            }
            int boxWidth = textWidth + 2 * padding;
            int boxHeight = textHeight + 2 * padding;
            int boxX = x;
            int boxY = y + (int) (fontMetrics.getAscent() - fontMetrics.getHeight() / 2);
            g2d.setColor(ImgUtil.getColor(rectangleBackgroundColor));
            RoundRectangle2D.Double box = new RoundRectangle2D.Double(boxX, boxY, boxWidth, boxHeight, cornerRadius, cornerRadius);
            g2d.fill(box);
            g2d.setColor(ImgUtil.getColor(frameColor));
            g2d.setStroke(new BasicStroke(borderThickness));
            g2d.draw(box);
            //设置字体颜色
            g2d.setColor(ImgUtil.getColor(fontColor));
            g2d.drawString(text, x + padding, y + (int) (boxHeight / 2 + fontMetrics.getAscent() / 2) + 6);
            x += textWidth + 3 * padding;
            lineHeight = Math.max(lineHeight, boxHeight);
        }
        return y;
    }

    /**
     * 绘图文字换行
     *
     * @param g          图版
     * @param strContent 文字内容
     * @param loc_X      X
     * @param loc_Y      Y
     * @param font       字体
     * @param rowWidth   每一行字符串宽度
     * @author: 周杰
     * @date: 2023/3/30
     * @version: 1.0.0
     */
    public static int drawStringWithFontStyleLineFeed(Graphics g, String strContent, int loc_X, int loc_Y, Font font, int rowWidth) {
        int y = loc_Y;
        g.setFont(font);
        //获取字符串 字符的总宽度
        int strWidth = getStringLength(g, strContent);
        //  System.out.println("每行字符宽度:" + rowWidth);
        //获取字符高度
        int strHeight = getStringHeight(g);
        //字符串总个数
        // System.out.println("字符串总个数:" + strContent.length());
        if (strWidth > rowWidth) {
            int rowstrnum = getRowStrNum(strContent.length(), rowWidth, strWidth);
            int rows = getRows(strWidth, rowWidth);

            String temp = StringPoolConstant.EMPTY;
            for (int i = 0; i < rows; i++) {
                //获取各行的String
                if (i == rows - 1) {
                    //最后一行
                    temp = strContent.substring(i * rowstrnum, strContent.length());
                } else {
                    temp = strContent.substring(i * rowstrnum, i * rowstrnum + rowstrnum);
                }
                if (i > 0) {
                    //第一行不需要增加字符高度，以后的每一行在换行的时候都需要增加字符高度
                    loc_Y = loc_Y + strHeight;
                    y += strHeight;
                }
                g.drawString(temp, loc_X, loc_Y);
            }
        } else {
            //直接绘制
            g.drawString(strContent, loc_X, loc_Y);
        }
        return y;
    }

    /**
     * 字符高度
     *
     * @param g
     * @return
     */
    private static int getStringHeight(Graphics g) {
        int height = g.getFontMetrics().getHeight();
        //  System.out.println("字符高度:" + height);
        return height;
    }


    /**
     * 字符行数
     *
     * @param strWidth
     * @param rowWidth
     * @return
     */
    private static int getRows(int strWidth, int rowWidth) {
        int rows = 0;
        if (strWidth % rowWidth > 0) {
            rows = strWidth / rowWidth + 1;
        } else {
            rows = strWidth / rowWidth;
        }
        //  System.out.println("行数:" + rows);
        return rows;
    }

    private static int getRowStrNum(int strnum, int rowWidth, int strWidth) {
        int rowstrnum = 0;
        rowstrnum = (rowWidth * strnum) / strWidth;
        //   System.out.println("每行的字符数:" + rowstrnum);
        return rowstrnum;
    }

    /**
     * 字符串总宽度
     *
     * @param g
     * @param str
     * @return
     */
    private static int getStringLength(Graphics g, String str) {
        char[] strcha = str.toCharArray();
        int strWidth = g.getFontMetrics().charsWidth(strcha, 0, str.length());
        //   System.out.println("字符总宽度:" + strWidth);
        return strWidth;
    }


    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;
    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";

    /**
     * 获取距当前时间差值
     *
     * @param beforeTime
     * @return
     */
    public static String format(LocalDateTime beforeTime) {
        long compareTime = getTimestampOfDateTime(beforeTime);
        LocalDateTime now = LocalDateTime.now();
        long currentTime = getTimestampOfDateTime(now);
        long differTime = currentTime - compareTime;

        if (differTime < 1L * ONE_MINUTE) {
            long seconds = toSeconds(differTime);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }

        if (differTime < 60L * ONE_MINUTE) {
            long minutes = toMinutes(differTime);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (differTime < 24L * ONE_HOUR) {
            long hours = toHours(differTime);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (differTime < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (differTime < 7L * ONE_DAY) {
            long days = toDays(differTime);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        } else {
            return "7天前";
        }
        /*if (differTime < 30L * ONE_DAY) {
            long days = toDays(differTime);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (differTime < 12L * 4L * ONE_WEEK) {
            long months = toMonths(differTime);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(differTime);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }*/
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }


    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 对List<Map<String,Object>>中int类型字段进行排序
     *
     * @param lists
     * @param sort  排序，asc/desc
     * @param key   排序字段
     * @return
     */
    public static List<Map<String, Object>> sortIntMap(List<Map<String, Object>> lists, String sort, String key) {
        if (sort.equals("asc")) {
            lists.sort(Comparator.comparingInt(o -> Integer.parseInt(o.get(key).toString())));
        } else {
            Collections.sort(lists, (o1, o2) -> Integer.parseInt(o2.get(key).toString()) - Integer.parseInt(o1.get(key).toString()));
        }
        return lists;
    }

    /**
     * 对List<Map<String,Object>>中double类型字段进行排序
     *
     * @param lists
     * @param sort  排序，asc/desc
     * @param key   排序字段
     * @return
     */
    public static List<Map<String, Object>> sortDoubleMap(List<Map<String, Object>> lists, String sort, String key) {
        if (sort.equals("asc")) {
            lists.sort(Comparator.comparingDouble(o -> Double.parseDouble(o.get(key).toString())));
        } else {
            Collections.sort(lists, (o1, o2) -> {
                Double d1 = Double.parseDouble(o2.get(key).toString());
                Double d2 = Double.parseDouble(o1.get(key).toString());
                if (d1 > d2) {
                    return 1;
                }
                return -1;
            });
        }
        return lists;
    }


    /**
     * 字符串转文件
     */
    public static String convertToTxt(String text, String filePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(text);
            writer.close();
            System.out.println("字符串已成功保存为txt文件！");
        } catch (IOException e) {
            System.out.println("保存文件时发生错误：" + e.getMessage());
        }
        return filePath;
    }

    /**
     * 获取范围之内随机数
     */
    public static Long getRandom() {
        return ThreadLocalRandom.current().nextLong(2, 4);
    }

    /**
     * 获取范围之内随机数
     */
    public static Long getRandom(long start, long end) {
        return ThreadLocalRandom.current().nextLong(start, end);
    }

    /**
     * 随机范围之内毫秒
     */
    public static Long getRandomMillisecond(long start, long end) {
        return ThreadLocalRandom.current().nextLong(start, end) * 1000;
    }
    /**
     * 随机毫秒
     */
    public static Long getRandomMillisecond() {
        return ThreadLocalRandom.current().nextLong(4, 9) * 1000;
    }

    /**
     * 判断字符串里面有不是中文
     * @return true 有不是中文的字符串 false 都是中文
     */
    public static Boolean isChinese(String content){
        // 使用正则表达式匹配非汉字字符
        content = content.replace(" ", "");
        return content.matches(".*[^\\u4E00-\\u9FA5]+.*");
    }


    /**
     * 解码 URL 编码的字符串
     *
     * @param urlEncodedString
     * @return
     */
    public static String decodeURL(String urlEncodedString) {
        try {
            return URLDecoder.decode(urlEncodedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // 解码异常处理
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 高斯滤波
     * 在图像处理中，高斯滤波通常会导致图像的模糊效果，这是因为高斯滤波器的目的之一就是模糊图像以降低图像中的噪声。这种模糊效果是通过对图像中的每个像素及其周围像素进行加权平均来实现的，而权重则是由高斯函数计算得出的。
     * 高斯滤波的模糊效果受到两个主要因素的影响：
     * 1. **内核大小（Kernel Size）：**内核大小决定了滤波器的大小，也就是滤波器在图像上移动的区域大小。内核越大，滤波器影响的像素越多，从而导致图像模糊程度增加。
     * 2. **标准差（Standard Deviation）：**标准差决定了高斯函数的形状，即权重的分布范围。标准差越大，权重分布越广，模糊效果越明显；标准差越小，权重分布越窄，模糊效果越轻微。
     * 因此，如果你在应用高斯滤波时发现图像变得更模糊了，可能是因为你选择了较大的内核大小或较大的标准差。你可以尝试调整这些参数，以获得更符合你需求的滤波效果。另外，也可以考虑尝试其他的图像处理技术，如中值滤波等，以达到更好的去噪效果。
     */
    public static void GaussianBlur(String path) {
        Mat src = Imgcodecs.imread(path);
        Mat dst = src.clone();
        // 定义内核大小和标准差
        Size kernelSize = new Size(3, 3); // 内核大小为 5x5
        double sigmaX = 0; // X 方向的标准差（如果设置为0，则从内核大小计算）
        double sigmaY = 0; // Y 方向的标准差（如果设置为0，则与 sigmaX 相同）
        Imgproc.GaussianBlur(src, dst, kernelSize, sigmaX, sigmaY, Core.BORDER_DEFAULT);
        imwrite(path, dst);


    }


    /**
     * OpenCV 图片识别文字
     */
    public static String openCvOCR(String fileUrl) {
     //   System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // 加载 OpenCV 本地库
        System.load("E:\\opencv\\opencv\\build\\java\\x64\\opencv_java490.dll");
        // 读取图像
        Mat image = Imgcodecs.imread(fileUrl);
        // 转换图像为灰度图像
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        // 保存预处理后的图像
        String preprocessedFilePath = fileUrl;
        imwrite(preprocessedFilePath, grayImage);
        if (fileUrl.contains("resumeName")){
            Imgproc.bilateralFilter(image,grayImage, 9, 75, 75); //双边滤波
       //     Imgproc.threshold(image, grayImage, 178, 305, Imgproc.THRESH_BINARY);//二化值
            imwrite(preprocessedFilePath, grayImage);
        } else {
            GaussianBlur(preprocessedFilePath);//高斯滤波
        }
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
            recognizedText =  recognizedText.replace("，", "|").replace(" ","")
                    .replace("$", "|").replace("+", "*");

            System.out.println("识别文本: " + recognizedText);
            return recognizedText;
        } catch (TesseractException e) {
            System.out.println("识别文本时出错: " + e.getMessage());
            throw new BusinessException("");
        }


    }


}

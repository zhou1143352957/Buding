## Buding
## 项目概述
Buding 是一个用 Java 编写的数据采集工具，旨在从目标网站上爬取和提取有价值的信息。项目使用了多种框架和工具，包括 Selenium、OpenCV 和 Tesseract OCR（tess4j），以实现高效且准确的数据采集。

## 功能特性
- **自动化网页导航**：使用 Selenium 实现浏览器自动化，自动登录、点击、输入等操作。
- **图像处理**：通过 OpenCV 进行图像预处理，以提高 OCR 的识别准确性。
- **文本识别**：利用 Tesseract OCR（tess4j）从图像中提取文本内容。
- **数据存储**：将提取的数据保存到本地文件或数据库中，方便后续分析和处理。

## 技术栈
- **编程语言**：Java 17
- **自动化框架**：Selenium
- **图像处理**：OpenCV
- **光学字符识别 (OCR)**：Tesseract OCR

## 环境要求

- JDK 17
- Maven 3.6+
- 浏览器驱动（如 ChromeDriver）

## 安装与运行

### 配置项目
- **1. 确保已安装 JDK 17 和 Maven。**
- **2. 在 src/main/resources 目录下创建一个配置文件 config.properties，填写所需的配置信息，如目标网站 URL、登录凭据等。**

### 编译项目
- **mvn clean install**
### 运行项目
- **java -cp /url/local/Buding.jar resume/script/CrawlerResume**


### 克隆项目
```bash
git clone https://github.com/zhou1143352957/Buding.git
cd Buding



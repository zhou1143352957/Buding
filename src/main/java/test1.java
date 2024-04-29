import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;

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


        // 配置网络
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.001))
                .list()
                .layer(new ConvolutionLayer.Builder()
                        .nIn(1) // 输入通道数
                        .nOut(64) // 输出通道数
                        .kernelSize(3, 3)
                        .activation(Activation.RELU)
                        .build())
                .layer(new ConvolutionLayer.Builder()
                        .nOut(128)
                        .kernelSize(3, 3)
                        .activation(Activation.RELU)
                        .build())
                .layer(new ConvolutionLayer.Builder()
                        .nOut(256)
                        .kernelSize(3, 3)
                        .activation(Activation.RELU)
                        .build())
                .layer(new GravesLSTM.Builder()
                        .nOut(256)
                        .activation(Activation.TANH)
                        .build())
                .layer(new RnnOutputLayer.Builder()
                        .activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .nOut(256) // 输出类别数（根据你的数据集进行更改）
                        .build())
                .build();

        // 构建并初始化网络
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        // 训练网络
        int numEpochs = 10;
        for (int i = 0; i < numEpochs; i++) {
            // 在这里加载和处理数据集，然后使用net.fit()来训练模型
            System.out.println("Epoch " + i + " complete");
        }

        // 在测试集上评估网络性能
        // 在这里加载测试集数据，然后使用net.evaluate()来评估模型

        // 使用网络进行预测
        // 在这里加载需要预测的数据，然后使用net.output()来获取预测结果


    }
}

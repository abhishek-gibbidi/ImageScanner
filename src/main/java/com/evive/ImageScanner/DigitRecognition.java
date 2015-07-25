package com.evive.ImageScanner;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DigitRecognition {
    public static final Logger LOG = LoggerFactory.getLogger(DigitRecognition.class);
    static Properties properties = Utils.getProperties();

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // gr.assignLabels();
        for (int i = 0; i < 70; i++) {

            final StringBuilder str = new StringBuilder(String.valueOf(properties.get("inputFormFolder")));
            str.append("form-").append(i).append(String.valueOf(properties.get("IMAGE_EXTENSTION")));
            final StringBuilder imageName = new StringBuilder();
            imageName.append("form-").append(i).append(".png");
            LOG.info(
                    "***********************************************************IMAGE : {} ********************************************",
                    str);
            final StringBuilder csvName = new StringBuilder(String.valueOf(properties.get("csvFolder")));
            csvName.append("form-").append(i).append(".csv");
            List<List<Point>> squares = new ArrayList<>();
            LOG.info("Path : {}", str);
            final Mat image = Highgui.imread(str.toString());
            // Highgui.imwrite(imageName.toString(), image);
            Imgproc.resize(image, image, new Size(1000, 1000));
            squares = GetRectangles.findSquareV2(image, squares);
            LOG.info("Squares Size : {}", squares.size());
            FeactureExtractor.collectHOG(image, squares, csvName.toString());

        }
       // final RandomForestModel rfModel =
         //       Classifier.trainClassifier("/home/abhishek/Desktop/HWR/DigitRecognition/HOGfea.csv", ",");
        //Classifier.perdictLabels(rfModel, "/home/abhishek/Desktop/HWR/DigitRecognition/csvFolder/form-1.csv", ",");
    }
}

package com.evive.ImageScanner;

import com.evive.spark.classifier.ClassifierRandomForest;
import com.evive.spark.util.MetricUtil;
import com.evive.spark.util.UtilitySpark;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Classifier {
    public static final Logger LOG = LoggerFactory.getLogger(Classifier.class);

    public static RandomForestModel trainClassifier(String path, String delemiter) {
        final SparkConf sparkConf = new SparkConf().setAppName("LRImageRecognizer").setMaster("local[*]");

        final JavaSparkContext sc = new JavaSparkContext(sparkConf);

        final ClassifierRandomForest rfClassifier = new ClassifierRandomForest();
        final String[] args1 = { "", path, delemiter };
        final JavaRDD<LabeledPoint> training = UtilitySpark.fromCmdArgumentsToLabeledPointJavaRDD(sc, args1);
        training.cache();
        rfClassifier.setNumClasses(11);
        rfClassifier.setNumTrees(100);
        final RandomForestModel rfModel = rfClassifier.trainRandomForestClassifier(training);

        LOG.info("no of training :\n {}", training.count());
        LOG.info("no of training features:\n {}", training.first().features().size());


        LOG.info("Learned classification model:\n {}", rfModel.toString());


        LOG.info("The rf accuracy is {} ", MetricUtil.calculateAccuracy(rfModel, training));

        sc.close();
        return rfModel;
    }

    public static void perdictLabels(RandomForestModel rfModel, String path, String delemitier) {
        final SparkConf sparkConf = new SparkConf().setAppName("LRImageRecognizer").setMaster("local[*]");

        final StringBuilder str = new StringBuilder(path);
        str.delete(str.length() - 4, str.length());
        final JavaSparkContext sc = new JavaSparkContext(sparkConf);

        final String[] args1 = { "", path, delemitier };

        final JavaRDD<Vector> testing2 = UtilitySpark.fromCmdArgumentsToVectorJavaRDD(sc, args1);

        final JavaRDD<LabeledPoint> predictedT = testing2.map(f -> {
            return new LabeledPoint(rfModel.predict(f), f);
        });

        final JavaRDD<Double> labelsPredicted = predictedT.map(f -> f.label());
        labelsPredicted.coalesce(1).saveAsTextFile(str.toString());

        LOG.info("Predictedd labels : {}", labelsPredicted.collect());
        for (final LabeledPoint row : predictedT.collect()) {

            LOG.info(" {} ", row);

        }
        sc.stop();

    }
}

package com.evive.ImageScanner;

import org.opencv.core.Rect;

import org.opencv.core.Point;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author abhishek
 *
 */
public class FeactureExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(FeactureExtractor.class);



    public static void collectHOG(Mat image, List<List<Point>> squares, String filename) {

        if (image.empty() || squares.isEmpty()) {
            LOG.error("Error in the parameters");

        } else {
            LOG.info("Computing HOG");
            final List<List<Point>> sqr = ProcessForm.sortPtsAccordY(squares);
            List<List<Point>> sqr1 = ProcessForm.sortSquaresInLine(sqr);
            sqr1 = ProcessForm.removeNonNumeric(sqr1);
            final String COMMA_DELIMITER = ",";
            final String NEW_LINE_SEPARATOR = "\n";
            LOG.info("Writing to csv");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(filename);
                for (int i = 0; i < sqr1.size(); i++) {
                    // LOG.info("Squares : {}",sqr1.get(i));
                    final MatOfPoint mat = new MatOfPoint();
                    mat.fromList(sqr1.get(i));
                    final Rect rect = Imgproc.boundingRect(mat);
                    final Mat roi = new Mat(image, rect);
                    // String name = i + ".jpg";
                    // Highgui.imwrite(name,roi);
                    final MatOfFloat descriptor = FeactureExtractor.getHOGFeatures(roi);
                    final List<Float> featureVector = descriptor.toList();
                    try {
                        for (int j = 0; j < featureVector.size(); j++) {
                            fileWriter.append(String.valueOf(featureVector.get(j)));
                            fileWriter.append(COMMA_DELIMITER);
                        }
                        fileWriter.append(NEW_LINE_SEPARATOR);
                    } catch (final Exception e) {
                        LOG.error(e.getMessage(), e.toString());
                    }
                }

            } catch (final IOException e1) {
                LOG.error(e1.getMessage(), e1.toString());
            } finally {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (final IOException e) {
                    LOG.info("Error while flushing/closing fileWriter");
                    LOG.error(e.getMessage(), e.toString());
                }
            }


        }
    }

    public static MatOfFloat getHOGFeatures(Mat image) {

        final Mat processImage = preProcessImage(image);
        final Mat final_image = removeBoundries(processImage);
        Imgproc.resize(final_image, final_image, new Size(64, 64));
        final HOGDescriptor hog =
                new HOGDescriptor(new Size(32, 32), new Size(32, 32), new Size(16, 16), new Size(16, 16), 9);
        final MatOfPoint locations = new MatOfPoint();
        final MatOfFloat descriptors = new MatOfFloat();
        hog.compute(final_image, descriptors, new Size(32, 32), new Size(0, 0), locations);
        LOG.info(" Descriptor : {} ", descriptors.toList());
        return descriptors;
    }

    public static Mat preProcessImage(Mat image) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(image, image, new Size(9, 9), 0, 0);
        Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11,
                2);
        return image;
    }

    public static Mat removeBoundries(Mat image) {
        final Mat new_image = image;
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                if (i > 10 && j < image.cols() - 5 && i < image.rows() - 5 && j > 10) {
                    if (image.get(i, j)[0] < 255) {
                        new_image.put(i, j, 0);
                    } else {
                        new_image.put(i, j, 255);
                    }
                } else {
                    new_image.put(i, j, 255);
                }
            }
        }

        return new_image;
    }

}

package com.evive.ImageScanner;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 
 * @author abhishek
 *
 */
public class GetRectangles {
    private static final Logger LOG = LoggerFactory.getLogger(GetRectangles.class);

    static Properties properties = Utils.getProperties();



    /**
     * 
     * @param Point pt1
     * @param Point pt2
     * @param Point pt0
     * @return double
     */
    public static double angle(final Point pt1, final Point pt2, final Point pt0) {
        final double dx1 = pt1.x - pt0.x;
        final double dy1 = pt1.y - pt0.y;
        final double dx2 = pt2.x - pt0.x;
        final double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }


    /**
     * Assign labels to the predicated digits.
     */
    public static void assignLabels() {
        final List<Integer> numSquares;
        final List<String> squareNames;
        List<String> predictedLabels = new ArrayList<String>();



        numSquares = new ArrayList<Integer>();

        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_1")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_2")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_3")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_4")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_5")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_6")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_7")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_8")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_9")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_10")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_11")));
        numSquares.add(Integer.valueOf(properties.getProperty("FIELDS_IN_INPUT_12")));


        squareNames = new ArrayList<>();

        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_1"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_2"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_3"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_4"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_5"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_6"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_7"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_8"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_9"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_10"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_11"));
        squareNames.add(properties.getProperty("NAME_OF_INPUT_FEILDS_12"));

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get("src/main/resources/input.txt"))) {
            predictedLabels = bufferedReader.lines().collect(Collectors.toList());

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }


        StringBuilder stringBuilder;
        int index = 0;
        for (final String sqName : squareNames) {
            stringBuilder = new StringBuilder();
            for (int i = 0; i < numSquares.get(squareNames.indexOf(sqName)); i++) {
                stringBuilder.append(predictedLabels.get(index));
                index++;
            }
            LOG.info("{} : {}", sqName, stringBuilder.toString());

        }

    }

    /**
     * 
     * @param Mat image
     * @param List<List<Point> > squares Given a image this function locates all the squares present in the image.
     */
    public static List<List<Point>> findSquares(final Mat image, List<List<Point>> squares) {
        LOG.info("Entered findSquares");
        squares.clear();
        image.cols();
        final int distPercentageThreshold = Integer.valueOf(properties.getProperty("distPercentageThreshold"));
        final int distThreshold = image.cols() * distPercentageThreshold / 100;
        final Mat pyr = new Mat();
        final Mat gray = new Mat();
        Imgproc.pyrDown(image, pyr, new Size(image.cols() / 2, image.rows() / 2));
        final Mat timg = new Mat();
        Imgproc.pyrUp(pyr, timg, image.size());
        final List<MatOfPoint> contours = new ArrayList<>();
        LOG.info("Starting different color pannel thresholds");
        for (int i = 0; i < Integer.valueOf(properties.getProperty("COLOR_PANNEL_THRESHOLD")); i++) {
            final MatOfInt ch = new MatOfInt(i, 0);
            final Mat gray0 = new Mat(image.size(), CvType.CV_8U);
            final List<Mat> src = Arrays.asList(timg);
            final List<Mat> des = Arrays.asList(gray0);
            // LOG.info("Mixing channels");
            Core.mixChannels(src, des, ch);
            // LOG.info("Mixed Channels");
            for (int j = 0; j < Integer.valueOf(properties.getProperty("THRESHOLD_LEVEL_TRY")); j++) {
                if (j == 0) {
                    // LOG.info("Trying for threshold Zero");
                    final int CONTRAST_THRESHOLD = Integer.valueOf(properties.getProperty("CONTRAST_THRESHOLD"));
                    // LOG.info("Using canny");
                    Imgproc.Canny(gray0, gray, 0, CONTRAST_THRESHOLD, 5, false);
                    // LOG.info("Dilating image");
                    Imgproc.dilate(gray, gray, new Mat(), new Point(-1, -1), 1);
                } else {
                    // LOG.info("Trying for non zero thresholds {} ", j);
                    for (int rows = 0; rows < image.rows(); rows++) {
                        for (int cols = 0; cols < image.cols(); cols++) {
                            if (gray0.get(rows, cols)[0] >= (j + 1) * 255
                                    / Integer.valueOf(properties.getProperty("THRESHOLD_LEVEL_TRY"))) {
                                // LOG.info("Channging value of gray{} {} {}",rows,cols,gray0.get(rows, cols)[0]);
                                gray.put(rows, cols, gray0.get(rows, cols)[0]);
                            }
                        }
                    }
                }
                // LOG.info("Changed values of gray ");
                final Mat hierarchy = new Mat();
                // LOG.info("Finding contours .... ");
                Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                LOG.info("Got contours {} ", contours.toArray());
                // final MatOfPoint2f approx = new MatOfPoint2f();
                LOG.info("Contour size : {}", contours.size());

                LOG.info("Calculating approxPolyDP");
                final MatOfPoint2f mMOP2f2 = new MatOfPoint2f();
                final Mat contoursFrame = image;
                for (int index = 0; index < contours.size(); index++) {
                    // Convert contours(i) from MatOfPoint to MatOfPoint2f
                    final MatOfPoint2f mMOP2f1 = new MatOfPoint2f(contours.get(index).toArray());
                    // Processing on mMOP2f1 which is in type MatOfPoint2f
                    Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, Imgproc.arcLength(mMOP2f1, true) * 0.02, true);
                    // Convert back to MatOfPoint and put the new values back into the contours list
                    final MatOfPoint points = new MatOfPoint(mMOP2f2.toArray());
                    // Get bounding rect of contour
                    final Rect rect = Imgproc.boundingRect(points);

                    Core.rectangle(contoursFrame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 1, 8, 0);


                }
                Highgui.imwrite("Contours.png", contoursFrame);

                for (int k = 0; k < contours.size(); k++) {

                    // Convert contours(i) from MatOfPoint to MatOfPoint2f
                    final MatOfPoint2f mMOP2f1 = new MatOfPoint2f(contours.get(k).toArray());
                    // Processing on mMOP2f1 which is in type MatOfPoint2f
                    Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, Imgproc.arcLength(mMOP2f1, true) * 0.02, true);
                    // Convert back to MatOfPoint and put the new values back into the contours list
                    final MatOfPoint points = new MatOfPoint(mMOP2f2.toArray());
                    final List<Point> approx_list = mMOP2f2.toList();
                    // LOG.info("Got approx List {} ", approx_list);

                    if (mMOP2f2.toList().size() == 4 && Math.abs(Imgproc.contourArea(points)) > 1000
                            && Imgproc.isContourConvex(points)) {
                        double maxCosine = 0;
                        // LOG.info("Got approx List in IF {} ", approx_list);
                        for (int m = 2; m < 5; ++m) {
                            // find the maximum cosine of the angle between joint edges
                            final double cosine =
                                    Math.abs(angle(approx_list.get(m % 4), approx_list.get(m - 2),
                                            approx_list.get(m - 1)));
                            maxCosine = Math.max(maxCosine, cosine);
                        }
                        LOG.info("Edge List : {}", approx_list);
                        LOG.info("MAX Cosine {}", maxCosine);
                        // if cosines of all angles are small
                        // (all angles are ~90 degree) then write quandrange
                        // vertices to resultant sequence
                        if (maxCosine < Double.valueOf(properties.getProperty("COSINE_THRESHOLD"))) {
                            final List<List<Point>> newSquare =
                                    ProcessForm.storeUniqueRectangles(approx_list, squares, distThreshold);
                            // LOG.info("New squares  after storeing : {}, size : {} ",newSquare.toArray(),newSquare.size()
                            // );
                            squares = newSquare;
                        }
                    }
                }
            }
        }
        return squares;
    }

    public static List<List<Point>> findSquareV2(Mat image, List<List<Point>> squares) {

        LOG.info("Entered findSquares");
        squares.clear();
        image.cols();
        final int distPercentageThreshold = Integer.valueOf(properties.getProperty("distPercentageThreshold"));
        final int distThreshold = image.cols() * distPercentageThreshold / 100;
        // final Mat pyr = new Mat();
        final Mat gray = new Mat();
        final List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);

        // final int CONTRAST_THRESHOLD = Integer.valueOf(properties.getProperty("CONTRAST_THRESHOLD"));
        // LOG.info("Using canny");
        // Imgproc.Canny(gray, gray, 0, CONTRAST_THRESHOLD, 5, false);
        // Highgui.imwrite("Canny.png", gray);
        // // LOG.info("Dilating image");
        // Imgproc.dilate(gray, gray, new Mat(), new Point(-1, -1), 1);

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 2, 2);
        Imgproc.adaptiveThreshold(gray, gray, 255, 1, 1, 11, 2);


        final Mat hierarchy = new Mat();
        // LOG.info("Finding contours .... ");
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        // Highgui.imwrite("Hierarchy.png",hierarchy);
        LOG.info("Got contours {} ", contours.toArray());
        // final MatOfPoint2f approx = new MatOfPoint2f();
        LOG.info("Contour size : {}", contours.size());
        final MatOfPoint2f mMOP2f2 = new MatOfPoint2f();
        final Mat contoursFrame = image.clone();
        final Mat cFrame = image.clone();
        int count = 0;
        for (int index = 0; index < contours.size(); index++) {
            // Convert contours(i) from MatOfPoint to MatOfPoint2f
            final MatOfPoint2f mMOP2f1 = new MatOfPoint2f(contours.get(index).toArray());
            // Processing on mMOP2f1 which is in type MatOfPoint2f
            Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, Imgproc.arcLength(mMOP2f1, true) * 0.02, true);
            // Convert back to MatOfPoint and put the new values back into the contours list
            final MatOfPoint points = new MatOfPoint(mMOP2f2.toArray());
            // Get bounding rect of contour

            if (mMOP2f2.toList().size() == 4 && Imgproc.isContourConvex(points)
                    && Math.abs(Imgproc.contourArea(points)) > 1000 && Math.abs(Imgproc.contourArea(points)) < 2500) {
                LOG.info(" EDGES : {} AREA : {} ", mMOP2f2.toList().size(), Math.abs(Imgproc.contourArea(points)));
                count++;
            }

            final Rect rect1 = Imgproc.boundingRect(points);

            Core.rectangle(cFrame, rect1.tl(), rect1.br(), new Scalar(0, 255, 0), 2, 8, 0);

            if (mMOP2f2.toList().size() >= 4 && Math.abs(Imgproc.contourArea(points)) > 1000
                    && Math.abs(Imgproc.contourArea(points)) < 2500) {

                final Rect rect = Imgproc.boundingRect(points);

                Core.rectangle(contoursFrame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2, 8, 0);

                final List<List<Point>> newSquare =
                        ProcessForm.storeUniqueRectangles(mMOP2f2.toList(), squares, distThreshold);
                LOG.info(" Store size : {} ", newSquare.size());
                squares = newSquare;
            }

        }
        LOG.info("COUNT : {} ", count);
        Highgui.imwrite("Contours.png", contoursFrame);
        Highgui.imwrite("CFrame.png", cFrame);
        return squares;
    }

}

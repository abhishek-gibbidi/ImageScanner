package com.evive.ImageScanner;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * @author abhishek
 *
 */
public class ProcessForm {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessForm.class);
    static Properties properties = Utils.getProperties();

    public static int compareCoord(Double[] pt1, Double[] pt2, int threshold) {
        final double dist = Utils.EUCLIDEAN_DISTANCE(pt1, pt2);
        threshold = threshold * threshold;
        if (dist > threshold) {
            return 1;
        }
        return 0;

    }

    public static int compareDouble(final double pt1, final double pt2) {
        if (pt1 > pt2) {
            return 0;
        }
        return 1;
    }

    public static void formatCoordinatesABCD(final List<Point> rect, final Double[][] coord) {
        for (int i = 0; i < (Integer.valueOf(properties.getProperty("NO_COORDINATES"))); ++i) {
            final Point pt = rect.get(i);
            coord[i][0] = pt.x;
            coord[i][1] = pt.y;
        }

        sortCoordArray(coord, 1);

        if (compareDouble(coord[0][0], coord[1][0]) == 0) {
            List<Double> swapped = Utils.SWAP(coord[0][0], coord[1][0]);
            coord[0][0] = swapped.get(0);
            coord[1][0] = swapped.get(1);

            swapped = Utils.SWAP(coord[0][1], coord[1][1]);
            coord[0][1] = swapped.get(0);
            coord[1][1] = swapped.get(1);
        }

        if (compareDouble(coord[2][0], coord[3][0]) == 1) {
            List<Double> swapped = Utils.SWAP(coord[2][0], coord[3][0]);
            coord[2][0] = swapped.get(0);
            coord[3][0] = swapped.get(1);

            swapped = Utils.SWAP(coord[2][1], coord[3][1]);
            coord[2][1] = swapped.get(0);
            coord[3][1] = swapped.get(1);
        }
    }



    public static List<List<Point>> sortSquaresInLine(List<List<Point>> squares) {
        final List<List<Point>> sortSqrs = new ArrayList<>();

        final List<Integer> squaresInLine = new ArrayList<Integer>();
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_1")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_2")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_3")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_4")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_5")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_6")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_7")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_8")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_9")));
        squaresInLine.add(Integer.valueOf(properties.getProperty("SQUARES_IN_LINE_10")));

        int index = 0;
        for (int i = 0; i < Integer.valueOf(properties.getProperty("NO_OF_LINES")); i++) {

            final List<List<Point>> temp = new ArrayList<>();
            for (int j = index; j < squaresInLine.get(i) + index; j++) {
                LOG.info("LINE : {} , INDEX : {}, SIZE: {}", i, j, squares.size());
                temp.add(squares.get(j));
            }
            index = index + squaresInLine.get(i);
            final List<List<Point>> sorted_temp = sortPtsAccordX(temp);
            for (int k = 0; k < sorted_temp.size(); k++) {
                sortSqrs.add(sorted_temp.get(k));
            }
            temp.clear();
        }

        return sortSqrs;

    }

    public static List<List<Point>> removeNonNumeric(List<List<Point>> squares) {
        LOG.info("Squares size {}", squares.size());
        final Integer size = squares.size();
        for (int i = squares.size() - 1; i >= size - 35; i--) {
            LOG.info("Removing index : {} ", i);
            squares.remove(i);
        }
        return squares;
    }

    public static int getSmallerRect(Double[][] old_coord, Double[][] new_coord) {
        double old_coord_dist = 0.0;
        for (int i = 1; i < Integer.valueOf(properties.getProperty("NO_COORDINATES")); i++) {
            LOG.info("value of i : {}", i);
            old_coord_dist += Utils.EUCLIDEAN_DISTANCE(old_coord, i);
        }
        double new_coord_dist = 0.0;
        for (int i = 1; i < Integer.valueOf(properties.getProperty("NO_COORDINATES")); ++i) {

            new_coord_dist += Utils.EUCLIDEAN_DISTANCE(new_coord, i);
        }

        if (old_coord_dist > new_coord_dist) {
            return 1;
        }

        return 0;
    }

    // public static void main(final String[] args) {
    // //final ProcessForm processForm = new ProcessForm();
    // // processForm.getProperties();
    //
    // final Point p1 = new Point(1, 3);
    // final Point p2 = new Point(2, 5);
    // final Point p3 = new Point(3, 8);
    // final Point p4 = new Point(4, 4);
    //
    // final Point p11 = new Point(11, 13);
    // final Point p12 = new Point(12, 15);
    // final Point p13 = new Point(13, 18);
    // final Point p14 = new Point(14, 14);
    //
    // final Point p51 = new Point(51, 53);
    // final Point p52 = new Point(52, 55);
    // final Point p53 = new Point(53, 58);
    // final Point p54 = new Point(54, 54);
    //
    //
    // final List<Point> pts = new ArrayList<>();
    // pts.add(p1);
    // pts.add(p2);
    // pts.add(p3);
    // pts.add(p4);
    //
    // final List<Point> pts1 = new ArrayList<>();
    // pts1.add(p11);
    // pts1.add(p12);
    // pts1.add(p13);
    // pts1.add(p14);
    //
    // final List<Point> pts5 = new ArrayList<>();
    // pts5.add(p51);
    // pts5.add(p52);
    // pts5.add(p53);
    // pts5.add(p54);
    //
    //
    // final Point ans = ProcessForm.findMaxYCoord(pts);
    // final Point ans1 = ProcessForm.findMaxXCoord(pts);
    // final Point ans2 = ProcessForm.findMinXCoord(pts);
    // final Point ans3 = ProcessForm.findMinYCoord(pts);
    // LOG.info("{}, {}, {}, {}", ans, ans1, ans2, ans3);
    //
    // final List<List<Point>> sqs = new ArrayList<>();
    // sqs.add(pts5);
    // sqs.add(pts);
    // sqs.add(pts1);
    //
    // LOG.info("Before: {}", sqs.toString());
    // ProcessForm.sortPtsAccordY(sqs);
    // LOG.info("After: {}", sqs.toString());
    //
    //
    // LOG.info("Before: {}", sqs.toString());
    // ProcessForm.sortPtsAccordX(sqs);
    // LOG.info("After: {}", sqs.toString());
    //
    //
    // final int x = 3, y = 4;
    // Utils.SWAP(x, y);
    // LOG.info("p1: {}, p2: {}", x, y);
    //
    // }

    public static void sortCoordArray(final Double[][] array, final int index) {
        for (int i = 1; i < (Integer.valueOf(properties.getProperty("NO_COORDINATES"))); ++i) {
            for (int j = 0; j < (Integer.valueOf(properties.getProperty("NO_COORDINATES"))) - i; ++j) {
                if (array[j][index] > array[j + 1][index]) {
                    List<Double> swapped = Utils.SWAP(array[j][1], array[j + 1][1]);
                    array[j][1] = swapped.get(0);
                    array[j + 1][1] = swapped.get(1);

                    swapped = Utils.SWAP(array[j][0], array[j + 1][0]);
                    array[j][0] = swapped.get(0);
                    array[j + 1][0] = swapped.get(1);
                }
            }
        }
    }

    public static List<List<Point>> storeUniqueRectangles(List<Point> new_square, List<List<Point>> squares,
            int threshold) {

        // LOG.info("Entered uniqueRe{}", Integer.valueOf(properties.getProperty("NO_COORDINATES")));
        final Double[][] next_coord = new Double[Integer.valueOf(properties.getProperty("NO_COORDINATES"))][2];
        final Double[][] old_coord = new Double[Integer.valueOf(properties.getProperty("NO_COORDINATES"))][2];
        // LOG.info("new_square : {}",new_square);
        // LOG.info("Formating corrd");
        formatCoordinatesABCD(new_square, next_coord);
        // LOG.info("next_square : ");
        int count = 0;
        for (int i = 0; i < squares.size(); i++) {
            final List<Point> square = squares.get(i);
            formatCoordinatesABCD(square, old_coord);
            count = 0;
            for (int j = 0; j < Integer.valueOf(properties.getProperty("NO_COORDINATES")); j++) {
                if (compareCoord(next_coord[j], old_coord[j], threshold) == 0) {
                    count++;
                }
            }
            if (count > 2) {
                final int diff = getSmallerRect(old_coord, next_coord);
                if (diff == 1) {

                    squares.add(i, new_square);
                }
                // LOG.info("Squares : {}", squares.toArray());
                return squares;
            }
        }
        squares.add(new_square);
        // LOG.info("Squares : {}", squares.toArray());
        return squares;
    }

    // private final String propFile = "src/main/resources/data.properties";

    public static int compareDimen(Length_breadth_count dimen, double breadth, double length, int threshold) {
        if (Math.abs(dimen.getBreadth() - breadth) > threshold || Math.abs(dimen.getLegth() - length) > threshold) {
            return 0;
        }
        return 1;

    }

    public static List<Length_breadth_count> createUniqueLengthBreadthList(List<List<Point>> squares, int threshold) {
        final List<Length_breadth_count> unique_dimen = new ArrayList<>();
        List<Point> rect = new ArrayList<>();
        for (int i = 0; i < squares.size(); ++i) {

            rect = squares.get(i);
            storeUniqueRectLengthBreadth(rect, unique_dimen, threshold);
        }
        return unique_dimen;

    }

    public static void drawSquares(final Mat image, final List<List<MatOfPoint>> squares) {

        for (final List<MatOfPoint> square : squares) {
            Core.polylines(image, square, true, new Scalar(0, 255, 0), 1, 4, 16);
        }
    }

    /** Finds the Min X coordinate point in Rectangle **/
    public static Point findMaxXCoord(final List<Point> rect) {
        double minCord = -1;
        int pos = 0;
        for (int i = 0; i < 4; ++i) {
            final Point pt = rect.get(i);
            if (minCord < pt.x) {
                minCord = pt.x;
                pos = i;
            }
        }
        return rect.get(pos);
    }

    /** Finds the Min X coordinate point in Rectangle **/
    public static Point findMaxYCoord(final List<Point> rect) {
        double minCord = -1;
        int pos = 0;
        for (int i = 0; i < 4; ++i) {
            final Point pt = rect.get(i);
            if (minCord < pt.y) {
                minCord = pt.y;
                pos = i;
            }
        }
        return rect.get(pos);
    }

    /** Finds the Min X coordinate point in Rectangle **/
    public static Point findMinXCoord(final List<Point> rect) {
        double minCord = 100000;
        int pos = 0;
        for (int i = 0; i < 4; ++i) {
            final Point pt = rect.get(i);
            if (minCord > pt.x) {
                minCord = pt.x;
                pos = i;
            }
        }
        return rect.get(pos);
    }

    /** Finds the Min Y coordinate point in Rectangle **/
    public static Point findMinYCoord(final List<Point> rect) {
        int pos = 0;
        double minCord = 100000;
        for (int i = 0; i < 4; ++i) {
            final Point pt = rect.get(i);
            if (minCord > pt.y) {
                minCord = pt.y;
                pos = i;
            }
        }
        return rect.get(pos);
    }

    public static Mat getImage() {
        Mat image = new Mat();
        for (int fileIndex = 0; fileIndex < 70; fileIndex++) {
            // StringBuilder stringBuilder = new StringBuilder();

            image = Highgui.imread("/home/abhishek/Desktop/HWR/DigitRecognition/Forms/form-1.jpg");
            if (image.empty()) {
                LOG.info("Unable to read Image");
                System.out.println("NO IMAGE");
                System.exit(0);
            }
            Imgproc.resize(image, image, new Size(1000, 1000));

        }
        return image;
    }



    public static Length_breadth_count mostCommonLengthBreadth(List<List<Point>> squares, int threshold) {
        final List<Length_breadth_count> unique_dimen = createUniqueLengthBreadthList(squares, threshold);
        Length_breadth_count temp_length_breadth = new Length_breadth_count();
        final Length_breadth_count max_length_breadth = new Length_breadth_count();
        max_length_breadth.setCount(0);
        for (int i = 0; i < unique_dimen.size(); ++i) {

            temp_length_breadth = unique_dimen.get(i);
            if (temp_length_breadth.getCount() > max_length_breadth.getCount()) {

                max_length_breadth.setBreadth(temp_length_breadth.getBreadth());
                max_length_breadth.setLegth(temp_length_breadth.getLegth());
                max_length_breadth.setCount(temp_length_breadth.getCount());
            } else {
                temp_length_breadth = null;
            }
        }
        unique_dimen.clear();
        final List<Length_breadth_count> temp = new ArrayList<>();
        unique_dimen.addAll(temp);
        return max_length_breadth;

    }

    public static List<List<Point>> removeRectUsingSize(List<List<Point>> squares, int threshold) {
        final Length_breadth_count max_length_breadth = mostCommonLengthBreadth(squares, threshold);
        double breadth = 0.0;
        double length = 0.0;
        final Double[][] organized_rect = new Double[4][2];
        for (int i = 0; i < squares.size(); i++) {
            final List<Point> sq_it = squares.get(i);
            formatCoordinatesABCD(sq_it, organized_rect);
            breadth = Math.abs(organized_rect[0][0] - organized_rect[1][0]);
            length = Math.abs(organized_rect[0][1] - organized_rect[2][1]);
            if (compareDimen(max_length_breadth, breadth, length, threshold) == 0) {

                squares.remove(sq_it);
            }
        }
        return squares;
    }

    public static List<List<Point>> replaceListFromArray(final List<List<Point>> squares,
            final List<PointList> pointRectArray, final int size) {

        for (int i = 0; i < size; ++i) {
            squares.add(pointRectArray.get(i).getRect());
        }
        return squares;
    }


    public static List<List<Point>> sortList(final List<List<Point>> squares) {
        final int size = squares.size();
        for (int i = 0; i < size; ++i) {
            for (int j = i; j < size - 1; ++j) {
                final List<Point> sqr = squares.get(j);
                final List<Point> sqr1 = squares.get(j + 1);
                if (findMinYCoord(sqr).y > findMinYCoord(sqr1).y) {
                    if (findMinXCoord(sqr).x > findMinXCoord(sqr1).x) {
                        final List<List<Point>> swapped = Utils.SWAP(squares.get(j), squares.get(j + 1));
                        squares.set(j, swapped.get(0));
                        squares.set(j + 1, swapped.get(1));
                    }
                }
            }
        }
        return squares;
    }

    public static List<List<Point>> sortPtsAccordX(final List<List<Point>> squares) {
        final List<PointList> pointRectArray = storePtsForSorting(squares);
        final int size = squares.size();
        for (int i = 1; i < size; ++i) {
            for (int j = 0; j < size - i; ++j) {
                if (pointRectArray.get(j).getPoint().x > pointRectArray.get(j + 1).getPoint().x) {
                    final List<PointList> swapped = Utils.SWAP(pointRectArray.get(j), pointRectArray.get(j + 1));
                    pointRectArray.set(j, swapped.get(0));
                    pointRectArray.set(j + 1, swapped.get(1));
                }
            }
        }
        squares.clear();
        // final List<List<Point> > temp;
        // temp.addAll(squares);

        return replaceListFromArray(squares, pointRectArray, size);
    }

    public static List<List<Point>> sortPtsAccordY(final List<List<Point>> squares) {
        final List<PointList> pointRectArray = storePtsForSorting(squares);
        final int size = squares.size();
        for (int i = 1; i < size; ++i) {
            for (int j = i; j < size - i; ++j) {
                if (pointRectArray.get(j).getPoint().y < pointRectArray.get(j + 1).getPoint().y) {
                    final List<PointList> swapped = Utils.SWAP(pointRectArray.get(j), pointRectArray.get(j + 1));
                    pointRectArray.set(j, swapped.get(0));
                    pointRectArray.set(j + 1, swapped.get(1));
                }
            }
        }
        squares.clear();
        // final List<List<Point>> temp = null;
        // temp.addAll(squares);
        pointRectArray.forEach(pt -> {
            LOG.info("Point: {}", pt.getPoint().toString());
        });
        // LOG.info("ptRectArray: {}", pointRectArray.toString());

        return replaceListFromArray(squares, pointRectArray, size);
    }

    public static List<PointList> storePtsForSorting(final List<List<Point>> squares) {
        final Double[][] organized_rec = new Double[Integer.valueOf(properties.getProperty("NO_COORDINATES"))][2];
        final List<PointList> pt_rect_array = new ArrayList<PointList>();
        for (final List<Point> square : squares) {
            final PointList pt_rect = new PointList();
            formatCoordinatesABCD(square, organized_rec);
            pt_rect.setPoint(new Point(organized_rec[0][0], organized_rec[0][1]));
            pt_rect.setRect(square);
            pt_rect_array.add(pt_rect);
        }
        return pt_rect_array;
    }

    public static void storeUniqueRectLengthBreadth(List<Point> rect, List<Length_breadth_count> unique_dimen,
            int threshold) {
        final Double[][] organized_rect = new Double[4][2];
        formatCoordinatesABCD(rect, organized_rect);
        final double breadth = Math.abs(organized_rect[0][0] - organized_rect[1][0]);
        final double length = Math.abs(organized_rect[0][1] - organized_rect[2][1]);
        Length_breadth_count dimen = new Length_breadth_count();
        boolean done = false;
        for (int i = 0; i < unique_dimen.size() && !done; ++i) {

            dimen = unique_dimen.get(i);
            if (compareDimen(dimen, breadth, length, threshold) == 1) {

                dimen.setCount(dimen.getCount() + 1);
                done = true;
            }
        }

    }
}

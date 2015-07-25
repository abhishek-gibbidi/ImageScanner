package com.evive.ImageScanner;

import org.opencv.core.Size;

import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.highgui.Highgui;
import org.opencv.core.Mat;
import org.opencv.core.Core;


public class opecvExamp {
    public static void main(String[] args) {
        System.out.println("test");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new test().readImage();
    }
}


class test {

    public void readImage() {
        final Mat image = Highgui.imread(getClass().getResource("/lena.png").getPath());
        image.cols();
        final Mat pyr = new Mat();
        final Mat timg = new Mat();
        new Mat();
        new Mat(image.size(), CvType.CV_8U);

        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        Highgui.imwrite("gray.png", image);

        final Size size = new Size(image.cols() / 2, image.rows() / 2);
        Imgproc.pyrDown(image, pyr, size);
        Imgproc.pyrUp(pyr, timg, image.size());

    }

}

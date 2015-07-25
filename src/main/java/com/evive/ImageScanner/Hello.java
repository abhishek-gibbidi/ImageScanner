package com.evive.ImageScanner;

import org.opencv.core.CvType;

import org.opencv.core.Mat;
import org.opencv.core.Core;


public class Hello {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        final Mat m = Mat.eye(4, 4, CvType.CV_8UC1);
        System.out.println("m = " + m.dump());
    }
}

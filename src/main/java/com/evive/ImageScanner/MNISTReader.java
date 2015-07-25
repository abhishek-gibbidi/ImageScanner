package com.evive.ImageScanner;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class implements a reader for the MNIST dataset of handwritten digits. The dataset is found at
 * http://yann.lecun.com/exdb/mnist/.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class MNISTReader {

    /**
     * @param args args[0]: label file; args[1]: data file.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final DataInputStream labels = new DataInputStream(new FileInputStream(args[0]));
        final DataInputStream images = new DataInputStream(new FileInputStream(args[1]));
        int magicNumber = labels.readInt();
        if (magicNumber != 2049) {
            System.err.println("Label file has wrong magic number: " + magicNumber + " (should be 2049)");
            System.exit(0);
        }
        magicNumber = images.readInt();
        if (magicNumber != 2051) {
            System.err.println("Image file has wrong magic number: " + magicNumber + " (should be 2051)");
            System.exit(0);
        }
        final int numLabels = labels.readInt();
        final int numImages = images.readInt();
        final int numRows = images.readInt();
        final int numCols = images.readInt();
        if (numLabels != numImages) {
            System.err.println("Image file and label file do not contain the same number of entries.");
            System.err.println("  Label file contains: " + numLabels);
            System.err.println("  Image file contains: " + numImages);
            System.exit(0);
        }

        final long start = System.currentTimeMillis();
        int numLabelsRead = 0;
        while (labels.available() > 0 && numLabelsRead < numLabels) {
            labels.readByte();
            numLabelsRead++;
            final int[][] image = new int[numCols][numRows];
            for (int colIdx = 0; colIdx < numCols; colIdx++) {
                for (int rowIdx = 0; rowIdx < numRows; rowIdx++) {
                    image[colIdx][rowIdx] = images.readUnsignedByte();
                }
            }


            // At this point, 'label' and 'image' agree and you can do whatever you like with them.

            if (numLabelsRead % 10 == 0) {
                System.out.print(".");
            }
            if ((numLabelsRead % 800) == 0) {
                System.out.print(" " + numLabelsRead + " / " + numLabels);
                final long end = System.currentTimeMillis();
                final long elapsed = end - start;
                final long minutes = elapsed / (1000 * 60);
                final long seconds = (elapsed / 1000) - (minutes * 60);
                System.out.println("  " + minutes + " m " + seconds + " s ");
            }
        }
        labels.close();
        images.close();
        System.out.println();
        final long end = System.currentTimeMillis();
        final long elapsed = end - start;
        final long minutes = elapsed / (1000 * 60);
        final long seconds = (elapsed / 1000) - (minutes * 60);
        System.out.println("Read " + numLabelsRead + " samples in " + minutes + " m " + seconds + " s ");
    }

}

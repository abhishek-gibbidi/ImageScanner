package com.evive.ImageScanner;

import java.awt.Point;

public class SortRectangles {

    public double angle(final Point pt1, final Point pt2, final Point pt0) {
        final double dx1 = pt1.x - pt0.x;
        final double dy1 = pt1.y - pt0.y;
        final double dx2 = pt2.x - pt0.x;
        final double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }


}

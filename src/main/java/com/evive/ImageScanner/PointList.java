package com.evive.ImageScanner;

import org.opencv.core.Point;

import java.util.List;

public class PointList {
    private Point point;
    List<Point> rect;

    public Point getPoint() {
        return point;
    }

    public List<Point> getRect() {
        return rect;
    }

    public void setPoint(final Point point) {
        this.point = point;
    }

    public void setRect(final List<Point> rect) {
        this.rect = rect;
    }

}

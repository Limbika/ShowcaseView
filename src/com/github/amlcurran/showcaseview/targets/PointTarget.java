package com.github.amlcurran.showcaseview.targets;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Showcase a specific x/y co-ordinate on the screen.
 */
public class PointTarget implements Target {

    private final Point mPoint;

    public PointTarget(Point point) {
        mPoint = point;
    }

    public PointTarget(int xValue, int yValue) {
        mPoint = new Point(xValue, yValue);
    }

    @Override
    public Point getPoint() {
        return mPoint;
    }

	@Override
	public Rect getArea() {
		return new Rect(mPoint.x, mPoint.y, mPoint.x, mPoint.y);
	}

}

package com.github.amlcurran.showcaseview.targets;

import android.graphics.Point;
import android.graphics.Rect;

public interface Target {
    Target NONE = new Target() {
        @Override
        public Point getPoint() {
            return new Point(1000000, 1000000);
        }

		@Override
		public Rect getArea() {
			return new Rect(0, 0, 0, 0);
		}
    };

    public Point getPoint();
    public Rect getArea();
}

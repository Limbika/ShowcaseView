package com.github.amlcurran.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;

/**
 * Target a view on the screen. This will centre the target on the view.
 */
public class ViewTarget implements Target {

    private View mView = null;
    private int mResId;
    private Activity mActivity;

    public ViewTarget(View view) {
        mView = view;
    }

    public ViewTarget(int viewId, Activity activity) {
    	mResId = viewId;
    	mActivity = activity;
    }

    @Override
    public Point getPoint() {
    	if ( mView == null ) {
    		mView = mActivity.findViewById(mResId);
    		mActivity = null;
    	}
    	
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        int x = location[0] + mView.getWidth() / 2;
        int y = location[1] + mView.getHeight() / 2;
        return new Point(x, y);
    }
}

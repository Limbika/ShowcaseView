package com.github.amlcurran.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
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
    	View view = getView();
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int x = location[0] + view.getWidth() / 2;
        int y = location[1] + view.getHeight() / 2;
        return new Point(x, y);
    }

	@Override
	public Rect getArea() {
		View view = getView();
		int[] location = new int[2];
        view.getLocationInWindow(location);
		
		Rect rect = new Rect();
		rect.left = location[0];
		rect.top = location[1];
		rect.right = location[0] + view.getWidth();
		rect.bottom = location[1] + view.getHeight();
		
		return rect;
	}
	
	private View getView() {
    	if ( mView == null ) {
    		mView = mActivity.findViewById(mResId);
    		mActivity = null;
    	}
    	return mView;
	}

}

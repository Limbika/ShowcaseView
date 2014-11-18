package com.github.amlcurran.showcaseview;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.ViewTarget;

class ShowcaseInfo {
	
	private View mTargetView = null;
	private int mTarget;
	private CharSequence mTitle;
	private CharSequence mDescription;
	private int mImage;
	private int mIcon;
	private int mTouchMode;
	private Runnable mRunnable = null;
	private boolean mFinalizeButton;

	public ShowcaseInfo(int target, CharSequence title, int icon,  CharSequence description, int image, int touchMode, Runnable runnable, boolean finalize) {
		mTarget = target;
		mTitle = title;
		mIcon = icon;
		mDescription = description;
		mImage = image;
		mTouchMode = touchMode;
		mRunnable = runnable;
		mFinalizeButton = finalize;
		Log.i("ShowcaseInfo", "Finalize: " +finalize);
	}
	
	public ShowcaseInfo(View target, CharSequence title, int icon, CharSequence description, int image, int touchMode, Runnable runnable, boolean finalize) { 
		this(0, title, icon, description, image, touchMode, runnable, finalize);
		mTargetView = target;
	}
	
	public ShowcaseView.Builder build(Activity activity) {
		ShowcaseView.Builder builder = new ShowcaseView.Builder(activity, mTouchMode, mFinalizeButton);
		if ( mTargetView == null && mTarget != -1 ) {
			ViewTarget viewTarget = new ViewTarget(mTarget, activity);
			builder.setTarget(viewTarget);
		}
		else if ( mTargetView != null ) {
			ViewTarget viewTarget = new ViewTarget(mTargetView);
			builder.setTarget(viewTarget);
		}
		if ( mTitle.length() != 0 ) {
			builder.setContentTitle(mTitle);
		}
		if ( mDescription.length() != 0 ) {
			builder.setContentText(mDescription);
		}
		if ( mImage != -1 ) {
			builder.setImage(mImage);
		}
		if ( mIcon != -1 ) {
			builder.setIcon(mIcon);
		}
		return builder;
	}
	
	/**
	 * Run the runnable if has it.
	 */
	public void run() {
		if ( mRunnable != null ) mRunnable.run();
		mRunnable = null;
	}
	
}

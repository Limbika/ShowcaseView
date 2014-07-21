package com.github.amlcurran.showcaseview;

import android.app.Activity;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class ShowcaseInfo {
	
	private View mTargetView = null;
	private int mTarget;
	private int mTitle;
	private int mDescription;
	private int mImage;

	public ShowcaseInfo(int target, int title, int description, int image) {
		mTarget = target;
		mTitle = title;
		mDescription = description;
		mImage = image;
	}
	
	public ShowcaseInfo(View target, int title, int description, int image) {
		this(0, title, description, image);
		mTargetView = target;
	}
	
	public ShowcaseView.Builder build(Activity activity) {
		ShowcaseView.Builder builder = new ShowcaseView.Builder(activity);
		if ( mTargetView == null && mTarget != 0 ) {
			ViewTarget viewTarget = new ViewTarget(mTarget, activity);
			builder.setTarget(viewTarget);
		}
		else if ( mTargetView != null ) {
			ViewTarget viewTarget = new ViewTarget(mTargetView);
			builder.setTarget(viewTarget);
		}
		if ( mTitle != 0 ) {
			builder.setContentTitle(mTitle);
		}
		if ( mDescription != 0 ) {
			builder.setContentText(mDescription);
		}
		if ( mImage != 0 ) {
			builder.setImage(mImage);
		}
		return builder;
	}
	
}

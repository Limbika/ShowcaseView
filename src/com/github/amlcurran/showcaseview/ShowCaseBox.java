package com.github.amlcurran.showcaseview;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Box of ShowCaseViews.
 */
public class ShowCaseBox {
	
	private Activity mActivity;
	private ArrayList<ShowcaseInfo> mShowcaseInfos = new ArrayList<ShowcaseInfo>();
	private ShowcaseView mshowcaseView;
	private int mShowcaseCurrent;
	
	public ShowCaseBox(Activity activity) {
		mActivity = activity;
	}
	
	/**
	 * Add new showcase.
	 * @param target The target view to focus. If is null, focus nothing.
	 * @param title The title resource. If is 0, without title. 
	 * @param description The description resource. If is 0, without description. 
	 * @param image The drawable resource. If is 0, without image.
	 */
	public void addShowCase(View target, int title, int description, int image) {
		ShowcaseInfo info = new ShowcaseInfo(target, title, description, image);
		mShowcaseInfos.add(info);
	}
	
	/**
	 * Add new showcase.
	 * @param target The target view id to focus. If is 0, focus nothing.
	 * @param title The title resource. If is 0, without title. 
	 * @param description The description resource. If is 0, without description. 
	 * @param image The drawable resource. If is 0, without image.
	 */
	public void addShowCase(int target, int title, int description, int image) {
		ShowcaseInfo info = new ShowcaseInfo(target, title, description, image);
		mShowcaseInfos.add(info);
	}
	
	/**
	 * Show the showcases in single shot mode.
	 */
	public void show() {
		show(true);
	}

	/**
	 * Show the showcases. 
	 * @param sigleShot True if is single shot showcase, false otherwise.
	 */
	public void show(boolean sigleShot) {
		if ( mShowcaseInfos.size() > 0 ) {
			mShowcaseCurrent = 0;
			ShowcaseInfo info = mShowcaseInfos.get(mShowcaseCurrent);
			showShowcase(info, sigleShot);
		}
	}
	
	private void showShowcase(ShowcaseInfo info, final boolean singleShot) {
		ShowcaseView.Builder builder = info.build(mActivity);
		if ( singleShot ) {
			builder.singleShot( mActivity.getClass().getName().hashCode() + mShowcaseCurrent );
		}
		builder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mshowcaseView.dismiss();
				mShowcaseCurrent++;
				if ( mShowcaseCurrent < mShowcaseInfos.size() ) {
					ShowcaseInfo info = mShowcaseInfos.get(mShowcaseCurrent);
					showShowcase(info, singleShot);
				}
			}
		});
		mshowcaseView = builder.build();
		mshowcaseView.show();
	}

}

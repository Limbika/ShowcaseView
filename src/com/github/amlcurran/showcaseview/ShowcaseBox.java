package com.github.amlcurran.showcaseview;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Box of ShowCaseViews.
 */
public class ShowcaseBox {
	
	private Activity mActivity;
	private ArrayList<ShowcaseInfo> mShowcaseInfos = new ArrayList<ShowcaseInfo>();
	private ShowcaseView mShowCaseView;
	private int mShowcaseCurrent;
	
	public ShowcaseBox(Activity activity) {
		mActivity = activity;
	}
	
	/**
	 * Add new showcase.
	 * @param target The target view to focus. If is null, focus nothing.
	 * @param title The title resource. If is 0, without title. 
	 * @param description The description resource. If is 0, without description. 
	 * @param image The drawable resource. If is 0, without image.
	 * @param touchMode. The touch mode behaviour. Use:
	 *  - ShowcaseView.TOUCH_ALL You can click in all screen.
	 *  - ShowcaseView.TOUCH_TARGET You can click only in target view.
	 *  - ShowcaseView.TOUCH_NONE You cannot click on the screen.
	 */
	public void addShowCase(View target, int title, int description, int image, int touchMode) {
		ShowcaseInfo info = new ShowcaseInfo(target, title, description, image, touchMode);
		mShowcaseInfos.add(info);
	}
	
	/**
	 * Add new showcase.
	 * @param target The target view id to focus. If is 0, focus nothing.
	 * @param title The title resource. If is 0, without title. 
	 * @param description The description resource. If is 0, without description. 
	 * @param image The drawable resource. If is 0, without image.
	 *  * @param touchMode. The touch mode behaviour. Use:
	 *  - ShowcaseView.TOUCH_ALL You can click in all screen.
	 *  - ShowcaseView.TOUCH_TARGET You can click only in target view.
	 *  - ShowcaseView.TOUCH_NONE You cannot click on the screen.
	 */
	public void addShowCase(int target, int title, int description, int image, int touchMode) {
		ShowcaseInfo info = new ShowcaseInfo(target, title, description, image, touchMode);
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
	/**
	 * Know if the ShowCaseView are showing information on the screen or not.
	 * @return True if yes, False is not.
	 */
	public boolean viewIsShown(){
		return mShowCaseView.isShown();
	}
	
	private void showShowcase(ShowcaseInfo info, final boolean singleShot) {
		ShowcaseView.Builder builder = info.build(mActivity);
		if ( singleShot ) {
			builder.singleShot( mActivity.getClass().getName().hashCode() + mShowcaseCurrent );
		}
		builder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mShowCaseView.dismiss();
				mShowcaseCurrent++;
				if ( mShowcaseCurrent < mShowcaseInfos.size() ) {
					ShowcaseInfo info = mShowcaseInfos.get(mShowcaseCurrent);
					showShowcase(info, singleShot);
				}
			}
		});
		mShowCaseView = builder.build();
		mShowCaseView.show();
	}
	

}

package com.github.amlcurran.showcaseview;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Box of ShowCaseViews.
 */
public class ShowcaseBox {
	
    public static final int TOUCH_ALL = 1;
    public static final int TOUCH_TARGET = 2;
    public static final int TOUCH_NONE = 3;
	
	private Activity mActivity;
	private ArrayList<ShowcaseInfo> mShowcaseInfos = new ArrayList<ShowcaseInfo>();
	private ShowcaseView mShowCaseView;
	private int mShowcaseCurrent;
	private ShotStateStore mShotStateStore;
	
	public ShowcaseBox(Activity activity) {
		mActivity = activity;
		mShotStateStore = new ShotStateStore(activity);
	}
	
	/**
	 * Clear the showcase box.
	 */
	public void clear() {
		mShowcaseInfos.clear();
	}
	
	/**
	 * Know if the ShowCaseView are showing information on the screen or not.
	 * @return True if yes, false if not.
	 */
	public boolean isShown(){
		return mShowCaseView != null ? mShowCaseView.isShown() : false;
	}
	
	/**
	 * Know if it has been added showcases.
	 * @return True if yes, false if not.
	 */
	public boolean isEmpty(){
		return mShowcaseInfos.isEmpty();
	}
	
	/**
	 * Don't show more showcases.
	 */
	public void finished() {
		mShotStateStore.finished();
		if (mShowCaseView != null) {
			mShowCaseView.dismiss();
			mShowcaseInfos.clear();
			mShowCaseView = null;
		}
	}
	
	/**
	 * Allow again to show showcases. 
	 */
	public void restore() {
		mShotStateStore.restore();
	}
	
	/**
	 * Clean showcase sharedPrefs.
	 */
	public void clean() {
		mShotStateStore.clean();
		
	}
	
	/**
	 * Add new showcase.
	 * @param target The target view to focus. If is null, focus nothing.
	 * @param title The title resource. If is "", without title. 
	 * @param icon The drawable resource. It will be beside the title. If is -1, without icon.
	 * @param description The description resource. If is "", without description. 
	 * @param image The drawable resource. It will be below the description. If is -1, without image.
	 * @param touchMode. The touch mode behaviour. Use:
	 * <ul>
	 * 	<li>{@link #TOUCH_ALL} You can click in all screen.</li>
	 *  <li>{@link #TOUCH_TARGET}You can click only in target view.</li>
	 *  <li>{@link #TOUCH_NONE} You cannot click on the screen.</li>
	 * </ul>
	 * @param runnable The callback called when the showcase start.
	 * @param finishbutton True to show the button. False otherwise.
	 */
	public void addShowCase(View target, CharSequence title, int icon, CharSequence description, int image, int touchMode, Runnable runnable, boolean finalize) {
		ShowcaseInfo info = new ShowcaseInfo(target, title, icon, description, image, touchMode, runnable, finalize);
		mShowcaseInfos.add(info);
	}
	
	/**
	 * Add new showcase.
	 * @param target The target view id to focus. If is -1, focus nothing.
	 * @param title The title resource. If is "", without title. 
	 * @param icon The drawable resource. It will be beside the title. If is -1, without icon.
	 * @param description The description resource. If is "", without description. 
	 * @param image The drawable resource. It will be below the description. If is -1, without image.
	 * @param touchMode. The touch mode behaviour. Use:
	 * <ul>
	 *  <li>{@link #TOUCH_ALL} You can click in all screen.</li>
	 *  <li>{@link #TOUCH_TARGET}You can click only in target view.</li>
	 *  <li>{@link #TOUCH_NONE} You cannot click on the screen.</li>
	 * </ul>
	 * @param runnable The callback called when the showcase start.
	 * @param finishbutton True to show the button. False otherwise.
	 */
	public void addShowCase(int target, CharSequence title, int icon, CharSequence description, int image, int touchMode, Runnable runnable, boolean finalize) {
		ShowcaseInfo info = new ShowcaseInfo(target, title, icon, description, image, touchMode, runnable, finalize);
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
			showShowcase(sigleShot);
		}
	}
	
	private void showShowcase(final boolean singleShot) {
		// The list is finished
		if ( mShowcaseCurrent >= mShowcaseInfos.size() ) return;
		
		final ShowcaseInfo info = mShowcaseInfos.get(mShowcaseCurrent);
		if ( singleShot ) {
			long value = mActivity.getClass().getName().hashCode() + mShowcaseCurrent;
			mShotStateStore.setSingleShot(value);
			if ( mShotStateStore.hasShot() ) {
				showNext(singleShot);
				return;
			}
		}
		
		ShowcaseView.Builder builder = info.build(mActivity);
		builder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Mark singleshot
				if ( singleShot ) mShotStateStore.storeShot();
				
				mShowCaseView.dismiss();
				mShowCaseView = null;
				
				// Execute runnable
				info.run();
				
				if ( v.getId() == R.id.btn_end )  {
					showNext(singleShot);
				}
				else if ( v.getId() == R.id.btn_finalize ) {
					mShotStateStore.finished();
					mShowcaseInfos.clear();
				}
				
			}
		});
		
		if ( !mShotStateStore.isFinished() ) {
			mShowCaseView = builder.build();
			mShowCaseView.show();
		}
	}
	
	private void showNext(boolean singleShot) {
		mShowcaseInfos.set(mShowcaseCurrent, null);
		mShowcaseCurrent++;
		showShowcase(singleShot);
	}
	
}

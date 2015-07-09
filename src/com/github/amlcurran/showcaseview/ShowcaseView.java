package com.github.amlcurran.showcaseview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.AnimationFactory.AnimationStartListener;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
class ShowcaseView extends RelativeLayout implements View.OnTouchListener, ViewTreeObserver.OnPreDrawListener,
		ViewTreeObserver.OnGlobalLayoutListener {

	private static final String TAG = "ShowCaseView";

	private final ImageButton mTickFollowButton;
	private final ImageButton mFinalizeXButton;
	private final TextDrawer textDrawer;
	private final ShowcaseDrawer showcaseDrawer;
	private final ShowcaseAreaCalculator showcaseAreaCalculator;
	private final AnimationFactory animationFactory;

	// Showcase metrics
	private int showcaseX = -1;
	private int showcaseY = -1;
	private float scaleMultiplier = 1f;
	private Rect targetArea;

	// Touch mode
	private int mTouchMode;

	private boolean hasAlteredText = false;
	private boolean hasNoTarget = false;
	private boolean shouldCentreText;
	private Bitmap bitmapBuffer;

	protected ShowcaseView(Context context, int touchMode, boolean finalize) {
		this(context, null, R.styleable.CustomTheme_showcaseViewStyle, touchMode, finalize);
	}

	protected ShowcaseView(Context context, AttributeSet attrs, int defStyle, int touchMode, boolean finalize) {
		super(context, attrs, defStyle);

		ApiUtils apiUtils = new ApiUtils();
		animationFactory = new AnimatorAnimationFactory();
		showcaseAreaCalculator = new ShowcaseAreaCalculator();
		textDrawer = new TextDrawer(getResources(), showcaseAreaCalculator, getContext());

		apiUtils.setFitsSystemWindowsCompat(this);
		getViewTreeObserver().addOnPreDrawListener(this);
		getViewTreeObserver().addOnGlobalLayoutListener(this);
		setOnTouchListener(this);

		// Get the attributes for the ShowcaseView
		final TypedArray styled = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ShowcaseView, R.attr.showcaseViewStyle,
				R.style.ShowcaseView);

		// Touch behaviour
		mTouchMode = touchMode;

		// Buttons
		LayoutInflater.from(context).inflate(R.layout.button_tick_follow_layout, this);
		mTickFollowButton = (ImageButton) findViewById(R.id.btn_tick_follow);
		mTickFollowButton.setLayoutParams(getLayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));

		LayoutInflater.from(context).inflate(R.layout.button_finalize_x_layout, this);
		mFinalizeXButton = (ImageButton) findViewById(R.id.btn_finalize_x);
		mFinalizeXButton.setLayoutParams(getLayoutParams(RelativeLayout.ALIGN_PARENT_LEFT));
		mFinalizeXButton.setVisibility(finalize ? VISIBLE : GONE);

		if (touchMode == ShowcaseBox.TOUCH_NONE) {
			showcaseDrawer = new NoTargetShowcaseDrawer(getResources());
			mTickFollowButton.setVisibility(VISIBLE);
		}
		else {
			showcaseDrawer = new TargetShowcaseDrawer(getResources());
			mTickFollowButton.setVisibility(GONE);
		}

		// Style
		updateStyle(styled, false);
	}

	void setShowcasePosition(Point point) {
		setShowcasePosition(point.x, point.y);
	}

	void setShowcasePosition(int x, int y) {
		showcaseX = x;
		showcaseY = y;
		invalidate();
	}

	public void setTarget(final Target target) {
		setShowcase(target, true);
	}

	public void setShowcase(final Target target, final boolean animate) {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				updateBitmap();
				
				if (target != null && target.getPoint() != null && target.getArea() != null) {
					Point targetPoint = target.getPoint();
					hasNoTarget = false;
					targetArea = target.getArea();
					if (animate && (Build.VERSION.SDK_INT > 10)) {
						animationFactory.animateTargetToPoint(ShowcaseView.this, targetPoint);
					}
					else {
						setShowcasePosition(targetPoint);
					}
				}
				else {
					mTickFollowButton.setVisibility(VISIBLE);
					showcaseX = 0;
					showcaseY = 0;
					hasNoTarget = true;
					invalidate();
				}
			}
		}, 500);
	}

	private void updateBitmap() {
		try {
			if (bitmapBuffer == null || haveBoundsChanged()) {
				clearBitmap();
				bitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			}
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
			dismiss();
		}
	}

	private boolean haveBoundsChanged() {
		return getMeasuredWidth() != bitmapBuffer.getWidth() || getMeasuredHeight() != bitmapBuffer.getHeight();
	}

	public boolean hasShowcaseView() {
		return (showcaseX != 1000000 && showcaseY != 1000000) || !hasNoTarget;
	}

	public void setShowcaseX(int x) {
		setShowcasePosition(x, showcaseY);
	}

	public void setShowcaseY(int y) {
		setShowcasePosition(showcaseX, y);
	}

	public int getShowcaseX() {
		return showcaseX;
	}

	public int getShowcaseY() {
		return showcaseY;
	}

	/**
	 * Override the standard button click event
	 * 
	 * @param listener Listener to listen to on click events
	 */
	public void setButtonsOnClickListener(OnClickListener listener) {
		if (mTickFollowButton != null) {
			mTickFollowButton.setOnClickListener(listener);
		}
		if (mFinalizeXButton != null) {
			mFinalizeXButton.setOnClickListener(listener);
		}
	}

	@Override
	public boolean onPreDraw() {
		boolean recalculatedCling = showcaseAreaCalculator.calculateShowcaseRect(showcaseX, showcaseY, showcaseDrawer);
		boolean recalculateText = recalculatedCling || hasAlteredText;
		if (recalculateText) {
			textDrawer.calculateTextPosition(getMeasuredWidth(), getMeasuredHeight(), this, shouldCentreText);
		}
		hasAlteredText = false;
		return true;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (showcaseX < 0 || showcaseY < 0) {
			super.dispatchDraw(canvas);
			return;
		}

		// Draw background color
		showcaseDrawer.erase(bitmapBuffer);

		// Draw the showcase drawable
		if (!hasNoTarget) {
			showcaseDrawer.drawShowcase(bitmapBuffer, showcaseX, showcaseY, scaleMultiplier);
		}
		showcaseDrawer.drawToCanvas(canvas, bitmapBuffer);

		// Draw the text on the screen, recalculating its position if necessary
		textDrawer.draw(canvas);

		super.dispatchDraw(canvas);
	}

	@SuppressWarnings("deprecation")
	public void recycle() {
		clearBitmap();
		textDrawer.recycle();

		getViewTreeObserver().removeOnPreDrawListener(this);
		if (Build.VERSION.SDK_INT >= 16) {
			getViewTreeObserver().removeOnGlobalLayoutListener(this);
		}
		else {
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}

		setOnTouchListener(null);
	}

	private void clearBitmap() {
		if (bitmapBuffer != null && !bitmapBuffer.isRecycled()) {
			bitmapBuffer.recycle();
			bitmapBuffer = null;
		}
	}

	public void show() {
		fadeInShowcase();
	}

	public void dismiss() {
		recycle();

		// Remove view
		final Activity activity = (Activity) getContext();
		((ViewGroup) activity.getWindow().getDecorView()).removeView(ShowcaseView.this);
	}

	private void fadeInShowcase() {
		animationFactory.fadeInView(this, getResources().getInteger(android.R.integer.config_mediumAnimTime), new AnimationStartListener() {

			@Override
			public void onAnimationStart() {
				setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		// No touch allowed
		if (mTouchMode == ShowcaseBox.TOUCH_NONE) {
			return true;
		}

		// Touch in target allowed and touched in it.
		else
			if (mTouchMode == ShowcaseBox.TOUCH_TARGET) {
				if (targetArea != null && targetArea.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
					return !mTickFollowButton.performClick();
				}
				else {
					return true;
				}
			}

		// Touch free
		return false;
	}

	private static void insertShowcaseView(ShowcaseView showcaseView, Activity activity) {
		((ViewGroup) activity.getWindow().getDecorView()).addView(showcaseView);
		showcaseView.show();
	}

	public void setContentTitle(CharSequence title) {
		textDrawer.setContentTitle(title);
	}

	public void setContentText(CharSequence text) {
		textDrawer.setContentText(text);
	}

	public void setImage(int res) {
		textDrawer.setImage(res);
	}

	public void setIcon(int res) {
		textDrawer.setIcon(res);

	}

	public void setScaleMultiplier(float scaleMultiplier) {
		this.scaleMultiplier = scaleMultiplier;
	}

	@Override
	public void onGlobalLayout() {
		updateBitmap();
	}

	public void hideButton() {
		mTickFollowButton.setVisibility(GONE);
		mFinalizeXButton.setVisibility(GONE);
	}

	public void showButton() {
		mTickFollowButton.setVisibility(VISIBLE);
		mFinalizeXButton.setVisibility(VISIBLE);
	}

	/**
	 * Builder class which allows easier creation of {@link ShowcaseView}s. It is recommended that you use this Builder
	 * class.
	 */
	public static class Builder {

		final ShowcaseView showcaseView;
		private final Activity activity;

		public Builder(Activity activity) {
			this(activity, ShowcaseBox.TOUCH_TARGET, false);
		}

		public Builder(Activity activity, int touchMode, boolean finalizeButton) {
			this.activity = activity;
			this.showcaseView = new ShowcaseView(activity, touchMode, finalizeButton);
		}

		/**
		 * Create the {@link com.github.amlcurran.showcaseview.ShowcaseView} and show it.
		 * 
		 * @return the created ShowcaseView
		 */
		public ShowcaseView build() {
			insertShowcaseView(showcaseView, activity);
			return showcaseView;
		}

		/**
		 * Set the title text shown on the ShowcaseView.
		 */
		public Builder setContentTitle(int resId) {
			return setContentTitle(activity.getString(resId));
		}

		/**
		 * Set the title text shown on the ShowcaseView.
		 */
		public Builder setContentTitle(CharSequence title) {
			showcaseView.setContentTitle(title);
			return this;
		}

		/**
		 * Set the descriptive text shown on the ShowcaseView.
		 */
		public Builder setContentText(int resId) {
			return setContentText(activity.getString(resId));
		}

		/**
		 * Set the descriptive text shown on the ShowcaseView.
		 */
		public Builder setContentText(CharSequence text) {
			showcaseView.setContentText(text);
			return this;
		}

		/**
		 * Set a image below the description.
		 */
		public Builder setImage(int res) {
			showcaseView.setImage(res);
			return this;
		}

		/**
		 * Set a icon in beside the title.
		 */
		public Builder setIcon(int res) {
			showcaseView.setIcon(res);
			return this;
		}

		/**
		 * Set the target of the showcase.
		 * 
		 * @param target a {@link com.github.amlcurran.showcaseview.targets.Target} representing the item to showcase
		 *            (e.g., a button, or action item).
		 */
		public Builder setTarget(Target target) {
			showcaseView.setTarget(target);
			return this;
		}

		/**
		 * Set the style of the ShowcaseView. See the sample app for example styles.
		 */
		public Builder setStyle(int theme) {
			showcaseView.setStyle(theme);
			return this;
		}

		/**
		 * Set a listener which will override the button clicks.
		 * <p/>
		 * Note that you will have to manually hide the ShowcaseView
		 */
		public Builder setOnClickListener(OnClickListener onClickListener) {
			showcaseView.setButtonsOnClickListener(onClickListener);
			return this;
		}

		/**
		 * No target showcase.
		 */
		public Builder setNoTarget() {
			showcaseView.setTarget(null);
			return this;
		}
	}

	/**
	 * Set whether the text should be centred in the screen, or left-aligned (which is the default).
	 */
	public void setShouldCentreText(boolean shouldCentreText) {
		this.shouldCentreText = shouldCentreText;
		hasAlteredText = true;
		invalidate();
	}

	/**
	 * Change the position of the ShowcaseView's button from the default bottom-right position.
	 * 
	 * @param layoutParams a {@link android.widget.RelativeLayout.LayoutParams} representing the new position of the
	 *            button
	 */
	public void setButtonPosition(RelativeLayout.LayoutParams layoutParams) {
		mTickFollowButton.setLayoutParams(layoutParams);
	}

	/**
	 * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#setStyle(int)
	 */
	public void setStyle(int theme) {
		TypedArray array = getContext().obtainStyledAttributes(theme, R.styleable.ShowcaseView);
		updateStyle(array, true);
	}

	private void updateStyle(TypedArray styled, boolean invalidate) {
		int backgroundColor = styled.getColor(R.styleable.ShowcaseView_sv_backgroundColor, Color.argb(230, 80, 80, 80));
		int showcaseColor = styled.getColor(R.styleable.ShowcaseView_sv_showcaseColor, Color.argb(255, 84, 178, 169));
		int titleTextAppearance = styled.getResourceId(R.styleable.ShowcaseView_sv_titleTextAppearance,
				R.style.TextAppearance_ShowcaseView_Title);
		int detailTextAppearance = styled.getResourceId(R.styleable.ShowcaseView_sv_detailTextAppearance,
				R.style.TextAppearance_ShowcaseView_Detail);

		styled.recycle();

		showcaseDrawer.setShowcaseCircleColor(showcaseColor);
		showcaseDrawer.setBackgroundColour(backgroundColor);
		textDrawer.setTitleStyling(titleTextAppearance);
		textDrawer.setDetailStyling(detailTextAppearance);
		hasAlteredText = true;

		if (invalidate) {
			invalidate();
		}
	}

	private RelativeLayout.LayoutParams getLayoutParams(int pos) {
		int margin = (int) getResources().getDimension(R.dimen.button_padding);
		RelativeLayout.LayoutParams lps = (LayoutParams) generateDefaultLayoutParams();
		lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lps.addRule(pos);
		lps.setMargins(margin, margin, margin, margin);
		return lps;

	}

}

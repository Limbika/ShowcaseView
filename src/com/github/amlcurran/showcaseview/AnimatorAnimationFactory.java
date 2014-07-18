package com.github.amlcurran.showcaseview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

class AnimatorAnimationFactory implements AnimationFactory {

    private static final float INVISIBLE = 0f;
    private static final float VISIBLE = 1f;

    private final AccelerateDecelerateInterpolator interpolator;

    public AnimatorAnimationFactory() {
        interpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public void fadeInView(View target, long duration, final AnimationStartListener listener) {
        AlphaAnimation animation = new AlphaAnimation(INVISIBLE, VISIBLE);
        animation.setDuration(duration);
        animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				listener.onAnimationStart();
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {}
		});
        target.startAnimation(animation);
    }

    @Override
    public void fadeOutView(View target, long duration, final AnimationEndListener listener) {
    	AlphaAnimation animation = new AlphaAnimation(VISIBLE, INVISIBLE);
        animation.setDuration(duration);
        animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				listener.onAnimationEnd();
			}
		});
        target.startAnimation(animation);
    }

    @Override
    public void animateTargetToPoint(ShowcaseView showcaseView, Point point) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator xAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseX", point.x);
        ObjectAnimator yAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseY", point.y);
        set.playTogether(xAnimator, yAnimator);
        set.setInterpolator(interpolator);
        set.start();
    }

}

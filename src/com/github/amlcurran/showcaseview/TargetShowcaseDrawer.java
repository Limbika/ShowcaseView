package com.github.amlcurran.showcaseview;

import android.content.res.Resources;

public class TargetShowcaseDrawer extends StandardShowcaseDrawer {

	public TargetShowcaseDrawer(Resources resources) {
		super(resources);
	}

	@Override
	protected int getTargetDrawableResource() {
		return R.drawable.circle_target;
	}
}

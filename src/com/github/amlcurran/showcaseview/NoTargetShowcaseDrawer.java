package com.github.amlcurran.showcaseview;

import android.content.res.Resources;

public class NoTargetShowcaseDrawer extends StandardShowcaseDrawer {

	public NoTargetShowcaseDrawer(Resources resources) {
		super(resources);
	}

	@Override
	protected int getTargetDrawableResource() {
		return R.drawable.circle_no_target;
	}
}

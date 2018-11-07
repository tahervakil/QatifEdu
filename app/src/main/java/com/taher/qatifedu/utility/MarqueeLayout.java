package com.taher.qatifedu.utility;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class MarqueeLayout  extends FrameLayout {
	private Animation animation;

	public MarqueeLayout(Context context) {
		super(context);

		animation = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF, +1f,
			Animation.RELATIVE_TO_SELF,	-1f,
			Animation.RELATIVE_TO_SELF, 0f,
			Animation.RELATIVE_TO_SELF, 0f
		);

		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.RESTART);
	}

	public void setDuration(int durationMillis) {
		animation.setDuration(durationMillis);
	}
	
	public void startAnimation() {
		startAnimation(animation);
	}
}




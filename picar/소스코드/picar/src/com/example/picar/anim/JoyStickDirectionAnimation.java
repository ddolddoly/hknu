package com.example.picar.anim;

import com.example.picar.R;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class JoyStickDirectionAnimation implements AnimationListener {
	
	View v;
	
	
	public JoyStickDirectionAnimation(View v) {
		this.v = v;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		v.setBackgroundResource(R.drawable.btn_control_normal);
	}

	@Override
	public void onAnimationRepeat(Animation animation) { }

	@Override
	public void onAnimationStart(Animation animation) {
		v.setPressed(false);
		v.setBackgroundResource(R.drawable.btn_control_pressed);
	}
}

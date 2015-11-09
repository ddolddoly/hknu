package com.example.picar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

public class PagerAdapterClass extends PagerAdapter {

	// 상수 선언
	private final static int PAGE_COUNT = 9;
	
	// 변수 선언
	private LayoutInflater mInflater;
	private int currentPosition = 0;
	
	@SuppressLint("InflateParams")
	public PagerAdapterClass(Context context) {
		super();
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}
	
	public int getCurrentPosition() {
		return currentPosition;
	}
	
	@SuppressLint("InflateParams")
	public Object instantiateItem(View pager, int position) {
		View v = null;
		currentPosition = position;
		if (position == 0) {
			v = mInflater.inflate(R.layout.layout_tutorial1, null);
		} else if (position == 1) {
			v = mInflater.inflate(R.layout.layout_tutorial2, null);
		} else if (position == 2) {
			v = mInflater.inflate(R.layout.layout_tutorial3, null);
		} else if (position == 3) {
			v = mInflater.inflate(R.layout.layout_tutorial4, null);
		} else if (position == 4) {
			v = mInflater.inflate(R.layout.layout_tutorial5, null);
		} else if (position == 5) {
			v = mInflater.inflate(R.layout.layout_tutorial6, null);
		} else if (position == 6) {
			v = mInflater.inflate(R.layout.layout_tutorial7, null);
		} else if (position == 7) {
			v = mInflater.inflate(R.layout.layout_tutorial8, null);
		} else {
			v = mInflater.inflate(R.layout.layout_tutorial9, null);
		}
		
		((ViewPager)pager).addView(v, 0);
		
		return v;
	}
	
	public void destroyItem(View pager, int position, Object view) {
		((ViewPager)pager).removeView((View)view);
	}

	@Override
	public boolean isViewFromObject(View pager, Object obj) {
		return pager == obj;
	}
	
	public void restoreState(Parcelable arg0, ClassLoader arg1) {}
    public Parcelable saveState() { return null; }
    public void startUpdate(View arg0) {}
    public void finishUpdate(View arg0) {}
}

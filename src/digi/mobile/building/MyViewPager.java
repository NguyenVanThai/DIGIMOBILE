package digi.mobile.building;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	private boolean enabled;

	public MyViewPager(Context context) {
		super(context);
		this.enabled = false;
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.enabled = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	@Override
	protected void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		super.onPageScrolled(arg0, arg1, arg2);
	}

	public void setPagingEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
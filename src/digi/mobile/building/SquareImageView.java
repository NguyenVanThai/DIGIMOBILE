
package digi.mobile.building;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {
	public SquareImageView(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new square image view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	
	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); // Snap to
																		// width
	}
}
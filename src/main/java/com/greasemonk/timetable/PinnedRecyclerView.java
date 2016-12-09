package com.greasemonk.timetable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Wiebe Geertsma on 8-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 * 
 * Simple RecyclerView that will render the first item always at the starting position.
 */
public class PinnedRecyclerView extends RecyclerView
{
	private boolean pin = true;
	public View v;
	
	public PinnedRecyclerView(Context context)
	{
		super(context);
	}
	
	public PinnedRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public PinnedRecyclerView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}
	
	/**
	 * Set the first item as pinned
	 * @param pin TRUE if the item should be pinned
	 */
	public void setPin(boolean pin)
	{
		this.pin = pin;
	}
	
	/**
	 * Returns wether the first item is pinned or not.
	 * @return TRUE if pinned
	 */
	public boolean getIsPinned()
	{
		return pin;
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
		if (pin && v != null)
		{
			canvas.save();
			Rect rect = new Rect(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
			canvas.clipRect(rect);
			v.draw(canvas);
			canvas.restore();
		}
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		if (pin)
			v = v == null ? getChildAt(0) : v;
	}
}
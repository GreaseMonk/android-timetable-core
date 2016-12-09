package com.greasemonk.timetable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Wiebe Geertsma on 8-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class PinnedRecyclerView extends RecyclerView
{
	private boolean needPin = true;
	public View frameLayout;
	
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
	
	public void setNeedPin(boolean needPin)
	{
		this.needPin = needPin;
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
		if (needPin && frameLayout != null)
		{
			canvas.save();
			Rect rect = new Rect(0, 0, frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight());
			canvas.clipRect(rect);
			frameLayout.draw(canvas);
			canvas.restore();
		}
		
	}
}
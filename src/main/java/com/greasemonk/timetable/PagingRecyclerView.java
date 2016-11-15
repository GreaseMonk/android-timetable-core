package com.greasemonk.timetable;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Wiebe Geertsma on 15-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class PagingRecyclerView extends RecyclerView
{
	private static final int SWIPE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	private GestureDetector gestureDetector;
	private PagingDelegate pagingDelegate;
	
	public PagingRecyclerView(Context context)
	{
		super(context);
		init();
	}
	
	public PagingRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	
	public PagingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}
	
	private void init()
	{
		gestureDetector = new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
			{
				boolean result = false;
				try {
					float diffY = e2.getY() - e1.getY();
					float diffX = e2.getX() - e1.getX();
					if (Math.abs(diffX) > Math.abs(diffY)) {
						if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
							if (diffX > 0) {
								if(pagingDelegate != null)
									pagingDelegate.onSwipeRight();
							} else {
								if(pagingDelegate != null)
									pagingDelegate.onSwipeLeft();
							}
						}
						result = true;
					}
					/*else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							//onSwipeBottom();
						} else {
							//onSwipeTop();
						}
					}*/
					result = true;
					
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return result;
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		gestureDetector.onTouchEvent(e);
		
		switch (e.getAction())
		{
			case MotionEvent.ACTION_MOVE:
				return true;
		}
		
		return false;
	}
	
	public void setPagingDelegate(PagingDelegate delegate)
	{
		this.pagingDelegate = delegate;
	}
}

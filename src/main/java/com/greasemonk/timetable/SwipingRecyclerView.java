package com.greasemonk.timetable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.Scroller;

/**
 * Created by Wiebe Geertsma on 17-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class SwipingRecyclerView extends RecyclerView
{
	private static final int SWIPE_THRESHOLD = 500;
	private static final float AXIS_X_MIN = -2f;
	private static final float AXIS_X_MAX = 2f;
	private static final float AXIS_Y_MIN = 0f;
	private static final float AXIS_Y_MAX = 0f;
	
	private float startX, offsetX;
	private SimplePagingDelegate delegate;
	private GestureDetector gestureDetector;
	private Scroller mScroller;
	
	private Rect mContentRect = new Rect();
	private RectF mCurrentViewport = new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);
	private RectF mScrollerStartViewport = new RectF(); // Used only for zooms and flings.
	private Point mSurfaceSizeBuffer = new Point();
	
	public SwipingRecyclerView(Context context)
	{
		this(context, null, 0);
	}
	
	public SwipingRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	
	public SwipingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mScroller = new Scroller(getContext());
		gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()
		{
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
			{
				float viewportOffsetX = distanceX * mCurrentViewport.width() / mContentRect.width();
				float viewportOffsetY = -distanceY * mCurrentViewport.height() / mContentRect.height();
				
				setViewportBottomLeft(mCurrentViewport.left + viewportOffsetX, mCurrentViewport.bottom + viewportOffsetY);
				return true;
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
			{
				fling((int) -velocityX, (int) -velocityY);
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e)
			{
				mScrollerStartViewport.set(mCurrentViewport);
				mScroller.forceFinished(true);
				ViewCompat.postInvalidateOnAnimation(SwipingRecyclerView.this);
				return true;
			}
		});
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		if(!mScroller.isFinished())
		{
			float swipeThreshold = mContentRect.width() / 8;
			float val = -mScroller.getCurrX();
			float alpha = Math.min(Math.abs(val) / swipeThreshold, swipeThreshold);
			int a = (int) ((1f - alpha) * 255);
			
			canvas.translate(val, 0);
			canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), a, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
		}
		
		super.onDraw(canvas);
		
		// Clips the next few drawing operations to the content area
		int clipRestoreCount = canvas.save();
		canvas.clipRect(mContentRect);
		
		// Removes clipping rectangle
		canvas.restoreToCount(clipRestoreCount);
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}
	
	public void setDelegate(SimplePagingDelegate delegate)
	{
		this.delegate = delegate;
	}
	
	@Override
	public boolean fling(final int velocityX, final int velocityY)
	{
		if(Math.abs(velocityX) < SWIPE_THRESHOLD)
			return false;
		
		final int firstDistanceX = velocityX > 0 ? mContentRect.width() / 8 : -mContentRect.width() / 8;
		// Flings use math in pixels (as opposed to math based on the viewport).
		computeScrollSurfaceSize(mSurfaceSizeBuffer);
		mScrollerStartViewport.set(mCurrentViewport);
		final int startX = (int) (mSurfaceSizeBuffer.x * (mScrollerStartViewport.left - AXIS_X_MIN) / (
				AXIS_X_MAX - AXIS_X_MIN));
		//final int startY = (int) (mSurfaceSizeBuffer.y * (AXIS_Y_MAX - mScrollerStartViewport.bottom) / (
		//		AXIS_Y_MAX - AXIS_Y_MIN));
		mScroller.forceFinished(true);
		mScroller.startScroll(startX,
				0,
				firstDistanceX,
				0);
		
		final int duration = mScroller.getDuration();
		final int dX = mContentRect.width() / 8;
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				int start = mContentRect.width() / 8;
				if (velocityX > 0)
				{
					start = -mContentRect.width() / 8;
					delegate.onSwipeLeft();
				}
				else
					delegate.onSwipeRight();
				
				mScroller.forceFinished(true);
				mScroller.startScroll(start, 0, -firstDistanceX, 0, 200);
				ViewCompat.postInvalidateOnAnimation(SwipingRecyclerView.this);
			}
		}, duration);
		ViewCompat.postInvalidateOnAnimation(this);
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mContentRect.set(getPaddingLeft(),
				getPaddingTop(),
				getWidth() + getPaddingRight(),
				getHeight() + getPaddingBottom());
	}
	
	private void setViewportBottomLeft(float x, float y)
	{
		float curWidth = mCurrentViewport.width();
		float curHeight = mCurrentViewport.height();
		x = Math.max(AXIS_X_MIN, Math.min(x, AXIS_X_MAX - curWidth));
		y = Math.max(AXIS_Y_MIN + curHeight, Math.min(y, AXIS_Y_MAX));
		
		mCurrentViewport.set(x, y - curHeight, x + curWidth, y);
		ViewCompat.postInvalidateOnAnimation(this);
	}
	
	private void computeScrollSurfaceSize(Point out)
	{
		out.set((int) (mContentRect.width() * (AXIS_X_MAX - AXIS_X_MIN)
						/ mCurrentViewport.width()),
				(int) (mContentRect.height() * (AXIS_Y_MAX - AXIS_Y_MIN)
						/ mCurrentViewport.height()));
	}
	
	@Override
	public void computeScroll()
	{
		super.computeScroll();
		if (mScroller.computeScrollOffset())
		{
			computeScrollSurfaceSize(mSurfaceSizeBuffer);
			int currX = mScroller.getCurrX();
			int currY = mScroller.getCurrY();
			
			float currXRange = AXIS_X_MIN + (AXIS_X_MAX - AXIS_X_MIN)
					* currX / mSurfaceSizeBuffer.x;
			float currYRange = AXIS_Y_MAX - (AXIS_Y_MAX - AXIS_Y_MIN)
					* currY / mSurfaceSizeBuffer.y;
			setViewportBottomLeft(currXRange, currYRange);
		}
	}
}

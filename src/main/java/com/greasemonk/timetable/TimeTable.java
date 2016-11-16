package com.greasemonk.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.greasemonk.timetable.rows.InitialsRow;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTable<T extends AbstractRowItem> extends FrameLayout
{
	private static final int SWIPE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	private GestureDetector gestureDetector;
	
	private View view;
	private RecyclerView recyclerView;
	private List<T> items;
	private List<InitialsRow> rows;
	private FastItemAdapter<InitialsRow> adapter;
	private ProgressBar progressBar;
	private Calendar left = Calendar.getInstance();
	private Calendar right = Calendar.getInstance();
	private TextView[] textViews = new TextView[7];
	
	private final RecyclerView.OnFlingListener flingListener = new RecyclerView.OnFlingListener()
	{
		@Override
		public boolean onFling(int velocityX, int velocityY)
		{
			int velocity = velocityY > 0 ? velocityY : velocityY * -1;
			if (Math.abs(velocity) > SWIPE_VELOCITY_THRESHOLD) 
			{
				if (velocityY > 0) 
				{
					onSwipeRight();
					return true;
				} else 
					{
					onSwipeLeft();
					return true;
				}
			}
			return false;
		}
	};
	
	
	private int columnCount;
	
	public TimeTable(Context context)
	{
		super(context);
		init();
	}
	
	public TimeTable(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	
	public TimeTable(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public TimeTable(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	private void init()
	{
		view = inflate(getContext(), com.greasemonk.timetable.R.layout.paging_timetable_view, null);
		
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setOnFlingListener(flingListener);
		
		textViews[0] = (TextView) view.findViewById(R.id.text1);
		textViews[1] = (TextView) view.findViewById(R.id.text2);
		textViews[2] = (TextView) view.findViewById(R.id.text3);
		textViews[3] = (TextView) view.findViewById(R.id.text4);
		textViews[4] = (TextView) view.findViewById(R.id.text5);
		textViews[5] = (TextView) view.findViewById(R.id.text6);
		textViews[6] = (TextView) view.findViewById(R.id.text7);
		
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		progressBar.setVisibility(GONE);
		left.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		right.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		
		/*gestureDetector = new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
			{
				boolean result = false;
				if(e1 == null || e2 == null)
					return false;
				try {
					float diffY = e2.getY() - e1.getY();
					float diffX = e2.getX() - e1.getX();
					if (Math.abs(diffX) > Math.abs(diffY)) {
						
					}
					// This is for up/down gestures which we won't block so the recyclerview can scroll.
					*//*else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							//onSwipeBottom();
						} else {
							//onSwipeTop();
						}
					}*//*
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return result;
			}
		});*/
		
		addView(view);
		requestLayout();
	}
	
	public void onSwipeRight()
	{
		left.add(Calendar.WEEK_OF_YEAR, -1);
		right.add(Calendar.WEEK_OF_YEAR, -1);
		update();
	}
	
	public void onSwipeLeft()
	{
		left.add(Calendar.WEEK_OF_YEAR, 1);
		right.add(Calendar.WEEK_OF_YEAR, 1);
		update();
	}
	
	public void setColumnCount(int columnCount)
	{
		this.columnCount = columnCount;
	}
	
	private void update()
	{
		if(items != null)
			update(items);
	}
	
	public void update(@NonNull List<T> items)
	{
		this.items = items;
		rows = new ArrayList<>();
		for(AbstractRowItem item : items)
		{
			// Left and Right are the TimeTable's date range
			boolean planStartsBeforeLeft = left.getTime().after(item.getPlanningStart());
			boolean planEndsAfterRight = right.getTime().before(item.getPlanningEnd());
			
			int start, span;
			if(planStartsBeforeLeft && planEndsAfterRight)
			{
				start = 0;
				span = columnCount;
			}
			else
			{
				// Calculate the start of the SpannableBar
				if(planStartsBeforeLeft)
					start = 0;
				else
				{
					long difference = item.getPlanningStart().getTime() - left.getTime().getTime();
					start = Math.round(TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS));
				}
				
				// Calculate the span of the SpannableBar
				if(planEndsAfterRight)
					span = columnCount - start;
				else
				{
					long difference = item.getPlanningEnd().getTime() - right.getTime().getTime();
					span = Math.round(TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS));
				}
			}
			
			// Do not add rows that display nothing.
			if(span > 0)
				rows.add(new InitialsRow(start, span, item));
		}
		
		// Sort by employee name
		Collections.sort(rows, InitialsRow.getComparator());
		String temp = null;
		for(InitialsRow row : rows)
		{
			if(temp == null || !temp.equals(row.getItem().getEmployeeName()))
			{
				temp = row.getItem().getEmployeeName();
				row.setInitialsVisibility(true); // Only display the initials on the top one if there's multiple
			}
		}
		
		if (adapter == null)
		{
			adapter = new FastItemAdapter<>();
			adapter.setHasStableIds(true);
			adapter.withSelectable(false);
			recyclerView.setAdapter(adapter);
		}
		
		adapter.set(rows);
		updateTitles();
		requestLayout();
	}
	
	private void updateTitles()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(left.getTime());
		for(int i = 0; i < 7; i++)
		{
			textViews[i].setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
			calendar.add(Calendar.DATE, 1);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		//gestureDetector.onTouchEvent(e) ||
		return super.onTouchEvent(e);
	}
}
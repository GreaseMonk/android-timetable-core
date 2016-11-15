package com.greasemonk.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTable extends FrameLayout
{
	private static final int SWIPE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	
	public enum TimePeriod
	{
		DAY, WEEK, MONTH
	}
	
	private View view;
	private RecyclerView recyclerView;
	private List<InitialsRow> rows;
	private FastItemAdapter<InitialsRow> adapter;
	private TimePeriod timePeriod = TimePeriod.DAY;
	private ProgressBar progressBar;
	private Calendar left = Calendar.getInstance();
	private Calendar right = Calendar.getInstance();
	private TextView[] textViews = new TextView[7];
	private GestureDetector gestureDetector;
	
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
								pageLeft();
							} else {
								pageRight();
							}
						}
						result = true;
					}
					else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							//onSwipeBottom();
						} else {
							//onSwipeTop();
						}
					}
					result = true;
					
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return result;
			}
		});
		
		addView(view);
		requestLayout();
	}
	
	public void pageLeft()
	{
		left.add(Calendar.WEEK_OF_YEAR, -1);
		right.add(Calendar.WEEK_OF_YEAR, -1);
		update();
		requestLayout();
	}
	
	public void pageRight()
	{
		left.add(Calendar.WEEK_OF_YEAR, 1);
		right.add(Calendar.WEEK_OF_YEAR, 1);
		update();
		requestLayout();
	}
	
	public void setColumnCount(int columnCount)
	{
		this.columnCount = columnCount;
	}
	
	public void update()
	{
		setRows(rows);
	}
	
	public void update(Iterable<AbstractRowItem> list) 
	{
		rows = new ArrayList<>();
		for(AbstractRowItem item : list)
		{
			rows.add(new InitialsRow(timePeriod, item.getPlanningStart(), item.getPlanningEnd(), item));
		}
		
		setRows(rows);
	}
	
	private void setRows(List<InitialsRow> rows)
	{
		// Sort by employee name
		Collections.sort(rows, InitialsRow.getComparator());
		String temp = null;
		for(InitialsRow row : rows)
		{
			if(temp == null || temp.equals(row.getItem().getEmployeeName()))
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
	public boolean onTouchEvent(MotionEvent event)
	{
		gestureDetector.onTouchEvent(event);
		
		return super.onTouchEvent(event);
	}
}
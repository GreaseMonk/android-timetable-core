package com.greasemonk.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.text.DateFormatSymbols;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTable<T extends AbstractRowItem> extends FrameLayout implements SimplePagingDelegate
{
	private static final int DEFAULT_COLUMN_COUNT = 7;
	private static final int DEFAULT_CURRENT_DAY_COLOR = Color.argb(255,33,150,243);
	
	private View view;
	private TextView title;
	private SwipingRecyclerView recyclerView;
	private List<T> items;
	private List<TimeTableRow> rows;
	private FastItemAdapter<TimeTableRow> adapter;
	private ProgressBar progressBar;
	private Calendar left = Calendar.getInstance();
	private Calendar right = Calendar.getInstance();
	private TextView[] textViews;
	private int columnCount;
	
	private int titlesColor, nowTitlesColor;
	
	private Paint linePaint;
	
	public TimeTable(Context context)
	{
		super(context);
		init(null);
	}
	
	public TimeTable(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs);
	}
	
	public TimeTable(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(attrs);
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public TimeTable(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(attrs);
	}
	
	private void init(@Nullable AttributeSet attrs)
	{
		view = inflate(getContext(), com.greasemonk.timetable.R.layout.paging_timetable_view, null);
		title = (TextView) view.findViewById(R.id.title);
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		recyclerView = (SwipingRecyclerView) view.findViewById(R.id.recycler_view);
		textViews = new TextView[]{
				(TextView) view.findViewById(R.id.text1),
				(TextView) view.findViewById(R.id.text2),
				(TextView) view.findViewById(R.id.text3),
				(TextView) view.findViewById(R.id.text4),
				(TextView) view.findViewById(R.id.text5),
				(TextView) view.findViewById(R.id.text6),
				(TextView) view.findViewById(R.id.text7),
				(TextView) view.findViewById(R.id.text8),
				(TextView) view.findViewById(R.id.text9),
				(TextView) view.findViewById(R.id.text10),
				(TextView) view.findViewById(R.id.text11),
				(TextView) view.findViewById(R.id.text12),
				(TextView) view.findViewById(R.id.text13),
				(TextView) view.findViewById(R.id.text14)};
		
		if (attrs != null)
		{
			TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
					attrs,
					R.styleable.TimeTable,
					0, 0);
			try
			{
				titlesColor = typedArray.getColor(R.styleable.TimeTable_calTitlesColor, textViews[0].getCurrentTextColor());
				nowTitlesColor = typedArray.getColor(R.styleable.TimeTable_calNowTitlesColor, DEFAULT_CURRENT_DAY_COLOR);
			} finally
			{
				typedArray.recycle();
			}
		}
		
		
		progressBar.setVisibility(GONE);
		
		
		linePaint = new Paint();
		linePaint.setColor(Color.argb(48, 0, 0, 0));
		
		// Initialize the list and make sure we can swipe to change pages.
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setDelegate(this);
		
		left.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		right.add(Calendar.DATE, 6);
		
		columnCount = DEFAULT_COLUMN_COUNT;
		
		addView(view);
		requestLayout();
	}
	
	@Override
	public void onSwipeRight()
	{
		left.add(Calendar.WEEK_OF_YEAR, -1);
		right.add(Calendar.WEEK_OF_YEAR, -1);
		update();
	}
	
	@Override
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
		if (items != null)
			update(items);
	}
	
	public void update(@NonNull List<T> items)
	{
		this.items = items;
		rows = new ArrayList<>();
		
		int currentDayColumn = -1;
		Calendar calendar = Calendar.getInstance();
		// Check if it is the current week we are drawing for the gray highlight for the current day.
		if (calendar.getTime().getTime() > left.getTime().getTime() && calendar.getTime().getTime() < right.getTime().getTime())
		{
			long difference = calendar.getTime().getTime() - left.getTime().getTime();
			currentDayColumn = Math.round(TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS));
		}
		
		
		for (AbstractRowItem item : items)
		{
			// Left and Right are the TimeTable's date range
			boolean planStartsBeforeLeft = left.getTime().getTime() > item.getPlanningStart().getTime();
			boolean planEndsAfterRight = right.getTime().getTime() < item.getPlanningEnd().getTime();
			
			int start, span;
			if (planStartsBeforeLeft && planEndsAfterRight)
			{
				start = 0;
				span = columnCount;
			}
			else
			{
				// Calculate the start of the SpannableBar
				if (planStartsBeforeLeft)
					start = 0;
				else
				{
					long difference = item.getPlanningStart().getTime() - left.getTime().getTime();
					start = Math.round(TimeUnit.DAYS.convert(Math.abs(difference), TimeUnit.MILLISECONDS));
				}
				
				// Calculate the span of the SpannableBar
				if (planEndsAfterRight)
					span = columnCount - start;
				else
				{
					long difference = item.getPlanningEnd().getTime() - right.getTime().getTime();
					span = Math.round(TimeUnit.DAYS.convert(Math.abs(difference), TimeUnit.MILLISECONDS));
				}
			}
			
			// Do not add rows that display nothing.
			if (span > 0)
			{
				TimeTableRow row = new TimeTableRow(start, span, item);
				row.setTodayColumn(currentDayColumn);
				rows.add(row);
			}
		}
		
		// Sort by employee name
		Collections.sort(rows, TimeTableRow.getComparator());
		String temp = null;
		for (TimeTableRow row : rows)
		{
			if (temp == null || !temp.equals(row.getItem().getEmployeeName()))
			{
				temp = row.getItem().getEmployeeName();
				row.setNameVisibility(true); // Only display the name on the top one if there's multiple
			}
			else
				row.setNameVisibility(false);
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
		for (int i = 0; i < 7; i++)
		{
			textViews[i].setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
			calendar.add(Calendar.DATE, 1);
		}
		
		String[] namesOfDays = DateFormatSymbols.getInstance().getShortWeekdays();
		int current = getTodayColumn();
		if(current >= 0)
		{
			textViews[current].setTextColor(nowTitlesColor);
			textViews[current + 7].setTextColor(nowTitlesColor);
		}
		else
		{
			for(TextView textView : textViews)
				textView.setTextColor(titlesColor);
		}
		textViews[7].setText(namesOfDays[2]);
		textViews[8].setText(namesOfDays[3]);
		textViews[9].setText(namesOfDays[4]);
		textViews[10].setText(namesOfDays[5]);
		textViews[11].setText(namesOfDays[6]);
		textViews[12].setText(namesOfDays[7]);
		textViews[13].setText(namesOfDays[1]);
		calendar = Calendar.getInstance();
		// Values calculated for the WEEK_OF_YEAR field range from 1 to 53. 
		String titleText = "week " + (calendar.get(Calendar.WEEK_OF_YEAR) - 1) + ", ";
		titleText += calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		titleText += " " + calendar.get(Calendar.YEAR);
		
		title.setText(titleText);
	}
	
	/**
	 * Get the column number if the current display contains the current time.
	 * 
	 * @return the column number for the current time, or -1 if non existent in current display
	 */
	private int getTodayColumn()
	{
		Calendar calendar = Calendar.getInstance();
		// Check if it is the current week we are drawing for the gray highlight for the current day.
		if (calendar.getTime().getTime() > left.getTime().getTime() && calendar.getTime().getTime() < right.getTime().getTime())
		{
			long difference = calendar.getTime().getTime() - left.getTime().getTime();
			return Math.round(TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS));
		}
		return -1;
	}
	
	/**
	 * Set the color for day numbers and day titles
	 * 
	 * @param color the color to set
	 */
	public void setTitlesColor(int color)
	{
		titlesColor = color;
		invalidate();
	}
	
	/**
	 * Set the color for the current day/week/month number and day title
	 * 
	 * @param color the color to set
	 */
	public void setNowTitlesColor(int color)
	{
		nowTitlesColor = color;
	}
}
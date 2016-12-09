package com.greasemonk.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import org.joda.time.*;

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
	private static final int DEFAULT_CURRENT_DAY_COLOR = Color.argb(255, 33, 150, 243);
	
	private View view;
	private TextView title;
	private RecyclerView recyclerView, guideX, guideY;
	private List<T> items;
	private List<TimeTableRow> rows;
	private FastItemAdapter<TimeTableRow> adapter;
	private ProgressBar progressBar;
	private DateTime left;
	private DateTime right;
	private TextView[] textViews;
	private int columnCount;
	
	private List<RecyclerView> observedList;
	
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
		view = inflate(getContext(), com.greasemonk.timetable.R.layout.timetable_layout, null);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		guideY = (RecyclerView) view.findViewById(R.id.guideY);
		guideX = (RecyclerView) view.findViewById(R.id.guideX);
		
		initGuideX();
		initGuideY();
		//recyclerView.addOnScrollListener(new SynchroScrollListener(guideY, true, true));
		
		List<PannableItem> pannableItems = new ArrayList<>();
		int row = 0;
		int column = 0;
		for (int i = 0; i < 15000; i++)
		{
			if (column == 100)
			{
				column = 0;
				row++;
			}
			pannableItems.add(new PannableItem(row, column));
			column++;
		}
		
		observedList = new ArrayList<RecyclerView>()
		{{
			add(guideX);
			add(guideY);
		}};
		
		FixedGridLayoutManager mgr = new FixedGridLayoutManager();
		mgr.setTotalColumnCount(100);
		FastItemAdapter<PannableItem> adapter = new FastItemAdapter<>();
		adapter.set(pannableItems);
		recyclerView.setLayoutManager(mgr);
		recyclerView.setAdapter(adapter);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			int state;
			
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy)
			{
				super.onScrolled(recyclerView, dx, dy);
				if (state == RecyclerView.SCROLL_STATE_IDLE)
				{
					return;
				}
				FixedGridLayoutManager layoutMgr = (FixedGridLayoutManager) recyclerView.getLayoutManager();
				int firstPos = layoutMgr.getFirstVisibleRow();
				View firstVisibleItem = layoutMgr.getChildAt(0);
				if (firstVisibleItem != null)
				{
					int decoratedY = layoutMgr.getDecoratedBottom(firstVisibleItem);
					int decoratedX = layoutMgr.getDecoratedRight(firstVisibleItem);
					
					LinearLayoutManager managerX = (LinearLayoutManager) observedList.get(0).getLayoutManager();
					LinearLayoutManager managerY = (LinearLayoutManager) observedList.get(1).getLayoutManager();
					
					if(managerX != null)
						managerX.scrollToPositionWithOffset(firstPos + 1, decoratedX);
					if(managerY != null)
						managerY.scrollToPositionWithOffset(firstPos + 1, decoratedY);
				}
			}
			
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState)
			{
				super.onScrollStateChanged(recyclerView, newState);
				state = newState;
			}
		});
		
		/*
		title = (TextView) view.findViewById(R.id.title);
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
		
		
		//progressBar.setVisibility(GONE);
		
		
		linePaint = new Paint();
		linePaint.setColor(Color.argb(48, 0, 0, 0));
		
		// Initialize the list and make sure we can swipe to change pages.
		FixedGridLayoutManager mgr = new FixedGridLayoutManager();
		mgr.setTotalColumnCount(1);
		recyclerView.setLayoutManager(mgr);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		//recyclerView.setDelegate(this);
		
		left = DateTime.now().withDayOfWeek(DateTimeConstants.MONDAY);
		right = DateTime.now().withDayOfWeek(DateTimeConstants.SUNDAY);
		
		columnCount = DEFAULT_COLUMN_COUNT;
		*/
		addView(view);
		requestLayout();
	}
	
	private void initGuideX()
	{
		FastItemAdapter<PannableItem> adapterX = new FastItemAdapter<>();
		guideX.setHasFixedSize(true);
		guideX.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		guideX.addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
		{
			@Override
			public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
			{
				return true;
			}
			
			@Override
			public void onTouchEvent(RecyclerView rv, MotionEvent e)
			{
				
			}
			
			@Override
			public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
			{
				
			}
		});
		List<PannableItem> guideXitems = new ArrayList<>();
		for (int y = 0; y < 100; y++)
		{
			guideXitems.add(new PannableItem());
		}
		guideX.setAdapter(adapterX);
		adapterX.set(guideXitems);
	}
	
	private void initGuideY()
	{
		FastItemAdapter<PannableItem> adapterY = new FastItemAdapter<>();
		guideY.setHasFixedSize(true);
		guideY.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		guideY.addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
		{
			@Override
			public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
			{
				return true;
			}
			
			@Override
			public void onTouchEvent(RecyclerView rv, MotionEvent e)
			{
				
			}
			
			@Override
			public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
			{
				
			}
		});
		List<PannableItem> guideYitems = new ArrayList<>();
		for (int y = 0; y < 150; y++)
		{
			guideYitems.add(new PannableItem());
		}
		guideY.setAdapter(adapterY);
		adapterY.set(guideYitems);
	}
	
	
	@Override
	public void onSwipeRight()
	{
		//left.weekOfWeekyear().addToCopy(-1);
		//right.weekOfWeekyear().addToCopy(-1);
		//update();
	}
	
	@Override
	public void onSwipeLeft()
	{
		//left.weekOfWeekyear().addToCopy(1);
		//right.weekOfWeekyear().addToCopy(1);
		//update();
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
		DateTime now = DateTime.now();
		// Check if it is the current week we are drawing for the gray highlight for the current day.
		if (now.getMillis() > left.getMillis() && now.getMillis() < right.getMillis())
		{
			long difference = now.getMillis() - left.getMillis();
			currentDayColumn = Math.round(TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS));
		}
		
		
		for (AbstractRowItem item : items)
		{
			// Left and Right are the TimeTable's date range
			boolean planStartsBeforeLeft = left.getMillis() > item.getPlanningStart().getTime();
			boolean planEndsAfterRight = right.getMillis() < item.getPlanningEnd().getTime();
			
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
					start = new DateTime(item.getPlanningStart()).dayOfWeek().get() - 1;
					
					//long difference = item.getPlanningStart().getTime() - left.getMillis();
					//start = Math.round(TimeUnit.DAYS.convert(Math.abs(difference), TimeUnit.MILLISECONDS));
				}
				
				// Calculate the span of the SpannableBar
				if (planEndsAfterRight)
					span = columnCount - start;
				else
				{
					span = new DateTime(item.getPlanningEnd()).dayOfWeek().get() - start;
					
					
					//long difference = item.getPlanningEnd().getTime() - right.getMillis();
					//span = Math.round(TimeUnit.DAYS.convert(Math.abs(difference), TimeUnit.MILLISECONDS));
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
		MutableDateTime dateTime = new MutableDateTime(left.getMillis());
		
		for (int i = 0; i < 7; i++)
		{
			textViews[i].setText(Integer.toString(dateTime.dayOfMonth().get()));
			dateTime.add(DurationFieldType.days(), 1);
		}
		
		String[] namesOfDays = DateFormatSymbols.getInstance().getShortWeekdays();
		int current = getTodayColumn();
		if (current >= 0)
		{
			textViews[current].setTextColor(nowTitlesColor);
			textViews[current + 7].setTextColor(nowTitlesColor);
		}
		else
		{
			for (TextView textView : textViews)
				textView.setTextColor(titlesColor);
		}
		textViews[7].setText(namesOfDays[2]);
		textViews[8].setText(namesOfDays[3]);
		textViews[9].setText(namesOfDays[4]);
		textViews[10].setText(namesOfDays[5]);
		textViews[11].setText(namesOfDays[6]);
		textViews[12].setText(namesOfDays[7]);
		textViews[13].setText(namesOfDays[1]);
		
		String titleText = "week " + left.weekOfWeekyear().getAsString() + ", ";
		titleText += left.monthOfYear().getAsText(Locale.getDefault());
		if (left.monthOfYear().get() != right.monthOfYear().get())
			titleText += "/" + right.monthOfYear().getAsText(Locale.getDefault());
		titleText += " " + left.year().getAsString();
		
		title.setText(titleText);
	}
	
	/**
	 * Get the column number if the current display contains the current time.
	 *
	 * @return the column number for the current time, or -1 if non existent in current display
	 */
	private int getTodayColumn()
	{
		DateTime now = DateTime.now();
		// Check if it is the current week we are drawing for the gray highlight for the current day.
		if (now.getMillis() > left.getMillis() && now.getMillis() < right.getMillis())
		{
			long difference = now.getMillis() - left.getMillis();
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
	
	protected DateTime getLeftTime()
	{
		return left;
	}
	
	protected DateTime getTimeRight()
	{
		return right;
	}
	
	
}
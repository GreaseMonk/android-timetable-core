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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import org.joda.time.*;

import java.util.*;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTable<T extends IGridItem, X extends IGuideXItem, Y extends IGuideYItem> extends FrameLayout
{
	private final String DTAG = "TimeTable";
	
	private RecyclerView recyclerView, guideY, guideX;
	private List<TimeTableItem> items;
	private List<RecyclerView> observedList;
	private DateTime left, right;
	
	private FastItemAdapter guideXadapter, guideYadapter, gridAdapter;
	
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
		View view = inflate(getContext(), R.layout.timetable, null);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		guideY = (RecyclerView) view.findViewById(R.id.guideY);
		guideX = (RecyclerView) view.findViewById(R.id.guideX);
		
		
		
		
		
		/*if (attrs != null)
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
		}*/
		
		addView(view);
		requestLayout();
	}
	
	public void setItems(@NonNull List<T> items)
	{
		this.items = new ArrayList<>();
		
		
		if(left == null || right == null)
		{
			left = DateTime.now().monthOfYear().addToCopy(-1);
			right = DateTime.now().monthOfYear().addToCopy(1);
			setTimeRange(left.toDate(), right.toDate());
		}
		
		
		/**
		 * Generate items spanning from start(left) to end(right)
		 */
		DateTime current = left.millisOfDay().addToCopy(1);
		List<GuideXItem> itemsX = new ArrayList<>();
		while(current.getMillis() < right.getMillis())
		{
			itemsX.add(new GuideXItem(current.millisOfDay().addToCopy(1)));
			current = current.dayOfYear().addToCopy(1);
		}
		setGuideXItems((List<X>) itemsX);
		construct(itemsX.size());
		
		
		List<GuideYItem> itemsY = new ArrayList<>();
		int row = 0;
		for(int i = 0; i < items.size(); i++)
		{
			T item = items.get(i);
			
			/**
			 * Find the Y item (Person Name)
			 */
			GuideYItem itemY = null;
			for(GuideYItem guideYitem : itemsY)
			{
				if(guideYitem.getName().equals(item.getPersonName()))
					itemY = guideYitem;
			}
			if(itemY == null)
				itemY = new GuideYItem(item.getPersonName());
			
			itemsY.add(itemY);
			
			/**
			 * Generate an entire row for each day in the X row
			 * Each item that is not between start and end date, will have a blank cell generated.
			 */
			int column = 0;
			for(GuideXItem itemX : itemsX)
			{
				long columnMillis = itemX.getDateTime().getMillis();
				if(columnMillis > item.getStartDate().getTime() && columnMillis < item.getEndDate().getTime())
					this.items.add(new TimeTableItem(item, row, column));
				else
					this.items.add(new TimeTableItem(row, column)); // Make a blank cell
				
				column++;
			}
			
			row++;
		}
		
		
		
		if (gridAdapter == null)
		{
			gridAdapter = new FastItemAdapter<>();
			gridAdapter.setHasStableIds(true);
			gridAdapter.withSelectable(false);
			recyclerView.setAdapter(gridAdapter);
		}
		
		setGuideYItems((List<Y>) itemsY);
		gridAdapter.set(this.items);
		requestLayout();
	}
	
	private void construct(final int itemCount)
	{
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
		
		observedList = new ArrayList<RecyclerView>()
		{{
			add(guideX);
			add(guideY);
		}};
		
		FixedGridLayoutManager mgr = new FixedGridLayoutManager();
		mgr.setTotalColumnCount(itemCount);
		recyclerView.setLayoutManager(mgr);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
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
				
				final LinearLayoutManager managerX = (LinearLayoutManager) observedList.get(0).getLayoutManager();
				final LinearLayoutManager managerY = (LinearLayoutManager) observedList.get(1).getLayoutManager();
				final FixedGridLayoutManager layoutMgr = (FixedGridLayoutManager) recyclerView.getLayoutManager();
				
				final int firstRow = layoutMgr.getFirstVisibleRow();
				final int firstColumn = layoutMgr.getFirstVisibleColumn();
				
				View firstVisibleItem = layoutMgr.getChildAt(0);
				if (firstVisibleItem != null)
				{
					int decoratedY = layoutMgr.getDecoratedBottom(firstVisibleItem);
					int decoratedX = layoutMgr.getDecoratedLeft(firstVisibleItem);
					
					
					
					if(managerX != null)
						managerX.scrollToPositionWithOffset(firstColumn + 1, decoratedX);
					if(managerY != null)
						managerY.scrollToPositionWithOffset(firstRow + 1, decoratedY);
				}
			}
			
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState)
			{
				super.onScrollStateChanged(recyclerView, newState);
				state = newState;
			}
		});
	}
	
	/**
	 * Initializes the horizontal guide which displays the days, dates.
	 * 
	 * @param left the time on the left end
	 * @param right the time on the right end
	 */
	public void setTimeRange(Date left, Date right)
	{
		DateTime timeLeft = new DateTime(left);
		DateTime timeRight = new DateTime(right);
		
		if(timeLeft.getMillis() > timeRight.getMillis())
		{
			Log.e(DTAG, "setTimeRange 'left' cannot be higher than 'right'.");
			return;
		}
		
		this.left = timeLeft;
		this.right = timeRight;
	}
	
	public void setGuideXItems(List<X> items)
	{
		if(guideXadapter == null)
		{
			guideXadapter = new FastItemAdapter();
			guideXadapter.setHasStableIds(true);
			guideXadapter.withSelectable(false);
			guideX.setAdapter(guideXadapter);
		}
			
		
		guideXadapter.set(items);
	}
	
	public void setGuideYItems(List<Y> items)
	{
		if(guideYadapter == null)
		{
			guideYadapter = new FastItemAdapter();
			guideYadapter.setHasStableIds(true);
			guideYadapter.withSelectable(false);
			guideY.setAdapter(guideYadapter);
		}
		
		guideYadapter.set(items);
	}
	
	public DateTime getTimeLeft()
	{
		return left;
	}
	
	public DateTime getTimeRight()
	{
		return right;
	}
	
	/*private void updateTitles()
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
	}*/
}
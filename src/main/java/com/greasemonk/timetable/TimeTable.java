package com.greasemonk.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.*;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTable extends FrameLayout
{
	private final String DTAG = "TimeTable";
	
	private RecyclerView recyclerView, guideY, guideX;
	private List<RecyclerView> observedList;
	private Calendar left, right;
	private TimeRange timeRange;
	private int columns;
	
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
		
		guideY.setHasFixedSize(true);
		guideY.setItemAnimator(null);
		guideX.setHasFixedSize(true);
		guideX.setItemAnimator(null);
		recyclerView.setHasFixedSize(true);
		recyclerView.setItemAnimator(null);
		
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
	
	/**
	 * Sets the items to be displayed. 
	 * 
	 * @param items the items to be displayed.
	 */
	public <T extends IGridItem> void setItems(@NonNull List<T> items)
	{
		if(left == null || right == null)
		{
			left = Calendar.getInstance();
			left.add(Calendar.MONTH, -1);
			left.setTimeInMillis(calendarToMidnightMillis(left));
			right = Calendar.getInstance();
			right.add(Calendar.MONTH, 1);
			right.setTimeInMillis(calendarToMidnightMillis(right));
			setTimeRange(left, right);
		}
		
		// Generate items spanning from start(left) to end(right)
		Calendar current = Calendar.getInstance();
		current.setTimeInMillis(calendarToMidnightMillis(left));
		List<GuideXItem> itemsX = new ArrayList<>();
		while(current.getTimeInMillis() <= right.getTimeInMillis())
		{
			itemsX.add(new GuideXItem(current));
			current.add(Calendar.DATE, 1);
		}
		setGuideXItems(itemsX);
		columns = timeRange.getColumnCount();
		construct(columns);
		
		List<Pair<String, List<IGridItem>>> pairs = new ArrayList<>();
		
		for(int i = 0; i < items.size(); i++)
		{
			T item = items.get(i);
			Pair<String, List<IGridItem>> pair = null;
			for(Pair<String, List<IGridItem>> p : pairs)
			{
				if(p.first.equals(item.getPersonName()))
				{
					pair = p;
					break;
				}
			}
			
			if(pair == null)
				pair = new Pair<String, List<IGridItem>>(item.getPersonName(), new ArrayList<IGridItem>());
			
			pair.second.add(item);
			
			if(!pairs.contains(pair))
				pairs.add(pair);
		}
		
		List<GridItemRow> rows = new ArrayList<>();
		for(Pair<String, List<IGridItem>> pair : pairs)
		{
			GridItemRow gridRow = new GridItemRow(pair.first, new TimeRange(left, right), pair.second);
			rows.add(gridRow);
		}
		
		
		List<GridItem> allGridItems = new ArrayList<>();
		List<GuideYItem> itemsY = new ArrayList<>();
		for(GridItemRow r : rows)
		{
			List<GridItem> l = r.getItems();
			allGridItems.addAll(l);
			
			for(int i = 0; i < l.size() / columns; i++)
				itemsY.add(new GuideYItem(i == 0 ? r.getPersonName() : "")); // only write the name once.
		}
		
		if (gridAdapter == null)
		{
			gridAdapter = new FastItemAdapter<>();
			gridAdapter.setHasStableIds(true);
			gridAdapter.withSelectable(false);
			recyclerView.setAdapter(gridAdapter);
		}
		
		setGuideYItems(itemsY);
		gridAdapter.set(allGridItems);
		requestLayout();
		center();
	}
	
	private void construct(final int itemCount)
	{
		final RecyclerView.OnItemTouchListener itemTouchListener = new RecyclerView.OnItemTouchListener()
		{
			@Override
			public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
			{
				return true;
			}
			
			@Override
			public void onTouchEvent(RecyclerView rv, MotionEvent e)
			{
				// Do nothing.
			}
			
			@Override
			public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
			{
				
			}
		};
		guideX.setHasFixedSize(true);
		guideX.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		// Do not allow scrolling with the X or Y for now because we do not have a scrollToPositionWithOffset yet in our FixedGridLayout
		guideX.addOnItemTouchListener(itemTouchListener);
		
		guideY.setHasFixedSize(true);
		guideY.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		// Do not allow scrolling with the X or Y for now because we do not have a scrollToPositionWithOffset yet in our FixedGridLayout
		guideY.addOnItemTouchListener(itemTouchListener);
		
		observedList = new ArrayList<RecyclerView>()
		{{
			add(guideX);
			add(guideY);
		}};
		
		FixedGridLayoutManager mgr = new FixedGridLayoutManager();
		mgr.setTotalColumnCount(itemCount);
		recyclerView.setLayoutManager(mgr);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			int state;
			
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy)
			{
				super.onScrolled(recyclerView, dx, dy);
				
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
		
		// TODO
		/*guideY.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			int state;
			
			@Override
			public void onScrolled(RecyclerView rv, int dx, int dy)
			{
				super.onScrolled(recyclerView, dx, dy);
				if (state == RecyclerView.SCROLL_STATE_IDLE)
				{
					return;
				}
				
				final LinearLayoutManager managerY = (LinearLayoutManager) rv.getLayoutManager();
				final FixedGridLayoutManager layoutMgr = (FixedGridLayoutManager) recyclerView.getLayoutManager();
				
				final int firstRow = managerY.findFirstVisibleItemPosition();
				
				View firstVisibleItem = layoutMgr.getChildAt(0);
				if (firstVisibleItem != null)
				{
					int decoratedY = managerY.getDecoratedBottom(firstVisibleItem);
					layoutMgr.scroll((firstRow + 1) * columns);
				}
				
			}
			
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState)
			{
				super.onScrollStateChanged(recyclerView, newState);
				state = newState;
			}
		});*/
		
	}
	
	/**
	 * Initializes the horizontal guide which displays the days, dates.
	 * 
	 * @param left the time on the left end
	 * @param right the time on the right end
	 */
	public void setTimeRange(Calendar left, Calendar right)
	{
		if(left.getTimeInMillis() > right.getTimeInMillis())
		{
			Log.e(DTAG, "setTimeRange 'left' cannot be higher than 'right'.");
			return;
		}
		
		this.left = left;
		this.right = right;
		
		this.timeRange = new TimeRange(left, right);
	}
	
	public <T extends IGuideXItem> void setGuideXItems(List<T> items)
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
	
	public <T extends IGuideYItem> void setGuideYItems(List<T> items)
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
	
	public Calendar getTimeLeft()
	{
		return left;
	}
	
	public Calendar getTimeRight()
	{
		return right;
	}
	
	public void center()
	{
		if(recyclerView != null && gridAdapter != null && gridAdapter.getItemCount() > 0)
		{
			recyclerView.scrollToPosition(((gridAdapter.getItemCount() / columns) / 2));
		}
	}
	
	private static long calendarToMidnightMillis(Calendar calendar)
	{
		Calendar c = Calendar.getInstance();
		c.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,0,0);
		
		return calendar.getTimeInMillis();
	}
}
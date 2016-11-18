package com.greasemonk.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import com.greasemonk.timetable.rows.InitialsRow;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTable<T extends AbstractRowItem> extends FrameLayout implements SimplePagingDelegate
{
	private View view;
	private TextView title;
	private SwipingRecyclerView recyclerView;
	private List<T> items;
	private List<InitialsRow> rows;
	private FastItemAdapter<InitialsRow> adapter;
	private ProgressBar progressBar;
	private Calendar left = Calendar.getInstance();
	private Calendar right = Calendar.getInstance();
	private TextView[] textViews;
	private int columnCount;
	
	
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
				(TextView) view.findViewById(R.id.text7)};
		
		progressBar.setVisibility(GONE);
		
		
		
		
		// Initialize the list and make sure we can swipe to change pages.
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setDelegate(this);
		
		left.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		right.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		
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
		for (AbstractRowItem item : items)
		{
			// Left and Right are the TimeTable's date range
			boolean planStartsBeforeLeft = left.getTime().after(item.getPlanningStart());
			boolean planEndsAfterRight = right.getTime().before(item.getPlanningEnd());
			
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
					start = Math.round(TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS));
				}
				
				// Calculate the span of the SpannableBar
				if (planEndsAfterRight)
					span = columnCount - start;
				else
				{
					long difference = item.getPlanningEnd().getTime() - right.getTime().getTime();
					span = Math.round(TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS));
				}
			}
			
			// Do not add rows that display nothing.
			if (span > 0)
				rows.add(new InitialsRow(start, span, item));
		}
		
		// Sort by employee name
		Collections.sort(rows, InitialsRow.getComparator());
		String temp = null;
		for (InitialsRow row : rows)
		{
			if (temp == null || !temp.equals(row.getItem().getEmployeeName()))
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
		for (int i = 0; i < 7; i++)
		{
			textViews[i].setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
			calendar.add(Calendar.DATE, 1);
		}
		
		String titleText = "week " + calendar.get(Calendar.WEEK_OF_YEAR) + ", ";
		titleText += calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		titleText += " " + calendar.get(Calendar.YEAR);
		
		title.setText(titleText);
	}
	
	
	
}
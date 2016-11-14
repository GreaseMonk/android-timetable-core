package com.greasemonk.timetable.rows;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.greasemonk.spannablebar.SpannableBar;
import com.greasemonk.timetable.AbstractRowItem;
import com.greasemonk.timetable.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.greasemonk.timetable.TimeTable.TimePeriod;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class InitialsRow extends AbstractItem<InitialsRow, InitialsRow.ViewHolder> implements Comparable<InitialsRow>
{
	private static final int SPAN_MAX = 7;
	
	private AbstractRowItem item; // The stored item
	private Date tableDateStart, tableDateEnd; // The plan's start and end date
	private TimePeriod timePeriod; // The time period (day, week, month) that is displayed
	private boolean showInitials;
	
	public InitialsRow(@NonNull TimePeriod timePeriod, // The time period: day, week, month, etc
	                   @NonNull Date tableDateStart, // The date that is outmost left of the timetable header
	                   @NonNull Date tableDateEnd, // The date that is outmost right of the timetable header
	                   @NonNull AbstractRowItem item)
	{
		this.tableDateStart = tableDateStart;
		this.tableDateEnd = tableDateEnd;
		this.timePeriod = timePeriod;
	}
	
	@Override
	public int compareTo(@NonNull InitialsRow other)
	{
		if (item.getEmployeeName() == null)
			return 1;
		else if (other.item.getEmployeeName() == null)
			return -1;
		
		return item.getEmployeeName().compareTo(other.getItem().getEmployeeName());
	}
	
	/**
	 * The comparator, used to sort a list with Collections.sort()..
	 * By default it sorts on the given employee string from AbstractRowItem
	 * @return a comparator for InitialsRow
	 */
	public static Comparator<InitialsRow> getComparator()
	{
		return new Comparator<InitialsRow>()
		{
			@Override
			public int compare(InitialsRow o1, InitialsRow o2)
			{
				return o1.compareTo(o2);
			}
		};
	}
	
	@Override
	public int getType()
	{
		return R.id.timetable_initials_row_id;
	}
	
	@Override
	public int getLayoutRes()
	{
		return R.layout.timetable_initials_row;
	}
	
	//The logic dateRight bind your data dateRight the view
	@Override
	public void bindView(final ViewHolder viewHolder, List payloads)
	{
		super.bindView(viewHolder, payloads);
		//set the selected state of this item. force this otherwise it may is missed when implementing an item
		viewHolder.itemView.setSelected(isSelected());
		//set the tag of this item dateRight this object (can be used when retrieving the view)
		viewHolder.itemView.setTag(this);
		
		
		// If the user is named Roy Haroldsen , display "RH"
		String displayedInitials = "";
		if (item.getEmployeeName() != null && item.getEmployeeName().length() > 0)
		{
			String[] splitted = item.getEmployeeName().split(" ");
			for (String str : splitted)
			{
				if (str.length() > 0)
					displayedInitials += str.substring(0, 0).toUpperCase();
			}
			
			viewHolder.initials.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Toast.makeText(viewHolder.itemView.getContext(), item.getEmployeeName(), Toast.LENGTH_LONG).show();
				}
			});
		}
		viewHolder.initials.setText(displayedInitials);
		
		
		// Compare dates, and figure out where the bar starts and how many columns it should span
		int span = 0;
		int start = 0;
		
		if (tableDateStart.after(item.getPlanningStart()) && tableDateEnd.before(item.getPlanningEnd()))
			span = SPAN_MAX;
		else
		{
			switch (timePeriod)
			{
				case DAY:
					Calendar cal = Calendar.getInstance();
					cal.setTime(tableDateStart);
					Date left = cal.getTime();
					cal.add(Calendar.DATE, SPAN_MAX);
					Date right = cal.getTime();
					
					if (left.after(tableDateStart))
					{
						// Most left column is after project start date.
						if (right.before(tableDateEnd))
						{
							// Most right column is before project end date.
							// The project's planning fits within all columns.
							span = 7;
						}
						else
						{
							// Most right column is after project end date.
							// Go back a couple of days.
							long diff = right.getTime() - tableDateEnd.getTime();
							int days = Math.round(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
							days -= 1;
							if (days < 0)
								days = 0;
							span = SPAN_MAX - days;
						}
					}
					else
					{
						// Most left column is before project start date.
						// Go forward a couple of days
						long diff = tableDateStart.getTime() - left.getTime();
						int days = Math.round(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
						days += 1;
						start = days;
						
						if (right.before(tableDateEnd))
						{
							// Most right column is before project end date.
							span = SPAN_MAX - start;
						}
						else
						{
							// Most right column is after project end date.
							// Go back a couple of days.
							long diff2 = right.getTime() - tableDateEnd.getTime();
							int days2 = Math.round(TimeUnit.DAYS.convert(diff2, TimeUnit.MILLISECONDS));
							span -= days2;
						}
					}
					break;
				case WEEK:
					
					break;
				case MONTH:
					
					break;
			}
		}
		if (span < 0)
			span = 0;
		
		if (viewHolder.bar != null)
		{
			viewHolder.bar.set(start, span);
			
			if (span > 0)
				viewHolder.bar.setText(item.getProjectName());
			else
				viewHolder.bar.setText("");
		}
	}
	
	protected static class ViewHolder extends RecyclerView.ViewHolder
	{
		protected RelativeLayout view;
		protected TextView initials;//, projectName;
		protected SpannableBar bar;
		
		public ViewHolder(View view)
		{
			super(view);
			this.view = (RelativeLayout) view;
			this.initials = (TextView) view.findViewById(R.id.text1);
			this.bar = (SpannableBar) view.findViewById(R.id.bar);
			//this.projectName = (TextView) view.findViewById(R.id.text2);
		}
	}
	
	/**
	 * Sets the visibility of the left TextView
	 * @param visible the visibility value dateRight set (TRUE for visible)
	 */
	public void setInitialsVisibility(boolean visible)
	{
		this.showInitials = visible;
	}
	
	/**
	 * Get the item stored in this row
	 * @return the item stored in this row
	 */
	public AbstractRowItem getItem()
	{
		return item;
	}
}
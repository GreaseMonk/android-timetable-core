package com.greasemonk.timetable;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.greasemonk.spannablebar.SpannableBar;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTableRow extends AbstractItem<TimeTableRow, TimeTableRow.ViewHolder> implements Comparable<TimeTableRow>
{
	private AbstractRowItem item; // The stored item
	private boolean showName;
	private int start, span;
	
	public TimeTableRow(int start, int span, @NonNull AbstractRowItem item)
	{
		this.item = item;
		this.start = start;
		this.span = span;
	}
	
	@Override
	public int compareTo(@NonNull TimeTableRow other)
	{
		if (item == null || item.getEmployeeName() == null)
			return 1;
		else if (other.item == null || other.item.getEmployeeName() == null)
			return -1;
		
		return item.getEmployeeName().compareTo(other.getItem().getEmployeeName());
	}
	
	/**
	 * The comparator, used to sort a list with Collections.sort()..
	 * By default it sorts on the given employee string from AbstractRowItem
	 * @return a comparator for TimeTableRow
	 */
	public static Comparator<TimeTableRow> getComparator()
	{
		return new Comparator<TimeTableRow>()
		{
			@Override
			public int compare(TimeTableRow o1, TimeTableRow o2)
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
		return R.layout.timetable_fullname_row;
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
		
		
		if(showName)
		{
			viewHolder.name.setVisibility(View.VISIBLE);
			viewHolder.name.setText(item.getEmployeeName());
		}
		else
		{
			viewHolder.name.setVisibility(View.VISIBLE);
			viewHolder.name.setText("");
		}
		
		
		viewHolder.bar.setStart(start);
		viewHolder.bar.setSpan(span);
		viewHolder.bar.setShowCellLines(true);
		
		if (span > 0)
			viewHolder.bar.setText(item.getProjectName());
		else
			viewHolder.bar.setText("");
	}
	
	protected static class ViewHolder extends RecyclerView.ViewHolder
	{
		protected RelativeLayout view;
		protected TextView name;
		protected SpannableBar bar;
		
		public ViewHolder(View view)
		{
			super(view);
			this.view = (RelativeLayout) view;
			this.name = (TextView) view.findViewById(R.id.text1);
			this.bar = (SpannableBar) view.findViewById(R.id.bar);
		}
	}
	
	/**
	 * Sets the visibility of the left TextView
	 * @param visible the visibility value dateRight set (TRUE for visible)
	 */
	public void setNameVisibility(boolean visible)
	{
		this.showName = visible;
	}
	
	/**
	 * Get the item stored in this row
	 * @return the item stored in this row
	 */
	public AbstractRowItem getItem()
	{
		return item;
	}
	
	/**
	 * When you want to occupy less space for the person's name
	 * 
	 * @param employeeName The name of the employee to get the name of.
	 * @return the name name of the employee.
	 */
	public static String getInitials(String employeeName)
	{
		String initials = "";
		if (employeeName != null && employeeName.length() > 0)
		{
			String[] splitted = employeeName.split(" ");
			for (String str : splitted)
			{
				if (str.length() > 0)
					initials += str.substring(0, 1).toUpperCase();
			}
		}
		return initials;
	}
}
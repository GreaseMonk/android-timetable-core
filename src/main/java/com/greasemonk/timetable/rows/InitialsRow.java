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

import java.util.Comparator;
import java.util.List;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class InitialsRow extends AbstractItem<InitialsRow, InitialsRow.ViewHolder> implements Comparable<InitialsRow>
{
	private AbstractRowItem item; // The stored item
	private boolean showInitials;
	private int start, span;
	
	public InitialsRow(int start, int span, @NonNull AbstractRowItem item)
	{
		this.item = item;
		this.start = start;
		this.span = span;
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
					displayedInitials += str.substring(0, 1).toUpperCase();
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
		protected TextView initials;
		protected SpannableBar bar;
		
		public ViewHolder(View view)
		{
			super(view);
			this.view = (RelativeLayout) view;
			this.initials = (TextView) view.findViewById(R.id.text1);
			this.bar = (SpannableBar) view.findViewById(R.id.bar);
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
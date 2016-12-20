package com.greasemonk.timetable;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

/**
 * Created by Wiebe Geertsma on 12-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class GuideXItem extends AbstractItem<GuideXItem, GuideXItem.ViewHolder> implements IGuideXItem
{
	private DateTime time;
	private String displayedText = "";
	
	public GuideXItem(DateTime time)
	{
		this.time = time;
		displayedText = getDateString();
	}
	
	public DateTime getDateTime()
	{
		//boolean sameDay = time.dayOfYear().get() == other.dayOfYear().get();
		//boolean sameYear = time.year().get() == other.year().get();
		
		return time;
	}
	
	@Override
	public String getDateString()
	{
		String retVal = "";
		retVal += Integer.toString(time.dayOfMonth().get());
		retVal += "-";
		retVal += Integer.toString(time.monthOfYear().get());
		return retVal;
	}
	
	@Override
	public void bindView(ViewHolder holder, List payloads)
	{
		super.bindView(holder, payloads);
		
		holder.name.setText(displayedText);
	}
	
	@Override
	public String getDayString()
	{
		return time.dayOfWeek().getAsText(Locale.getDefault());
	}
	
	@Override
	public int getType()
	{
		return R.id.timetable_guide_x_item;
	}
	
	@Override
	public long getIdentifier()
	{
		return System.identityHashCode(this);
	}
	
	@Override
	public int getLayoutRes()
	{
		return R.layout.item_2;
	}
	
	protected static class ViewHolder extends RecyclerView.ViewHolder
	{
		protected TextView name;
		
		public ViewHolder(View view)
		{
			super(view);
			this.name = (TextView) view.findViewById(R.id.text1);
		}
	}
}

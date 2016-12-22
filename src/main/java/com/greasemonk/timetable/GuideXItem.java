package com.greasemonk.timetable;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wiebe Geertsma on 12-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class GuideXItem extends AbstractItem<GuideXItem, GuideXItem.ViewHolder> implements IGuideXItem
{
	private Calendar time;
	private String date = "";
	private String day = "";
	
	public GuideXItem(Calendar time)
	{
		this.time = Calendar.getInstance();
		this.time.setTimeInMillis(time.getTimeInMillis());
		date = getDateString();
		day = getDayString();
	}
	
	public Calendar getDateTime()
	{
		//boolean sameDay = time.dayOfYear().get() == other.dayOfYear().get();
		//boolean sameYear = time.year().get() == other.year().get();
		
		return time;
	}
	
	@Override
	public String getDateString()
	{
		String retVal = "";
		retVal += Integer.toString(time.get(Calendar.DAY_OF_MONTH));
		retVal += "-";
		retVal += Integer.toString(time.get(Calendar.MONTH) + 1);
		return retVal;
	}
	
	@Override
	public void bindView(ViewHolder holder, List payloads)
	{
		super.bindView(holder, payloads);
		
		holder.date.setText(date);
		holder.day.setText(day);
		
		Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), isToday() ? R.drawable.item_today_bg : R.drawable.item_bg).mutate();
		Drawable wrapDrawable = DrawableCompat.wrap(drawable);
		holder.itemView.setBackground(wrapDrawable);
	}
	
	@Override
	public String getDayString()
	{
		return time.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
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
		return R.layout.item_1;
	}
	
	protected static class ViewHolder extends RecyclerView.ViewHolder
	{
		protected TextView date;
		protected TextView day;
		
		public ViewHolder(View view)
		{
			super(view);
			this.date = (TextView) view.findViewById(R.id.text1);
			this.day = (TextView) view.findViewById(R.id.text2);
		}
	}
	
	private boolean isToday()
	{
		Calendar now = Calendar.getInstance();
		if(time.get(Calendar.YEAR) != now.get(Calendar.YEAR))
			return false;
		if(time.get(Calendar.MONTH) != now.get(Calendar.MONTH))
			return false;
		if(time.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH))
			return false;
		
		return true;
	}
}

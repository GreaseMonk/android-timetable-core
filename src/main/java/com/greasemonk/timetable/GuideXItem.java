package com.greasemonk.timetable;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
	private final String text;
	
	public GuideXItem(Calendar time)
	{
		this.time = Calendar.getInstance();
		this.time.setTimeInMillis(time.getTimeInMillis());
		String weekNumber = (this.time.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? "Wk." + Integer.toString(this.time.get(Calendar.WEEK_OF_YEAR)) : "") + "\n";
		String date = getDateString() + "\n";
		String day = getDayString();
		text = (time.get(Calendar.WEEK_OF_YEAR) == 1 && time.get(Calendar.DAY_OF_MONTH) == 1 ? Integer.toString(time.get(Calendar.YEAR)) + "\n" : weekNumber) + date + day;
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
		
		holder.date.setText(text);
		holder.date.setTypeface(null, Typeface.BOLD);
		
		Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), isToday() ? R.drawable.item_today_bg : R.drawable.item_guide_bg).mutate();
		Drawable wrapDrawable = DrawableCompat.wrap(drawable);
		DrawableCompat.setTint(wrapDrawable, Color.WHITE);
		DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.OVERLAY);
		holder.itemView.setBackground(wrapDrawable);
	}
	
	@Override
	public String getDayString()
	{
		return time.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toUpperCase();
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
		return R.layout.item_guide_x;
	}
	
	protected static class ViewHolder extends RecyclerView.ViewHolder
	{
		protected TextView date;
		
		public ViewHolder(View view)
		{
			super(view);
			this.date = (TextView) view.findViewById(R.id.text1);
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

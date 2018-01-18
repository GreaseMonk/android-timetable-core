package com.greasemonk.timetable;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by Wiebe Geertsma on 12-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class GuideYItem extends AbstractItem<GuideYItem, GuideYItem.ViewHolder> implements IGuideYItem
{
	private final String name;
	
	public GuideYItem(String name)
	{
		this.name = name;
	}
	
	@Override
	public void bindView(ViewHolder holder, List payloads)
	{
		super.bindView(holder, payloads);
		
		holder.name.setText(getName());
		holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.item_guide_bg));
	}
	
	@Override
	public int getType()
	{
		return R.id.timetable_guide_y_item;
	}
	
	@Override
	public long getIdentifier()
	{
		return System.identityHashCode(this);
	}
	
	@Override
	public ViewHolder getViewHolder(View v)
	{
		return new ViewHolder(v);
	}
	
	@Override
	public int getLayoutRes()
	{
		return R.layout.item_guide_y;
	}
	
	@Override
	public String getName()
	{
		return name;
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
package com.greasemonk.timetable;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.greasemonk.timetable.FixedGridLayoutManager.LayoutParams;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by Wiebe Geertsma on 8-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTableItem extends AbstractItem<TimeTableItem, TimeTableItem.ViewHolder>
{
	private final IGridItem item;
	private final int row, column;
	
	public TimeTableItem(int row, int column)
	{
		// Make a blank item
		item = null;
		this.row = row;
		this.column = column;
	}
	
	public TimeTableItem(IGridItem item, int row, int column)
	{
		this.item = item;
		this.row = row;
		this.column = column;
	}
	
	@Override
	public int getType()
	{
		return R.id.timetable_item;
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
	
	@Override
	public void bindView(TimeTableItem.ViewHolder holder, List payloads)
	{
		super.bindView(holder, payloads);
		if(holder.itemView.getLayoutParams() != null)
		{
			LayoutParams params = new LayoutParams(holder.itemView.getLayoutParams());
			params.column = column;
			params.row = row;
			holder.itemView.setLayoutParams(params);
		}
		if(item != null)
		{
			holder.textView.setText(item.getName());
			holder.textView.setBackgroundColor(Color.argb(25, 0,0,255));
		}
		else
		{
			holder.textView.setText("");
			holder.textView.setBackgroundResource(R.drawable.item_bg);
		}
	}
	
	protected static class ViewHolder extends RecyclerView.ViewHolder
	{
		protected TextView textView;
		
		public ViewHolder(View view)
		{
			super(view);
			this.textView = (TextView) view.findViewById(R.id.text1);
		}
	}
	
	public IGridItem getItem()
	{
		return item;
	}
}

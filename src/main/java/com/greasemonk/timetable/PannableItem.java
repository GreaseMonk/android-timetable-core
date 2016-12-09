package com.greasemonk.timetable;

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
public class PannableItem extends AbstractItem<PannableItem, PannableItem.ViewHolder>
{
	private int row, column;
	
	public PannableItem()
	{
		
	}
	
	public PannableItem(int row, int column)
	{
		this.row = row;
		this.column = column;
	}
	
	@Override
	public int getType()
	{
		return 0;
	}
	
	@Override
	public int getLayoutRes()
	{
		return R.layout.test_item_layout;
	}
	
	@Override
	public void bindView(PannableItem.ViewHolder holder, List payloads)
	{
		super.bindView(holder, payloads);
		if(holder.itemView.getLayoutParams() != null && !(row == 0 && column == 0))
		{
			LayoutParams params = new LayoutParams(holder.itemView.getLayoutParams());
			params.column = column;
			params.row = row;
			holder.itemView.setLayoutParams(params);
		}
		holder.textView.setText(row + "," + column);
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
}

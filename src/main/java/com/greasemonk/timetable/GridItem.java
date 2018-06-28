package com.greasemonk.timetable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
public class GridItem extends AbstractItem<GridItem, GridItem.ViewHolder>
{
	private final IGridItem model;
	private final int row;
	private final int column;
	private boolean isStart, isToday, isWeekend;
	
	public GridItem(int row, int column)
	{
		// Make a blank item
		model = null;
		this.row = row;
		this.column = column;
	}
	
	public <T extends IGridItem> GridItem(T model, int row, int column)
	{
		this.model = model;
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
	public ViewHolder getViewHolder(View v)
	{
		return new ViewHolder(v);
	}
	
	@Override
	public int getLayoutRes()
	{
		return R.layout.item_grid;
	}
	
	@Override
	public void bindView(GridItem.ViewHolder holder, List payloads)
	{
		super.bindView(holder, payloads);
		
		if (holder.itemView.getLayoutParams() != null)
		{
			LayoutParams params = new LayoutParams(holder.itemView.getLayoutParams());
			params.column = column;
			params.row = row;
			holder.itemView.setLayoutParams(params);
		}
		
		holder.textView.setText(model != null && isStart ? model.getName() : "");
		
		/*if (model != null)
		{
			int resolvedColor = model.getItemColor() != -1 ? model.getItemColor() : holder.itemView.getResources().getColor(R.color.today_color);
			
			holder.itemView.setBackground(
					getTintedDrawable(
							holder.itemView.getContext(),
							holder.itemView.getResources().getDrawable(isToday ? R.drawable.item_today_bg : R.drawable.item_bg),
							isToday ? 0 : resolvedColor));
		}
		else
			*/

		Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), isToday ? R.drawable.item_today_bg : isWeekend ? R.drawable.item_weekend_bg : R.drawable.item_bg).mutate();
		Drawable wrapDrawable = DrawableCompat.wrap(drawable);
		
		if(model != null)
		{
		    DrawableCompat.setTint(wrapDrawable, model.getItemColor());
		    DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.OVERLAY);
			holder.itemView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					model.onClick(view);
				}
			});
		}
		holder.itemView.setBackground(wrapDrawable);
		
		
	}
	
	public boolean isStart()
	{
		return isStart;
	}
	
	public void setStart(boolean start)
	{
		isStart = start;
	}
	
	public boolean isToday()
	{
		return isToday;
	}
	
	public void setIsToday(boolean today)
	{
		isToday = today;
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
	
	public boolean getIsWeekend()
	{
		return isWeekend;
	}
	
	public void setIsWeekend(boolean weekend)
	{
		isWeekend = weekend;
	}
	
	public IGridItem getModel()
	{
		return model;
	}
	
	public int getRow()
	{
		return row;
	}
	
	public int getColumn()
	{
		return column;
	}
	
	public boolean isEmpty()
	{
		return model == null;
	}
}

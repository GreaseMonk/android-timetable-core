package com.greasemonk.timetable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by Wiebe Geertsma on 21-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public abstract class AbstractGridItem<T extends AbstractGridItem<?,?>, VH extends RecyclerView.ViewHolder> implements IGridItem
{
	private int itemColor = -1;
	
	/**
	 * If the TimeRange is null, your item will NOT be displayed.
	 *
	 * @return the time range of this item
	 */
	@Nullable
	public abstract TimeRange getTimeRange();
	
	/**
	 * Get the text that is displayed in the tile.
	 *
	 * @return the text that is displayed in the tile
	 */
	@Override
	public abstract String getName();
	
	/**
	 * Get the text that is displayed on the Y axis.
	 *
	 * @return the text that is displayed on the Y axis
	 */
	@Override
	public abstract String getPersonName();
	
	/**
	 * Get the item color
	 *
	 * @return the color of the item.
	 */
	@Override
	public int getItemColor()
	{
		return itemColor;
	}
	
	/**
	 * Set the item color.
	 *
	 * @param itemColor the color to set the item to.
	 */
	public void setItemColor(int itemColor)
	{
		this.itemColor = itemColor;
	}
	
	/**
	 * Executed when the user clicked on the item.
	 */
	public void onClick()
	{
		// Do nothing. Override me! 
	}
}

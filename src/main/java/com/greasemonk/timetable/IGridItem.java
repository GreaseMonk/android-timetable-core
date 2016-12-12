package com.greasemonk.timetable;

import java.util.Date;

/**
 * Created by Wiebe Geertsma on 12-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public interface IGridItem
{
	Date getStartDate();
	Date getEndDate();
	
	/**
	 * Get the text that is displayed in the tile.
	 * 
	 * @return the text that is displayed in the tile
	 */
	String getName();
	
	/**
	 * Get the text that is displayed on the Y axis.
	 * 
	 * @return the text that is displayed on the Y axis
	 */
	String getPersonName();
}

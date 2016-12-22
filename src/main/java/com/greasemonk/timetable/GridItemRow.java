package com.greasemonk.timetable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Wiebe Geertsma on 13-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class GridItemRow<T extends IGridItem>
{
	private final TimeRange timeRange;
	private final String personName;
	private final List<GridItem> items;
	
	public GridItemRow(final String personName, final TimeRange timeRange, final List<T> containedItems)
	{
		this.timeRange = timeRange; // We need to keep track of the time range of this row so we can display the current day
		this.personName = personName;
		items = generateGridItems(fitItems(containedItems), timeRange);
	}
	
	/**
	 * Here we get the final items for display.
	 * We determine if we need one or two rows.
	 * If we need two rows for this person, this function returns a total of two rows of items.
	 *
	 * @return all items for all required rows for this person.
	 */
	public List<GridItem> getItems()
	{
		return items;
	}
	
	/**
	 * Convert a list of potentially overlapping items into a list of lists containing IGridItems that don't overlap.
	 *
	 * @param list the unsorted list of IGridItems
	 * @return the list of
	 */
	private static <T extends IGridItem> List<List<T>> fitItems(List<T> list)
	{
		List<List<T>> sortedList = new ArrayList<>();
		sortedList.add(new ArrayList<T>()); // Add the initial item
		
		// Cycle until there are no more items left
		for (T item : list)
		{
			boolean wasAdded = true;
			for (List<T> currentRowList : sortedList)
			{
				boolean fitsInCurrentRow = true;
				for (IGridItem rowItem : currentRowList) // Check if there are overlapping items in this row
				{
					if (item.getTimeRange() != null &&
							rowItem.getTimeRange() != null &&
							item.getTimeRange().overlaps(rowItem.getTimeRange()))
					{
						fitsInCurrentRow = false;
						break;
					}
				}
				
				if (fitsInCurrentRow)
					currentRowList.add(item);
				else
					wasAdded = false;
			}
			if (!wasAdded)
			{
				List<T> newList = new ArrayList<>();
				newList.add(item);
				sortedList.add(newList);
			}
		}
		
		return sortedList;
	}
	
	/**
	 * Generate the cells. The items list should have already been sorted.
	 * The items in every list of itemsList should never overlap, or otherwise it will only display the first
	 * item it finds that corresponds to the time of the cell.
	 *
	 * @param itemsList the list containing items for each row.
	 * @param timeRange the time range (start to end) of this row.
	 * @return the generated list of GridItems ready to display in the RecyclerView.
	 */
	private static <T extends IGridItem> List<GridItem> generateGridItems(final List<List<T>> itemsList, final TimeRange timeRange)
	{
		final int columns = timeRange.getColumnCount();
		List<GridItem> gridItems = new ArrayList<>();
		
		for (int y = 0; y < itemsList.size(); y++)
		{
			Calendar cellTime = Calendar.getInstance();
			cellTime.setTimeInMillis(timeRange.getStart().getTimeInMillis());
			cellTime.add(Calendar.DATE, 1);
			
			for (int x = 0; x < columns; x++)
			{
				GridItem gridItem = null;
				for (T item : itemsList.get(y))
				{
					if (item.getTimeRange() == null)
						continue; // Skip any items that have null start or end date.
					if (item.getTimeRange().isWithin(cellTime))
					{
						gridItem = new GridItem(item, x, y);
						break;
					}
				}
				if (gridItem == null)
					gridItem = new GridItem(x, y);
				else if (!gridItems.isEmpty() && gridItems.size() > 0)
				{
					GridItem lastItem = gridItems.get((y * columns) + x - 1);
					gridItem.setStart(lastItem.isEmpty() || !gridItem.getModel().equals(lastItem.getModel()));
				}
				if (compareDates(cellTime, Calendar.getInstance()))
				{
					gridItem.setIsToday(true);
				}
				
				gridItems.add(gridItem);
				cellTime.add(Calendar.DATE, 1);
			}
		}
		
		return gridItems;
	}
	
	public String getPersonName()
	{
		return personName;
	}
	
	/**
	 * Compare two dates, and check if they are the same.
	 * Only checks year, month, day.
	 * 
	 * @return TRUE if the dates are the same.
	 */
	public static boolean compareDates(Calendar left, Calendar right)
	{
		if(left.get(Calendar.YEAR) != right.get(Calendar.YEAR))
			return false;
		if(left.get(Calendar.MONTH) != right.get(Calendar.MONTH))
			return false;
		if(left.get(Calendar.DAY_OF_MONTH) != right.get(Calendar.DAY_OF_MONTH))
			return false;
		
		return true;
	}
}

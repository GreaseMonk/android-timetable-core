package com.greasemonk.timetable;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wiebe Geertsma on 13-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class GridItemRow
{
	private final TimeRange timeRange;
	private final String personName;
	private final List<GridItem> items;
	
	public GridItemRow(final String personName, final TimeRange timeRange, final List<IGridItem> containedItems)
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
	private static List<List<IGridItem>> fitItems(List<IGridItem> list)
	{
		List<List<IGridItem>> sortedList = new ArrayList<>();
		sortedList.add(new ArrayList<IGridItem>()); // Add the initial item
		
		// Cycle until there are no more items left
		for (IGridItem item : list)
		{
			boolean wasAdded = true;
			for (List<IGridItem> currentRowList : sortedList)
			{
				boolean fitsInCurrentRow = true;
				for (IGridItem rowItem : currentRowList) // Check if there are overlapping items in this row
				{
					if (item.getTimeRange().overlaps(rowItem.getTimeRange()))
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
				List<IGridItem> newList = new ArrayList<>();
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
	private static List<GridItem> generateGridItems(final List<List<IGridItem>> itemsList, final TimeRange timeRange)
	{
		final int columns = timeRange.getColumnCount();
		List<GridItem> gridItems = new ArrayList<>();
		
		for (int y = 0; y < itemsList.size(); y++)
		{
			DateTime cellTime = timeRange.getStart().dayOfYear().addToCopy(1).millisOfDay().setCopy(0);
			
			for (int x = 0; x < columns; x++)
			{
				GridItem gridItem = null;
				for (IGridItem item : itemsList.get(y))
				{
					if(item.getTimeRange() == null)
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
					gridItem.setStart(lastItem.isEmpty() || !gridItem.getItem().equals(lastItem.getItem()));
				}
				
				gridItems.add(gridItem);
				cellTime = cellTime.dayOfYear().addToCopy(1);
			}
		}
		
		return gridItems;
	}
	
	public String getPersonName()
	{
		return personName;
	}
}

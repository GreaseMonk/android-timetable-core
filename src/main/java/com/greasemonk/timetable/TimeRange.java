package com.greasemonk.timetable;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by Wiebe Geertsma on 19-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeRange
{
	private DateTime start, end;
	
	public TimeRange(DateTime start, DateTime end)
	{
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Check if the time ranges overlap
	 * @see <a href="http://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap">http://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap</a>
	 * 
	 * @param other the other time range to check with
	 * @return TRUE if overlapping
	 */
	public final boolean overlaps(TimeRange other)
	{
		return start.getMillis() <= other.getEnd().getMillis() && end.getMillis() >= other.getStart().getMillis();
	}
	
	public final boolean isWithin(DateTime time)
	{
		return time.getMillis() >= start.millisOfDay().setCopy(0).getMillis() && time.getMillis() <= end.millisOfDay().setCopy(0).getMillis();
	}
	
	public final int getColumnCount()
	{
		return Days.daysBetween(start, end).getDays();
	}
	
	public DateTime getStart()
	{
		return start;
	}
	
	public void setStart(DateTime start)
	{
		this.start = start;
	}
	
	public DateTime getEnd()
	{
		return end;
	}
	
	public void setEnd(DateTime end)
	{
		this.end = end;
	}
}

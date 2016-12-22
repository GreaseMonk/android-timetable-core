package com.greasemonk.timetable;

import java.util.Calendar;
import java.util.Date;

/**
 * Class to hold two simple dates so all the time handling can be done here
 * 
 * Created by Wiebe Geertsma on 19-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeRange
{
	private Calendar start, end;
	
	public TimeRange(Calendar start, Calendar end)
	{
		this.start = Calendar.getInstance();
		this.end = Calendar.getInstance();
		this.start.setTimeInMillis(start.getTimeInMillis());
		this.end.setTimeInMillis(end.getTimeInMillis());
	}
	
	public TimeRange(Date start, Date end)
	{
		this.start = Calendar.getInstance();
		this.start.setTime(start);
		this.end = Calendar.getInstance();
		this.end.setTime(end);
	}
	
	public TimeRange(long millisStart, long millisEnd)
	{
		this.start = Calendar.getInstance();
		this.start.setTimeInMillis(millisStart);
		this.end = Calendar.getInstance();
		this.end.setTimeInMillis(millisEnd);
	}
	
	/**
	 * Check if the time ranges overlap
	 * 
	 * @param other the other time range to check with
	 * @return TRUE if overlapping
	 */
	public final boolean overlaps(TimeRange other)
	{
		return start.getTimeInMillis() <= other.getEnd().getTimeInMillis() && end.getTimeInMillis() >= other.getStart().getTimeInMillis();
	}
	
	public final boolean isWithin(Calendar time)
	{
		
		return time.getTimeInMillis() >= start.getTimeInMillis() && time.getTimeInMillis() <= end.getTimeInMillis();
	}
	
	public final int getColumnCount()
	{
		Calendar current = Calendar.getInstance();
		current.setTimeInMillis(start.getTimeInMillis());
		int days = 0;
		while(current.getTimeInMillis() < end.getTimeInMillis())
		{
			current.add(Calendar.DATE, 1);
			days++;
		}
		return days;
	}
	
	public Calendar getStart()
	{
		return start;
	}
	
	public void setStart(Calendar start)
	{
		this.start = start;
	}
	
	public Calendar getEnd()
	{
		return end;
	}
	
	public void setEnd(Calendar end)
	{
		this.end = end;
	}
}

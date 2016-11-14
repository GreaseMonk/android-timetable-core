package com.greasemonk.timetable;

import java.util.Date;

/**
 * Created by Wiebe Geertsma on 14-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public abstract interface AbstractRowItem
{
	/**
	 * Get the start date of this employee's project planning
	 * @return The start date of this employee's project planning
	 */
	Date getPlanningStart();
	
	/**
	 * Get the end date of this employee's project planning
	 * @return The end date of this employee's project planning
	 */
	Date getPlanningEnd();
	
	/**
	 * Get the stored employee name
	 * @return the stored employee name
	 */
	String getEmployeeName();
	
	/**
	 * Get the stored project name
	 * @return the stored project name
	 */
	String getProjectName();
}

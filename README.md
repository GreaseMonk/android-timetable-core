[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable) [![API](https://img.shields.io/badge/API-16%2B-yellow.svg?style=flat)](https://android-arsenal.com/api?level=16)
android-timetable-core
===================

##`Two-dimensional support is coming. Watch the repo to stay up to date about this, or check back later.`

A timetable designed for planning employees to projects.

For example, in the construction sector, planners use a time table program at the office to sort out who works where out on the worksite, and with this library the workers can see it on the phone. The date (day/week/month) are on the horizontal axis, and the vertical axis lists the projects per employee.

![current result example](https://github.com/GreaseMonk/android-timetable-core/blob/develop/images/device-2016-11-30-145927.png) 


# Installation

Include the following in your build.gradle as a dependency:

```gradle
dependencies {
  compile 'com.github.greasemonk:timetable:1.0.8'
}
```

If this fails, make sure to check if you have synchronized your local repositories.

In IntelliJ or Android Studio, you can find this under Settings>Build,Execution,Deployment>Build Tools>Maven>Repositories.


# Usage

1. Include the layout in your XML:

```xml
<com.greasemonk.timetable.TimeTable android:id="@+id/time_table"
                                        android:layout_width="match_parent"
                                        android:layout_height="wrap_content"/>
```

2. Implement your class with AbstractRowItem

```java
public class EmployeePlanItem implements AbstractRowItem
{
	private String employeeName, projectName;
	private Date planStart, planEnd;
  
  ...
  
  @Override
	public Date getPlanningStart(){ return planStart; }
	
	@Override
	public Date getPlanningEnd(){ return planEnd; }
	
	@Override
	public String getEmployeeName(){  return employeeName; }
	
	@Override
	public String getProjectName(){ return projectName; }
```

3. Fill the table with data:

```java
timeTable = (TimeTable) findViewById(R.id.time_table);
timeTable.setColumnCount(7);
timeTable.update( getPlanData() );
```

Optional XML attributes:

```xml
<com.greasemonk.timetable.TimeTable
...
app:calTitlesColor="@color:..."		// Set the color for day numbers and day titles
app:calNowTitlesColor="@color:..."	// Set the color for the current day/week/month number and day title
/>
```

Optional functions:

```java
// ie. Color.argb(255,255,0,0) or Color.RED
timetable.setTitlesColor(int color) 	// Set the color for day numbers and day titles
timetable.setNowTitlesColor(int color)	// Set the color for the current day/week/month number and day title
```


## Dependencies

[FastAdapter](https://github.com/mikepenz/fastadapter) by Mike Penz. Used to display the rows.


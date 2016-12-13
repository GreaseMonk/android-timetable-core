[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable) [![API](https://img.shields.io/badge/API-16%2B-yellow.svg?style=flat)](https://android-arsenal.com/api?level=16)
android-timetable-core
===================

A timetable designed for planning employees to projects.

For example, in the construction sector, planners use a time table program at the office to sort out who works where out on the worksite, and with this library the workers can see it on the phone. The date (day/week/month) are on the horizontal axis, and the vertical axis lists the projects per employee.

![Demo gif](https://github.com/GreaseMonk/android-timetable-core/blob/develop/images/giphy_1.gif) 


# Installation

Include the following in your build.gradle as a dependency:

```gradle
// Check back soon, will update to maven in near future
```

If this fails, make sure to check if you have synchronized your local repositories.

In IntelliJ or Android Studio, you can find this under Settings>Build,Execution,Deployment>Build Tools>Maven>Repositories.


# Usage

###1. Include the layout in your XML

```xml
<com.greasemonk.timetable.TimeTable android:id="@+id/time_table"
                                        android:layout_width="match_parent"
                                        android:layout_height="wrap_content"/>
```

###2. Implement your class with IGridItem

```java
public class EmployeePlanItem implements IGridItem
{
	private String employeeName, projectName;
	private Date planStart, planEnd;
	
	public EmployeePlanItem() {}
	
	public EmployeePlanItem(String employeeName, String projectName, Date planStart, Date planEnd)
	{
		this.employeeName = employeeName;
		this.projectName = projectName;
		this.planStart = planStart;
		this.planEnd = planEnd;
	}
	
	@Override
	public Date getStartDate()
	{
		return planStart;
	}
	
	@Override
	public Date getEndDate()
	{
		return planEnd;
	}
	
	@Override
	public String getName()
	{
		return projectName;
	}
	
	@Override
	public String getPersonName()
	{
		return employeeName;
	}
}
```

###3. Fill the table with data

```java
timeTable = (TimeTable) findViewById(R.id.time_table);
timeTable.setItems(generateSamplePlanData());
```

## Dependencies

[FastAdapter](https://github.com/mikepenz/fastadapter) by Mike Penz. Used to display the rows.


## License

```
		Copyright 2016 Positive Computers

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
```

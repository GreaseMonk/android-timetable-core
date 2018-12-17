[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.greasemonk/timetable) [![API](https://img.shields.io/badge/API-16%2B-yellow.svg?style=flat)](https://android-arsenal.com/api?level=16)
android-timetable-core
===================

A timetable designed for planning employees to projects.

[Click here to download the demo APK](https://github.com/GreaseMonk/android-timetable-core/blob/develop/apk/20-12-2016_app-debug-v1.0.1-DEBUG-c101.apk)  
![Demo gif](https://github.com/GreaseMonk/android-timetable-core/blob/develop/images/giphy_1.gif) 

# !
# !
# !
# REPLACEMENT/DEPRECATION NOTICE
Due to a repository name change, the kotlin version has been moved [HERE](https://github.com/GreaseMonk/android-timetable).
This is a very different approach to the timetable code, which is more open to the modifications that are required.
This repository is no longer being maintained.
# !
# !
# !
.
.
.
.
.
.
.
.
.
.

# Contribute
This timetable needs to have modifications to the layout manager code that makes it possible to span cells over multiple columns.
How do you contribute ?
- Fork the repository
- Coffee
- Code
- Commit
- Send me your pull request
- Commits are reviewed before approval

Requests and tips are welcome, please open an issue for your questions.

# Installation

(Please check the versions to make sure you have the latest, updating maven could take a bit)
Include the following in your build.gradle as a dependency:

```gradle
dependencies {
	compile 'com.github.greasemonk:timetable:1.2.0'
}
```

If this fails, make sure to check if you have synchronized your local repositories.

In IntelliJ or Android Studio, you can find this under Settings>Build,Execution,Deployment>Build Tools>Maven>Repositories.


## Benefits

- You only need the name of the plan, the person's name, and a start & end date.
- No need for multi-dimensional arrays
- No need to calculate the X,Y / row,colum
- Pannable in X and Y
- Optimized for best performance
- Multiple items are merged in a single row.


# Usage

###1. Include the layout in your XML

```xml
<com.greasemonk.timetable.TimeTable android:id="@+id/time_table"
                                        android:layout_width="match_parent"
                                        android:layout_height="wrap_content"/>
```


###2. Implement your class with IGridItem (or extend [AbstractGridItem](https://github.com/GreaseMonk/android-timetable/blob/develop/app/src/main/java/com/greasemonk/timetable/app/EmployeePlanItem.java) )

Refer to the [Demo Activity](https://github.com/GreaseMonk/android-timetable/blob/develop/app/src/main/java/com/greasemonk/timetable/app/MainActivity.java) and [Demo PlanItem class](https://github.com/GreaseMonk/android-timetable/blob/develop/app/src/main/java/com/greasemonk/timetable/app/EmployeePlanItem.java) for detailed instructions.


```java
public class EmployeePlanItem implements IGridItem
{
	private String employeeName, projectName;
	private TimeRange timeRange;
	
	public EmployeePlanItem() {}
	
	public EmployeePlanItem(String employeeName, String projectName, Date planStart, Date planEnd)
	{
		this.employeeName = employeeName;
		this.projectName = projectName;
		this.timeRange = new TimeRange(planStart, planEnd);
	}
	
	@Override
	public TimeRange getTimeRange()
	{
		return timeRange;
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

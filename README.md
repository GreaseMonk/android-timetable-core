android-timetable-core
===================

Main library for a timetable designed for planning employees to projects.


## Goal

The goal is to create a time table for employees that are planned to projects.

In the construction sector, planners use a time table program at the office to sort out who works where out on the worksite, and with this library the workers can see it on the phone.

The date (day/week/month) are on the horizontal axis, and the vertical axis lists the projects per employee.
It has to be readable, and accurate to the day.

Here is a work in progress from 16-11-2016, which displays an employee's initials (but when tapped, shows the full name) and the project they are planned on on which days.

![current result example](https://github.com/GreaseMonk/android-timetable-core/blob/master/images/device-2016-11-16-160822.png) 
![YouTube video sample](https://www.youtube.com/watch?v=Jau9FQB9HyA)

Take note that it is not important to un-clutter the long list that may appear. Workers often only get to see the time table they are assigned to, sometimes with the colleagues in his team.


## The current progress

There's a column view that can create a 'bar' that spans over the columns as one object, making it very easy to modify in terms of shading and texturing. 

I've used this along with FastAdapter to create the rows with relative ease (the rows are subject for styling overhaul).

Furthermore the date programming and day calculation is done. Currently, it generates random employees and puts them on one of the 3-4 projects or so which are planned anywhere between 30 days ago from today, lasting 0-30 days for a demo view.


## Connected Repositories

There are submodules in this repository, so that changes can be made quickly without any releases for the time being.

[SpannableBar](https://github.com/GreaseMonk/SpannableBar) - A bar that has columns, start index, and span, and can easily be styled.

[TimeTable](https://github.com/GreaseMonk/android-timetable) - Demo project that will be released on the google play store.


## Usage

To-do

Push to maven central is due when primary goals are achieved, check back later.


## Dependencies

[FastAdapter](https://github.com/mikepenz/fastadapter) by Mike Penz. Used to display the rows.

v7 Recyclerview by the Android open source project.


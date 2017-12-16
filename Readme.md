## The Library Simulator 

This is a project for the practical work of the Operating system 
module in The University of Ibn Khaldoun - Algeria in which 
we had used the notion of threads and paralellism to make a library simulator.
this library simulator will simulate the behaviors of Readers : 
student and professors. and their communication to take a book,
grab a sit , block when ever there is not a book, or a sit,
prioritize the Professor then student in getting book's and so on.

## Code Style

The Code was Written In Pure Java.
The synchronisation has been made using semaphores, we had code the GUI
with the JavaFX library, in which there are a lot of classes to make a simple
or a complex GUI, for animating things in the scene we used also the packages
of JavaFX : javafx.animation, with this package it is very easy to animate things
around the scene in a complex and a simple way, for the static information
like the images and the coordinator of the points and books we used simple final 
classes and it is commented everywhere.

## Goal From This Project

First of all , the goal was to get knowldge how to use Threading in java and how
to manage thread by using semaphores and difrent kind of Mutual Exclusion to 
protect Critical sections, such as the books in the library, one reader can access 
to a book, when the other want to access the same book, they wel blocked until the current
reader will notify them when he will go out about the book, the places are Critical section,
the table, the chairs and so on.

Also By this project we can say that we had find a solution of managing robots in real life
,we can imagine that we have robots to to diffrent works in the same area, so this robots 
must know the situation of other robots to make a dicision and do an action in the environement
if the case we are in fully observable Enviroment, really it is intersting to manage robots by 
semaphores, so the semaphores are really great tools to managin real probel in the world of paralelism
not only for thread, for this example, it is also for robots, or in generale for anything 
that can excute simultanly.
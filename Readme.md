## The Library Simulator 

![alt text](https://github.com/Younes-Charfaoui/Library-Simulator/blob/master/src/res/Images/Things/Library.JPG)

This is a project for the practical work of the Operating system
module in The University of Ibn Khaldoun - Algeria in which
we had used the notion of threads and parallelism to make a library simulator.
this library simulator will simulate the behaviors of Readers : 
student and professors, and their communication to take a book,
take a sit , block when ever there is not a book, or a sit,
prioritize the Professor then student in getting book's and so on.

## Code Style

The Code was Written In Pure Java in the Intellij IDEA IDE.
The synchronisation has been made using semaphores because it is difficult to manage 79 semaphores,
so we accepted the challenge, we have coded the GUI with the JavaFX library, in which there are were a lot of
classes to make a simple or a complex GUI, for animating things in the scene we used also the packages
of JavaFX : javafx.animation, with this package it is very easy to animate in a complex or a simple way, for the static information
like the images, the coordinates of the points and books we used simple final
classes and they are commented everywhere.

## Goal From This Project

First of all, the goal was to get knowledge how to use threads in java and how
to manage threads by using semaphores and different kind of mutual exclusion to
protect critical sections, such as the books in the library, one reader can access
to a book. When the others want to access the same book, they will be blocked until the current
reader will notifies them when he will finishing and releasing the book, so is the case for the places are also critical section,
the table, the chairs, importing places, chain places and so on.
Also, By this project we can say that we can use this solution to manage robots in real life, we can imagine that
we have robots do different works in the same area, so these robots must know the situation of other robots to
make a decision and do an action in the environment if the case we are in fully observable environment,
really it is interesting to manage robots by semaphores,they  are really great tools to manage
problem in the real world of parallelism not only for thread, for this example, it is also for robots, or in
general for anything that can execute simultaneously.

## How to use the application

First of all, after you pull the code from this repository, try to open it in intellij idea
and then launch the application, a window will appear which it contains 3 buttons, the button will have the order to create and launch a thread reader (student or professor) , with the
specific book in the choice box that corresponds, and the last button can launch readers if the checkbox is selected with
a specific book in the choice box, otherwise the button will launch 20 readers together.

## Repository structure

The main application and the resources are in <b>Younes</b> the directory "src":
in this directory there is 2 packages the "com.librarysimulator" and "res".

"com.librarysimulator" : contain 4 packages

    * "Application" package: contains the Main class of the application in which the GUI is constructed and the
       main action such handling event and initialisation of semaphores and so on.

    * "Models" package: contain 2 classes
        - "Student" extends the thread class and override the run method in which 
        the life cycle of the student is presented.
        - "Professor" extends the thread class and override the run method in which 
        the life cycle of the professor is presented.

    * "Providers" package: contain 3 classes
        - "BooksProvider" in which we provide books in the application.
        - "CoordinatesProvider" in which we provide all the points by their X and Y coordinates.
        - "ImagesProvider" in which we provides the Professor, Student and employees images.

    * "Utilities" package: contain one class:
        - "ImagesUtilities" in which there is method to create images with predefine configuration.

"res": contain 2 more directories

    * "Images" directory: contain all the image in subdivides folders.
    * "Others" directory: contain css file for styling the GUI.

## Conclusion

Thanks for all who read this code, I wish this code helps people to learn something new, if you find any issue
just put it in the repository issue section. Thank you again , Peace :D .

سبحانك اللهم و بحمدك, أشهد ان لا إله إلا أنت استغفرك و اتوب اليك.

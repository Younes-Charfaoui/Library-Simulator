package com.librarysimulator.Models;


import com.librarysimulator.Application.Main;
import com.librarysimulator.Providers.CoordinatesProvider;
import com.librarysimulator.Utilities.ImagesUtilities;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Random;
import java.util.concurrent.Semaphore;

import static com.librarysimulator.Application.Main.*;

public class Student extends Thread {

    private String book ;

    public Student(String book) {
        this.book = book;
    }

    private double initialX = CoordinatesProvider.getInitialPoint().getX();
    private double initialY = CoordinatesProvider.getInitialPoint().getY();


    @Override
    public void run() {
        ImagesUtilities mImagesCreator = new ImagesUtilities();
        //an inner semaphore that we will use to play animation one by one for each thread
        Semaphore extraSemaphore = new Semaphore(0);
        // acquiring the mChainSemaphore semaphores
        String book = null;
        try {

            mCurrentBookSemaphore.acquire();
            book = this.book;
            mCurrentBookSemaphore.release();

            mChainSemaphore.acquire();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        ImageView student = null;
        try {

            //a random number generator to generate numbers for choosing a student image
            Random random = new Random();

            //creating a new Student and setting it's parameters and adding it to the scene
            String imageName = String.valueOf(random.nextInt(16) + 1) + ".png";
            student = mImagesCreator.createImageViewOfStudent(imageName);

            //adding the student to the Initial points
            student.setLayoutX(initialX);
            student.setLayoutY(initialY);

            ImageView finalStudent = student;
            //adding the student imageView to the scene in the main Thread
            Platform.runLater(() -> mRoot.getChildren().add(finalStudent));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        int duration = 1000;


        /**
         * defining transition that the Student will passe throw along his lifecycle
         */

        //from the initial points to the First place
        TranslateTransition transitionZero = new TranslateTransition(Duration.millis(duration), student);
        transitionZero.setFromX(0);
        transitionZero.setFromY(0);
        transitionZero.setToX(CoordinatesProvider.getListOfChainPlaces().get(3).getX() - initialX);
        transitionZero.setToY(CoordinatesProvider.getListOfChainPlaces().get(3).getY() - initialY);
        transitionZero.setOnFinished(event -> extraSemaphore.release());

        //from 1 to the 2
        TranslateTransition transitionOne = new TranslateTransition(Duration.millis(duration), student);
        transitionOne.setFromX(CoordinatesProvider.getListOfChainPlaces().get(3).getX() - initialX);
        transitionOne.setFromY(CoordinatesProvider.getListOfChainPlaces().get(3).getY() - initialY);
        transitionOne.setToX(CoordinatesProvider.getListOfChainPlaces().get(2).getX() - initialX);
        transitionOne.setToY(CoordinatesProvider.getListOfChainPlaces().get(2).getY() - initialY);
        transitionOne.setOnFinished(event -> extraSemaphore.release());

        //from 2 to 3
        TranslateTransition transitionTwo = new TranslateTransition(Duration.millis(duration), student);
        transitionTwo.setFromX(CoordinatesProvider.getListOfChainPlaces().get(2).getX() - initialX);
        transitionTwo.setFromY(CoordinatesProvider.getListOfChainPlaces().get(2).getY() - initialY);
        transitionTwo.setToX(CoordinatesProvider.getListOfChainPlaces().get(1).getX() - initialX);
        transitionTwo.setToY(CoordinatesProvider.getListOfChainPlaces().get(1).getY() - initialY);
        transitionTwo.setOnFinished(event -> extraSemaphore.release());

        //from 3 to 4
        TranslateTransition transitionThree = new TranslateTransition(Duration.millis(duration), student);
        transitionThree.setFromX(CoordinatesProvider.getListOfChainPlaces().get(1).getX() - initialX);
        transitionThree.setFromY(CoordinatesProvider.getListOfChainPlaces().get(1).getY() - initialY);
        transitionThree.setToX(CoordinatesProvider.getListOfChainPlaces().get(0).getX() - initialX);
        transitionThree.setToY(CoordinatesProvider.getListOfChainPlaces().get(0).getY() - initialY);
        transitionThree.setOnFinished(event -> extraSemaphore.release());


        try {

            /**
             * now the execution of the transition in way that two thread cannot access to
             * the same place and in such a method that a thread can execute one animation in
             * a specific moments
             */

            //getting access to the first place by its semaphore
            mChainSemaphoresArray[0].acquire();
            //then if we get access we can translate throw it in The main Thread
            Platform.runLater(transitionZero::play);
            //waiting the animation to stop
            extraSemaphore.acquire();

            //getting access to the second place by its semaphore
            mChainSemaphoresArray[1].acquire();
            //then if we get access we can translate throw it in The main Thread
            Platform.runLater(transitionOne::play);
            //waiting the animation to stop
            extraSemaphore.acquire();
            //now we can let someone access the first places because we are in the second place
            mChainSemaphoresArray[0].release();

            //getting access to the third place by its semaphore
            mChainSemaphoresArray[2].acquire();
            //then if we get access we can translate throw it in The main Thread
            Platform.runLater(transitionTwo::play);
            //waiting the animation to stop
            extraSemaphore.acquire();
            //now we can let someone access the second places because we are in the third place
            mChainSemaphoresArray[1].release();

            //getting access to the final mChainSemaphore place by its semaphore
            mChainSemaphoresArray[3].acquire();
            //then if we get access we can translate throw it in The main Thread
            Platform.runLater(transitionThree::play);
            //waiting the animation to stop
            extraSemaphore.acquire();
            //now we can let someone access the third places because we are in the final place
            mChainSemaphoresArray[2].release();


            /**
             * we are now in position to go to the importing place by the entry point
             * so we will first acquire the mImportSemaphore semaphore which have the permits of 5
             * then we will also have to acquire the entry semaphore for the intermediary place
             * and in that moment we will calculate the first empty place in the import mChainSemaphore
             * and we know that there is a place because we have access to it by the import semaphore
             * which have the permits of 5, then for the map which holding the empty places with their coordinate
             * we are using it mutually because of the entry semaphore
             */


            //acquiring the import semaphore which contains 5 permits
            mImportSemaphore.acquire();

            //acquiring the entry semaphore which have permits for only one
            mEntrySemaphore.acquire();

            // an int to save the position of the ImageView in the ImportChain after translate To it
            int indexInImportChain = -1;

            //getting the place empty in the import place
            Point2D importPoint = null;

            mAvailableImportMutex.acquire();
            for (int i = 0; i < mAvailableImportPlaces.size(); i++) {
                if (!mAvailableImportPlaces.get(CoordinatesProvider.getListOfImportPlaces().get(i))) {
                    indexInImportChain = i;
                    importPoint = CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain);
                    mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain), true);
                    break;
                }
            }
            mAvailableImportMutex.release();

            //creating the translation from the mChainSemaphore to import via a place know as Entry points
            TranslateTransition transitionEntry = new TranslateTransition(Duration.millis(duration), student);
            transitionEntry.setFromX(CoordinatesProvider.getListOfChainPlaces().get(0).getX() - initialX);
            transitionEntry.setFromY(CoordinatesProvider.getListOfChainPlaces().get(0).getY() - initialY);
            transitionEntry.setToX(CoordinatesProvider.getEntryPoint().getX() - initialX);
            transitionEntry.setToY(CoordinatesProvider.getEntryPoint().getY() - initialY);
            transitionEntry.setOnFinished(event -> extraSemaphore.release());


            // Creating the translation to the available import place
            TranslateTransition transitionImport = new TranslateTransition(Duration.millis(duration), student);
            transitionImport.setFromX(CoordinatesProvider.getEntryPoint().getX() - initialX);
            transitionImport.setFromY(CoordinatesProvider.getEntryPoint().getY() - initialY);
            if (importPoint != null) {
                transitionImport.setToX(importPoint.getX() - initialX);
                transitionImport.setToY(importPoint.getY() - initialY);
            }
            transitionImport.setOnFinished(event -> extraSemaphore.release());

            //then we can translate to the entry point safely in the Main Thread
            Platform.runLater(transitionEntry::play);
            extraSemaphore.acquire();

            //realising the Position number 4
            mChainSemaphoresArray[3].release();


            //finally we can translate to the import place with the indexInImportChain
            Platform.runLater(transitionImport::play);
            extraSemaphore.acquire();

            //realising the mChainSemaphore semaphore for letting someone out to enter in the mChainSemaphore
            mChainSemaphore.release();

            //realising the entry semaphore for letting the other to access the import mChainSemaphore if there is  place
            mEntrySemaphore.release();


            /**
             * so far we translate our ImageView from the Chain  places to the import places where
             * the reader will get a book if and if the book is available , then he will grab a sit
             * from to the table if and if there is at least one place available, otherwise the reader will
             * grab a sit from the waiting chairs if and if there is a place available in the chair places.
             *
             * if the reader want a book that someone is already reading it in the table the reader will
             * wait in the import mChainSemaphore and not in the waiting chairs because this chair are used by the person
             * who has already the book and they did not find a place at the table, so in this case the reader
             * will wait in the import mChainSemaphore and if all the readers in the import mChainSemaphore are waiting for book's from
             * the readers in the table, they will block reader who are in the Chain from getting mBooksObservableList even
             * if this mBooksObservableList are not used  because we are using The First come is the First who served FCFS
             */


            //sleeping to simulate the waiting for the book to come
            Thread.sleep(1000);

            //acquiring the book by its semaphore
            mBooksSemaphoresMap.get(book).acquire();

            /**
             * in this points there is multiple Things can happen so let's explain that in detail
             *
             * @detail of the problem
             *
             * after getting the Book, the Reader must see if there is readers waiting in chairs of waiting
             * if there is no ones, we have to see if there is a place in the table, if there is a place
             * we simply translate to it, otherwise we have to grab a sit in the waiting place by acquiring
             * it's semaphore which have the permits of 3 and translate to it , then we will acquire the
             * Table Semaphore, so the Reader will be automatically blocked and he will wait for a reader
             * from the table to quit and release Table semaphore.
             *
             * another scenario its when the reader get a book and again he will find that there is already
             * reader in the waiting chair if the numbers of this readers is >=3 then the Reader will block
             * on his actual position by acquiring the waiting semaphore which will cause to block ,
             * otherwise if there is one or two we acquire the semaphore and translate to the empty place ,
             * and release the import semaphore.
             *
             * when the reader of the table quit he must first see if there is someone in the waiting chairs, if it
             * is the case the reader will have to release the Table semaphore which the waiting reader in the waiting
             * chair are blocked in, but what about if there is no one waiting and someone come into the import
             * chain ? then this reader have to release the Table semaphore also hahahahahahaha , I Got you :D :D
             */

            Point2D waitingPoint = null;
            int indexInTheWaitingChairs = -1;

            Point2D tablePoint = null;
            int indexInTable = -1;

            mWaitingCounterMutex.acquire();

            //if there is 3 people in the waiting chair we must block
            if (mWaitingCounter >= 3) {
                //we release the mutex semaphore and acquire the Waiting one
                mWaitingCounterMutex.release();


                //after that when the reader acquire the Waiting Semaphore
                //Translate to the empty place in the waiting
                mWaitingSemaphore.acquire();
                mWaitingCounterMutex.acquire();
                mWaitingCounter++;
                mWaitingCounterMutex.release();

                //translate to the empty place in the Waiting chairs
                mAvailableWaitingMutex.acquire();
                for (int i = 0; i < mAvailableWaitingPlaces.size(); i++) {
                    if (!mAvailableWaitingPlaces.get(CoordinatesProvider.getListOfWaitingPlaces().get(i))) {
                        indexInTheWaitingChairs = i;
                        waitingPoint = CoordinatesProvider.getListOfWaitingPlaces().get(i);
                        mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(i), true);
                        break;
                    }
                }
                mAvailableWaitingMutex.release();

                //translation to the empty place in the waiting chair
                TranslateTransition transitionWaiting = new TranslateTransition(Duration.millis(1000), student);
                transitionWaiting.setFromX(importPoint.getX() - initialX);
                transitionWaiting.setFromY(importPoint.getY() - initialY);
                transitionWaiting.setToX(waitingPoint.getX() - initialX);
                transitionWaiting.setToY(waitingPoint.getY() - initialY);
                transitionWaiting.setOnFinished(event -> extraSemaphore.release());

                Platform.runLater(transitionWaiting::play);

                extraSemaphore.acquire();
                //also we have to set the pace in the hash ap to false to indicate that the place is empty
                mAvailableImportMutex.acquire();
                mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain), false);
                mAvailableImportMutex.release();

                //after we have translate to the waiting place we have also tto release the import semaphore -_-
                mImportSemaphore.release();

                mTableSemaphore.acquire();
                //now the reader is waiting ih the waiting chairs
                //translate to the empty place and decrement the mWaiting Counter

                //searching for the empty place in the Table
                mAvailableTableMutex.acquire();
                for (int i = 0; i < mAvailableTablePlaces.size(); i++) {
                    if (!mAvailableTablePlaces.get(CoordinatesProvider.getListOfTablePlaces().get(i))) {
                        indexInTable = i;
                        tablePoint = CoordinatesProvider.getListOfTablePlaces().get(indexInTable);
                        mAvailableTablePlaces.put(CoordinatesProvider.getListOfTablePlaces().get(indexInTable), true);
                        break;
                    }
                }
                mAvailableTableMutex.release();

                //create a Translation from the Place where we The Reader is to the Available Place in the table
                TranslateTransition transitionToTable = new TranslateTransition(Duration.millis(1000), student);
                if (tablePoint != null) {
                    transitionToTable.setFromX(waitingPoint.getX() - initialX);
                    transitionToTable.setFromY(waitingPoint.getY() - initialY);
                    transitionToTable.setToX(tablePoint.getX() - initialX);
                    transitionToTable.setToY(tablePoint.getY() - initialY);
                }
                transitionToTable.setOnFinished(event -> extraSemaphore.release());


                Platform.runLater(transitionToTable::play);
                extraSemaphore.acquire();

                mTableCounterMutex.acquire();
                mTableCounter++;
                mTableCounterMutex.release();

                //we have to change the state of the actual place to false, to indicate that it is empty
                mAvailableWaitingMutex.acquire();
                mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs), false);
                mAvailableWaitingMutex.release();

                //translate to the place
                mWaitingCounterMutex.acquire();
                mWaitingCounter--;
                mWaitingCounterMutex.release();

                mWaitingSemaphore.release();
            } else {

                //in this case  we check if there are no ones waiting
                if (mWaitingCounter == 0) {
                    //if it is the case we check if there is empty place in the table
                    mTableCounterMutex.acquire();
                    if (mTableCounter >= 12) {
                        //if there is no place
                        mTableCounterMutex.release();
                        mWaitingSemaphore.acquire();
                        mWaitingCounter++;
                        mWaitingCounterMutex.release();

                        //translate to the empty place in the waiting chair
                        //translate to the empty place in the Waiting chairs
                        mAvailableWaitingMutex.acquire();
                        for (int i = 0; i < mAvailableWaitingPlaces.size(); i++) {
                            if (!mAvailableWaitingPlaces.get(CoordinatesProvider.getListOfWaitingPlaces().get(i))) {
                                indexInTheWaitingChairs = i;
                                waitingPoint = CoordinatesProvider.getListOfWaitingPlaces().get(i);
                                mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(i), true);
                                break;
                            }
                        }
                        mAvailableWaitingMutex.release();

                        //translation to the empty place in the waiting chair
                        TranslateTransition transitionWaiting = new TranslateTransition(Duration.millis(1000), student);
                        transitionWaiting.setFromX(importPoint.getX() - initialX);
                        transitionWaiting.setFromY(importPoint.getY() - initialY);
                        transitionWaiting.setToX(waitingPoint.getX() - initialX);
                        transitionWaiting.setToY(waitingPoint.getY() - initialY);
                        transitionWaiting.setOnFinished(event -> extraSemaphore.release());

                        Platform.runLater(transitionWaiting::play);

                        extraSemaphore.acquire();
                        //also we have to set the pace in the hash ap to false to indicate that the place is empty
                        mAvailableImportMutex.acquire();
                        mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain), false);
                        mAvailableImportMutex.release();

                        //after we have translate to the waiting place we have also tto release the import semaphore -_-
                        mImportSemaphore.release();
                        //acquire the table semaphore
                        mTableSemaphore.acquire();

                        //now the reader is waiting ih the waiting chairs
                        //translate to the empty place and decrement the mWaiting Counter

                        //searching for the empty place in the Table
                        mAvailableTableMutex.acquire();
                        for (int i = 0; i < mAvailableTablePlaces.size(); i++) {
                            if (!mAvailableTablePlaces.get(CoordinatesProvider.getListOfTablePlaces().get(i))) {
                                indexInTable = i;
                                tablePoint = CoordinatesProvider.getListOfTablePlaces().get(indexInTable);
                                mAvailableTablePlaces.put(CoordinatesProvider.getListOfTablePlaces().get(indexInTable), true);
                                break;
                            }
                        }
                        mAvailableTableMutex.release();

                        //create a Translation from the Place where we The Reader is to the Available Place in the table
                        TranslateTransition transitionToTable = new TranslateTransition(Duration.millis(1000), student);
                        if (tablePoint != null) {
                            transitionToTable.setFromX(waitingPoint.getX() - initialX);
                            transitionToTable.setFromY(waitingPoint.getY() - initialY);
                            transitionToTable.setToX(tablePoint.getX() - initialX);
                            transitionToTable.setToY(tablePoint.getY() - initialY);
                        }
                        transitionToTable.setOnFinished(event -> extraSemaphore.release());


                        Platform.runLater(transitionToTable::play);
                        extraSemaphore.acquire();

                        mTableCounterMutex.acquire();
                        mTableCounter++;
                        mTableCounterMutex.release();

                        mAvailableWaitingMutex.acquire();
                        mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs), false);
                        mAvailableWaitingMutex.release();

                        //translate to the place
                        mWaitingCounterMutex.acquire();
                        mWaitingCounter--;
                        mWaitingCounterMutex.release();
                        mWaitingSemaphore.release();
                    } else {
                        //that means there is a place in the table
                        mTableSemaphore.acquire();
                        //translate to the empty place in the Table
                        //searching for the empty place in the Table
                        mAvailableTableMutex.acquire();
                        for (int i = 0; i < mAvailableTablePlaces.size(); i++) {
                            if (!mAvailableTablePlaces.get(CoordinatesProvider.getListOfTablePlaces().get(i))) {
                                indexInTable = i;
                                tablePoint = CoordinatesProvider.getListOfTablePlaces().get(indexInTable);
                                mAvailableTablePlaces.put(CoordinatesProvider.getListOfTablePlaces().get(indexInTable), true);
                                break;
                            }
                        }
                        mAvailableTableMutex.release();

                        //create a Translation from the Place where we The Reader is to the Available Place in the table
                        TranslateTransition transitionToTable = new TranslateTransition(Duration.millis(1000), student);
                        if (tablePoint != null) {
                            transitionToTable.setFromX(importPoint.getX() - initialX);
                            transitionToTable.setFromY(importPoint.getY() - initialY);
                            transitionToTable.setToX(tablePoint.getX() - initialX);
                            transitionToTable.setToY(tablePoint.getY() - initialY);
                        }
                        transitionToTable.setOnFinished(event -> extraSemaphore.release());


                        Platform.runLater(transitionToTable::play);
                        extraSemaphore.acquire();
                        mAvailableImportMutex.acquire();
                        mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain), false);
                        mAvailableImportMutex.release();

                        mImportSemaphore.release();

                        mTableCounter++;
                        mTableCounterMutex.release();
                        mWaitingCounterMutex.release();
                    }
                } else {
                    //that means there only one or two in the waiting chairs
                    mWaitingSemaphore.acquire();

                    mWaitingCounter++;
                    mWaitingCounterMutex.release();
                    //translate to the empty place in the waiting table
                    //translate to the empty place in the Waiting chairs
                    mAvailableWaitingMutex.acquire();
                    for (int i = 0; i < mAvailableWaitingPlaces.size(); i++) {
                        if (!mAvailableWaitingPlaces.get(CoordinatesProvider.getListOfWaitingPlaces().get(i))) {
                            indexInTheWaitingChairs = i;
                            waitingPoint = CoordinatesProvider.getListOfWaitingPlaces().get(i);
                            mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(i), true);
                            break;
                        }
                    }
                    mAvailableWaitingMutex.release();

                    //translation to the empty place in the waiting chair
                    TranslateTransition transitionWaiting = new TranslateTransition(Duration.millis(1000), student);
                    transitionWaiting.setFromX(importPoint.getX() - initialX);
                    transitionWaiting.setFromY(importPoint.getY() - initialY);
                    transitionWaiting.setToX(waitingPoint.getX() - initialX);
                    transitionWaiting.setToY(waitingPoint.getY() - initialY);
                    transitionWaiting.setOnFinished(event -> extraSemaphore.release());

                    Platform.runLater(transitionWaiting::play);

                    extraSemaphore.acquire();
                    //also we have to set the pace in the hash ap to false to indicate that the place is empty
                    mAvailableImportMutex.acquire();
                    mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain), false);
                    mAvailableImportMutex.release();

                    //after we have translate to the waiting place we have also tto release the import semaphore -_-
                    mImportSemaphore.release();


                    mTableSemaphore.acquire();

                    mTableCounterMutex.acquire();
                    mTableCounter++;
                    mTableCounterMutex.release();

                    //now the reader is waiting ih the waiting chairs
                    //translate to the empty place and decrement the mWaiting Counter

                    //searching for the empty place in the Table
                    mAvailableTableMutex.acquire();
                    for (int i = 0; i < mAvailableTablePlaces.size(); i++) {
                        if (!mAvailableTablePlaces.get(CoordinatesProvider.getListOfTablePlaces().get(i))) {
                            indexInTable = i;
                            tablePoint = CoordinatesProvider.getListOfTablePlaces().get(indexInTable);
                            mAvailableTablePlaces.put(CoordinatesProvider.getListOfTablePlaces().get(indexInTable), true);
                            break;
                        }
                    }
                    mAvailableTableMutex.release();

                    //create a Translation from the Place where we The Reader is to the Available Place in the table
                    TranslateTransition transitionToTable = new TranslateTransition(Duration.millis(1000), student);
                    if (tablePoint != null) {
                        transitionToTable.setFromX(waitingPoint.getX() - initialX);
                        transitionToTable.setFromY(waitingPoint.getY() - initialY);
                        transitionToTable.setToX(tablePoint.getX() - initialX);
                        transitionToTable.setToY(tablePoint.getY() - initialY);
                    }
                    transitionToTable.setOnFinished(event -> extraSemaphore.release());


                    Platform.runLater(transitionToTable::play);
                    extraSemaphore.acquire();

                    mAvailableWaitingMutex.acquire();
                    Main.mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs), false);
                    mAvailableWaitingMutex.release();

                    //translate to the place
                    Main.mWaitingCounterMutex.acquire();
                    Main.mWaitingCounter--;
                    Main.mWaitingCounterMutex.release();
                    Main.mWaitingSemaphore.release();
                }
            }


            Thread.sleep(60000);

            /**
             * at this points the reader will leave the library , but before that he must report the book to the
             * return place, which gonna be accessed by another chain called the Outer Chain , this chain has 6
             * places, each of these must be in mutual exclusion -_-.
             */

            Point2D returnPoint = null;
            int indexInReturn = -1;

            Main.mReturningSemaphore.acquire();

            Main.mAvailableReturnMutex.acquire();
            for (int i = 0; i < Main.mAvailableReturnPlaces.size(); i++) {
                if (!Main.mAvailableReturnPlaces.get(CoordinatesProvider.getListOfReturningPlaces().get(i))) {
                    indexInReturn = i;
                    returnPoint = CoordinatesProvider.getListOfReturningPlaces().get(i);
                    Main.mAvailableReturnPlaces.put(CoordinatesProvider.getListOfReturningPlaces().get(i), true);
                    break;
                }
            }
            Main.mAvailableReturnMutex.release();

            //change the value of false in the available places to indicate that it is empty
            mAvailableTableMutex.acquire();
            mAvailableTablePlaces.put(CoordinatesProvider.getListOfTablePlaces().get(indexInTable), false);
            mAvailableTableMutex.release();

            Main.mTableCounterMutex.acquire();
            Main.mTableCounter--;
            Main.mTableCounterMutex.release();

            //now we translate to the empty place
            TranslateTransition transitionToReturn = new TranslateTransition(Duration.millis(1000), student);
            transitionToReturn.setFromX(tablePoint.getX() - initialX);
            transitionToReturn.setFromY(tablePoint.getY() - initialY);
            transitionToReturn.setToX(returnPoint.getX() - initialX);
            transitionToReturn.setToY(returnPoint.getY() - initialY);
            transitionToReturn.setOnFinished(event -> extraSemaphore.release());

            Platform.runLater(transitionToReturn::play);
            extraSemaphore.acquire();

            //and release the Table Semaphores
            Main.mTableSemaphore.release();

            //simulating the return time
            Thread.sleep(1000);

            Main.mBooksSemaphoresMap.get(book).release();
            Main.mOutChainSemaphoresArray[0].acquire();



            //now we have to translate to the first place in the out chain
            TranslateTransition transitionOutOne = new TranslateTransition(Duration.millis(1000), student);
            transitionOutOne.setFromX(returnPoint.getX() - initialX);
            transitionOutOne.setFromY(returnPoint.getY() - initialY);
            transitionOutOne.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(0).getX() - initialX);
            transitionOutOne.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(0).getY() - initialY);
            transitionOutOne.setOnFinished(event -> extraSemaphore.release());

            Platform.runLater(transitionOutOne::play);
            extraSemaphore.acquire();

            Main.mAvailableReturnMutex.acquire();
            Main.mAvailableReturnPlaces.put(CoordinatesProvider.getListOfReturningPlaces().get(indexInReturn),false);
            Main.mAvailableReturnMutex.release();

            Main.mReturningSemaphore.release();

            Main.mOutChainSemaphoresArray[1].acquire();
            TranslateTransition transitionOutTwo = new TranslateTransition(Duration.millis(1000), student);
            transitionOutTwo.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(0).getX() - initialX);
            transitionOutTwo.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(0).getY() - initialY);
            transitionOutTwo.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(1).getX() - initialX);
            transitionOutTwo.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(1).getY() - initialY);
            transitionOutTwo.setOnFinished(event -> extraSemaphore.release());

            Platform.runLater(transitionOutTwo::play);
            extraSemaphore.acquire();

            Main.mOutChainSemaphoresArray[0].release();

            Main.mOutChainSemaphoresArray[2].acquire();
            TranslateTransition transitionOutThree = new TranslateTransition(Duration.millis(1000), student);
            transitionOutThree.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(1).getX() - initialX);
            transitionOutThree.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(1).getY() - initialY);
            transitionOutThree.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(2).getX() - initialX);
            transitionOutThree.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(2).getY() - initialY);
            transitionOutThree.setOnFinished(event -> extraSemaphore.release());

            Platform.runLater(transitionOutThree::play);
            extraSemaphore.acquire();

            Main.mOutChainSemaphoresArray[1].release();

            Main.mOutChainSemaphoresArray[3].acquire();
            TranslateTransition transitionOutFour = new TranslateTransition(Duration.millis(1000), student);
            transitionOutFour.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(2).getX() - initialX);
            transitionOutFour.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(2).getY() - initialY);
            transitionOutFour.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(3).getX() - initialX);
            transitionOutFour.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(3).getY() - initialY);
            transitionOutFour.setOnFinished(event -> extraSemaphore.release());

            Platform.runLater(transitionOutFour::play);
            extraSemaphore.acquire();

            Main.mOutChainSemaphoresArray[2].release();

            Main.mOutChainSemaphoresArray[4].acquire();
            TranslateTransition transitionOutFive = new TranslateTransition(Duration.millis(1000), student);
            transitionOutFive.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(3).getX() - initialX);
            transitionOutFive.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(3).getY() - initialY);
            transitionOutFive.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(4).getX() - initialX);
            transitionOutFive.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(4).getY() - initialY);
            transitionOutFive.setOnFinished(event -> extraSemaphore.release());

            Platform.runLater(transitionOutFive::play);
            extraSemaphore.acquire();

            Main.mOutChainSemaphoresArray[3].release();

            Main.mOutChainSemaphoresArray[5].acquire();
            TranslateTransition transitionOutSix = new TranslateTransition(Duration.millis(1000), student);
            transitionOutSix.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(4).getX() - initialX);
            transitionOutSix.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(4).getY() - initialY);
            transitionOutSix.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(5).getX() - initialX);
            transitionOutSix.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(5).getY() - initialY);
            transitionOutSix.setOnFinished(event -> extraSemaphore.release());

            Platform.runLater(transitionOutSix::play);
            extraSemaphore.acquire();

            Main.mOutChainSemaphoresArray[4].release();

            /**
             * and now the final transition out of the scene :D
             */
            TranslateTransition transitionFinal = new TranslateTransition(Duration.millis(1000), student);
            transitionFinal.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(5).getX() - initialX);
            transitionFinal.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(5).getY() - initialY);
            transitionFinal.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(5).getX() - initialX);
            transitionFinal.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(5).getY() - initialY + 110);
            transitionFinal.setOnFinished(event -> extraSemaphore.release());

            Platform.runLater(transitionFinal::play);
            extraSemaphore.acquire();

            Main.mOutChainSemaphoresArray[5].release();

            // at these points our reader is out of the scene
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }




    }
}

package com.librarysimulator.Models;

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

public class Professor extends Thread {

    /**
     * @definition this class id the definition for a professor
     * life cycle in the library, all this is defined in the run method
     */

    //member variable to hold the mBook
    private final String mBook;

    //public constructor
    public Professor(String book) {
        this.mBook = book;
    }

    //double variables to holds the initial position of each imageViews
    private static final double INITIAL_X = CoordinatesProvider.getInitialPoint().getX();
    private static final double INITIAL_Y = CoordinatesProvider.getInitialPoint().getY();

    //the run method in which the cycle of the Professor in this library
    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {

        try {
            //instance of the Image Utilities to create image for the Professor
            ImagesUtilities mImagesCreator = new ImagesUtilities();

            //an inner semaphore that we will use to play animation one by one for each thread
            Semaphore extraSemaphore = new Semaphore(0);

            //image view for the professor to animate
            ImageView professor;

            String book = this.mBook;

            // acquiring the mChainSemaphore semaphores
            mChainSemaphore.acquire();

            //creating a new Professor and setting it's parameters and adding it to the scene
            String imageName = String.valueOf(new Random().nextInt(32 - 17) + 17) + ".png";
            professor = mImagesCreator.createImageViewOfProfessor(imageName);

            //adding the professor to the Initial points
            professor.setLayoutX(INITIAL_X);
            professor.setLayoutY(INITIAL_Y);

            //reference to the image of the Professor to add it tto the scene
            ImageView finalProfessor = professor;

            //adding the professor imageView to the scene in the main Thread
            Platform.runLater(() -> mRoot.getChildren().add(finalProfessor));
            /**
             * defining the Transition with the time, X and Y ; and the Specific Node
             */

            TranslateTransition transitionAnimation = new TranslateTransition(Duration.millis(DURATION_ANIMATION), professor);
            transitionAnimation.setFromX(0);
            transitionAnimation.setFromY(0);
            transitionAnimation.setToX(CoordinatesProvider.getListOfChainPlaces().get(3).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfChainPlaces().get(3).getY() - INITIAL_Y);

            /**
             * we want to execute the animation one by one, but the play method of the transition
             * will execute in separate thread, so we will wait until the animation to finished
             * too do the other and so on. for that  when the animation finished we will release
             * a semaphore which the current thread will be blocked in
             */
            transitionAnimation.setOnFinished(event -> extraSemaphore.release());

            /**
             * now the execution of the transition in way that two thread cannot access to
             * the same place and in such a method that a thread can execute one animation in
             * a specific moments
             */

            //getting access to the first place by its semaphore
            mChainSemaphoresList.get(0).acquire();
            //then if we get access we can translate throw it in The main Thread
            Platform.runLater(transitionAnimation::play);
            //waiting the animation to stop
            extraSemaphore.acquire();

            //to the second place
            transitionAnimation.setFromX(CoordinatesProvider.getListOfChainPlaces().get(3).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfChainPlaces().get(3).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfChainPlaces().get(2).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfChainPlaces().get(2).getY() - INITIAL_Y);

            //getting access to the second place by its semaphore
            mChainSemaphoresList.get(1).acquire();
            //then if we get access we can translate throw it in The main Thread
            Platform.runLater(transitionAnimation::play);
            //waiting the animation to stop
            extraSemaphore.acquire();

            //now we can let someone access the first places because we are in the second place
            mChainSemaphoresList.get(0).release();

            //to the third place
            transitionAnimation.setFromX(CoordinatesProvider.getListOfChainPlaces().get(2).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfChainPlaces().get(2).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfChainPlaces().get(1).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfChainPlaces().get(1).getY() - INITIAL_Y);

            //getting access to the third place by its semaphore
            mChainSemaphoresList.get(2).acquire();
            //then if we get access we can translate throw it in The main Thread
            Platform.runLater(transitionAnimation::play);
            //waiting the animation to stop
            extraSemaphore.acquire();

            //now we can let someone access the second places because we are in the third place
            mChainSemaphoresList.get(1).release();

            //to the fourth place
            transitionAnimation.setFromX(CoordinatesProvider.getListOfChainPlaces().get(1).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfChainPlaces().get(1).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfChainPlaces().get(0).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfChainPlaces().get(0).getY() - INITIAL_Y);

            //getting access to the final mChainSemaphore place by its semaphore
            mChainSemaphoresList.get(3).acquire();
            //then if we get access we can translate throw it in The main Thread
            Platform.runLater(transitionAnimation::play);
            //waiting the animation to stop
            extraSemaphore.acquire();
            //now we can let someone access the third places because we are in the final place
            mChainSemaphoresList.get(2).release();


            /**
             * We are now in position to go to the importing place by the entry point
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

            //searching for the available place in the importing chain
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
            transitionAnimation.setFromX(CoordinatesProvider.getListOfChainPlaces().get(0).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfChainPlaces().get(0).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getEntryPoint().getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getEntryPoint().getY() - INITIAL_Y);

            //then we can translate to the entry point safely in the Main Thread
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            // Creating the translation to the available import place
            if (importPoint != null) {
                transitionAnimation.setFromX(CoordinatesProvider.getEntryPoint().getX() - INITIAL_X);
                transitionAnimation.setFromY(CoordinatesProvider.getEntryPoint().getY() - INITIAL_Y);
                transitionAnimation.setToX(importPoint.getX() - INITIAL_X);
                transitionAnimation.setToY(importPoint.getY() - INITIAL_Y);
            }
            //realising the Position number 4
            mChainSemaphoresList.get(3).release();


            //finally we can translate to the import place with the indexInImportChain
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            //realising the mChainSemaphore semaphore for letting someone out to enter in the mChainSemaphore
            mChainSemaphore.release();

            int finalIndexInImportChain = indexInImportChain;
            Platform.runLater(() -> mBooksLabelImporting.get(finalIndexInImportChain).setText(book));

            //realising the entry semaphore for letting the other to access the import mChainSemaphore if there is  place
            mEntrySemaphore.release();

            /**
             * so far we translate our ImageView from the Chain  places to the import places where
             * the reader will get a mBook if and if the mBook is available , then he will grab a sit
             * from to the table if and if there is at least one place available, otherwise the reader will
             * grab a sit from the waiting chairs if and if there is a place available in the chair places.
             *
             * if the reader want a mBook that someone is already reading it in the table the reader will
             * wait in the import mChainSemaphore and not in the waiting chairs because this chair are used by the person
             * who has already the mBook and they did not find a place at the table, so in this case the reader
             * will wait in the import mChainSemaphore and if all the readers in the import mChainSemaphore are waiting for mBook's from
             * the readers in the table, they will block reader who are in the Chain from getting mBooksObservableList even
             * if this mBooksObservableList are not used  because we are using The First come is the First who served FCFS
             */

            //sleeping to simulate the waiting for the mBook to come
            Thread.sleep(DURATION_IMPORTING);


            //acquiring the mBook by its semaphore
            mProfessorCounterMutex.acquire();
            mProfessorCounterMap.replace(book, mProfessorCounterMap.get(book) + 1);
            mProfessorCounterMutex.release();

            mBooksSemaphoresMap.get(book).acquire();

            mProfessorCounterMutex.acquire();
            mProfessorCounterMap.replace(book, mProfessorCounterMap.get(book) - 1);
            mProfessorCounterMutex.release();

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
             * another scenario its when the reader get a mBook and again he will find that there is already
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

            //acquiring the counter waiting semaphores to check the numbers of waiting their
            mWaitingCounterMutex.acquire();

            //if there is 3 people in the waiting chair we must block
            if (mWaitingCounter >= 3) {

                //we release the mutex semaphore and acquire the Waiting one
                mWaitingCounterMutex.release();

                //after that when the reader acquire the Waiting Semaphore
                mWaitingSemaphore.acquire();
                mWaitingCounterMutex.acquire();
                mWaitingCounter++;
                mWaitingCounterMutex.release();

                //searching for the empty place in the waiting chair
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
                if (importPoint != null && waitingPoint != null) {
                    transitionAnimation.setFromX(importPoint.getX() - INITIAL_X);
                    transitionAnimation.setFromY(importPoint.getY() - INITIAL_Y);
                    transitionAnimation.setToX(waitingPoint.getX() - INITIAL_X);
                    transitionAnimation.setToY(waitingPoint.getY() - INITIAL_Y);
                }
                Platform.runLater(() -> mBooksLabelImporting.get(finalIndexInImportChain).setText(""));
                Platform.runLater(transitionAnimation::play);
                extraSemaphore.acquire();

                //setting the value of the corresponding label to the mBook
                int finalIndexInTheWaitingChairs = indexInTheWaitingChairs;
                Platform.runLater(() -> mBooksLabelListWaiting.get(finalIndexInTheWaitingChairs).setText(book));

                //also we have to set the place in the hash ap to false to indicate that the place is empty
                mAvailableImportMutex.acquire();
                mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain), false);
                mAvailableImportMutex.release();

                //after we have translate to the waiting place we have also tto release the import semaphore -_-
                mImportSemaphore.release();

                mTableSemaphore.acquire();
                /**
                 * now the reader is waiting in the waiting chairs*/

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
                if (tablePoint != null && waitingPoint != null) {
                    transitionAnimation.setFromX(waitingPoint.getX() - INITIAL_X);
                    transitionAnimation.setFromY(waitingPoint.getY() - INITIAL_Y);
                    transitionAnimation.setToX(tablePoint.getX() - INITIAL_X);
                    transitionAnimation.setToY(tablePoint.getY() - INITIAL_Y);
                }
                Platform.runLater(transitionAnimation::play);

                //setting the value of the corresponding label to the mBook
                Platform.runLater(() -> mBooksLabelListWaiting.get(finalIndexInTheWaitingChairs).setText(""));
                extraSemaphore.acquire();

                //setting the value of the corresponding label to the mBook
                int finalIndexInTable = indexInTable;
                Platform.runLater(() -> mBooksLabelList.get(finalIndexInTable).setText(book));
                mTableCounterMutex.acquire();
                mTableCounter++;
                mTableCounterMutex.release();

                //we have to change the state of the actual place to false, to indicate that it is empty
                mAvailableWaitingMutex.acquire();
                mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs), false);
                mAvailableWaitingMutex.release();

                //decrement the number of the people waiting
                mWaitingCounterMutex.acquire();
                mWaitingCounter--;
                mWaitingCounterMutex.release();

                mWaitingSemaphore.release();
            } else {
                /**
                 * that means there is no one waiting of there is only one or two
                 */

                //in this case  we check if there are no ones waiting
                if (mWaitingCounter == 0) {

                    //release the counter semaphore for the waiting counter

                    //if it is the case we check if there is empty place in the table
                    mTableCounterMutex.acquire();
                    if (mTableCounter >= 12) {
                        //if there is no place
                        mTableCounterMutex.release();
                        mWaitingSemaphore.acquire();
                        mWaitingCounter++;
                        mWaitingCounterMutex.release();

                        //searching the empty place in the Waiting chairs
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
                        if (importPoint != null && waitingPoint != null) {
                            transitionAnimation.setFromX(importPoint.getX() - INITIAL_X);
                            transitionAnimation.setFromY(importPoint.getY() - INITIAL_Y);
                            transitionAnimation.setToX(waitingPoint.getX() - INITIAL_X);
                            transitionAnimation.setToY(waitingPoint.getY() - INITIAL_Y);
                        }
                        Platform.runLater(() -> mBooksLabelImporting.get(finalIndexInImportChain).setText(""));
                        Platform.runLater(transitionAnimation::play);

                        //setting the value of the corresponding label to the mBook
                        int finalIndexInTheWaitingChairs1 = indexInTheWaitingChairs;
                        Platform.runLater(() -> mBooksLabelListWaiting.get(finalIndexInTheWaitingChairs1).setText(book));
                        extraSemaphore.acquire();

                        //also we have to set the pace in the hash ap to false to indicate that the place is empty
                        mAvailableImportMutex.acquire();
                        mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain), false);
                        mAvailableImportMutex.release();

                        //after we have translate to the waiting place we have also tto release the import semaphore -_-
                        mImportSemaphore.release();
                        //acquire the table semaphore
                        mTableSemaphore.acquire();

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
                        if (tablePoint != null && waitingPoint != null) {
                            transitionAnimation.setFromX(waitingPoint.getX() - INITIAL_X);
                            transitionAnimation.setFromY(waitingPoint.getY() - INITIAL_Y);
                            transitionAnimation.setToX(tablePoint.getX() - INITIAL_X);
                            transitionAnimation.setToY(tablePoint.getY() - INITIAL_Y);
                        }
                        Platform.runLater(transitionAnimation::play);

                        //setting the value of the corresponding label to the mBook
                        int finalIndexInTheWaitingChairs2 = indexInTheWaitingChairs;
                        Platform.runLater(() -> mBooksLabelListWaiting.get(finalIndexInTheWaitingChairs2).setText(""));
                        extraSemaphore.acquire();

                        //setting the value of the corresponding label to the mBook
                        int finalIndexInTable = indexInTable;
                        Platform.runLater(() -> mBooksLabelList.get(finalIndexInTable).setText(book));

                        mTableCounterMutex.acquire();
                        mTableCounter++;
                        mTableCounterMutex.release();

                        //setting the place in waiting chairs  to false to indicate that is empty
                        mAvailableWaitingMutex.acquire();
                        mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs), false);
                        mAvailableWaitingMutex.release();

                        mWaitingCounterMutex.acquire();
                        mWaitingCounter--;
                        mWaitingCounterMutex.release();
                        mWaitingSemaphore.release();
                    } else {
                        //that means there is a place in the table
                        mTableSemaphore.acquire();

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
                        if (tablePoint != null && importPoint != null) {
                            transitionAnimation.setFromX(importPoint.getX() - INITIAL_X);
                            transitionAnimation.setFromY(importPoint.getY() - INITIAL_Y);
                            transitionAnimation.setToX(tablePoint.getX() - INITIAL_X);
                            transitionAnimation.setToY(tablePoint.getY() - INITIAL_Y);
                        }
                        Platform.runLater(() -> mBooksLabelImporting.get(finalIndexInImportChain).setText(""));
                        Platform.runLater(transitionAnimation::play);

                        //setting the value of the corresponding label to the mBook
                        int finalIndexInTable = indexInTable;
                        Platform.runLater(() -> mBooksLabelList.get(finalIndexInTable).setText(book));
                        extraSemaphore.acquire();

                        //changing the place to false to indicates tha it it empty
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

                    //searching the empty place in the Waiting chairs
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
                    if (importPoint != null && waitingPoint != null) {
                        transitionAnimation.setFromX(importPoint.getX() - INITIAL_X);
                        transitionAnimation.setFromY(importPoint.getY() - INITIAL_Y);
                        transitionAnimation.setToX(waitingPoint.getX() - INITIAL_X);
                        transitionAnimation.setToY(waitingPoint.getY() - INITIAL_Y);
                    }
                    Platform.runLater(() -> mBooksLabelImporting.get(finalIndexInImportChain).setText(""));
                    Platform.runLater(transitionAnimation::play);

                    //setting the value of the corresponding label to the mBook
                    int finalIndexInTheWaitingChairs = indexInTheWaitingChairs;
                    Platform.runLater(() -> mBooksLabelListWaiting.get(finalIndexInTheWaitingChairs).setText(book));
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
                    if (tablePoint != null && waitingPoint != null) {
                        transitionAnimation.setFromX(waitingPoint.getX() - INITIAL_X);
                        transitionAnimation.setFromY(waitingPoint.getY() - INITIAL_Y);
                        transitionAnimation.setToX(tablePoint.getX() - INITIAL_X);
                        transitionAnimation.setToY(tablePoint.getY() - INITIAL_Y);
                    }
                    Platform.runLater(transitionAnimation::play);
                    Platform.runLater(() -> mBooksLabelListWaiting.get(finalIndexInTheWaitingChairs).setText(""));
                    extraSemaphore.acquire();

                    //setting the value of the corresponding label to the mBook
                    int finalIndexInTable = indexInTable;
                    Platform.runLater(() -> mBooksLabelList.get(finalIndexInTable).setText(book));

                    mAvailableWaitingMutex.acquire();
                    mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs), false);
                    mAvailableWaitingMutex.release();

                    //decrementing the counter of the waiting people in the chair
                    mWaitingCounterMutex.acquire();
                    mWaitingCounter--;
                    mWaitingCounterMutex.release();
                    mWaitingSemaphore.release();
                }
            }


            Thread.sleep(DURATION_READING);

            /**
             * at this points the reader will leave the library , but before that he must report the mBook to the
             * return place, which gonna be accessed by another chain called the Outer Chain , this chain has 6
             * places, each of these must be in mutual exclusion -_-.
             */

            Point2D returnPoint = null;
            int indexInReturn = -1;

            //acquiring the Return semaphore
            mReturningSemaphore.acquire();

            //searching the empty place in the returning places
            mAvailableReturnMutex.acquire();
            for (int i = 0; i < mAvailableReturnPlaces.size(); i++) {
                if (!mAvailableReturnPlaces.get(CoordinatesProvider.getListOfReturningPlaces().get(i))) {
                    indexInReturn = i;
                    returnPoint = CoordinatesProvider.getListOfReturningPlaces().get(i);
                    mAvailableReturnPlaces.put(CoordinatesProvider.getListOfReturningPlaces().get(i), true);
                    break;
                }
            }
            mAvailableReturnMutex.release();

            //now we translate to the empty place
            if (tablePoint != null && returnPoint != null) {
                transitionAnimation.setFromX(tablePoint.getX() - INITIAL_X);
                transitionAnimation.setFromY(tablePoint.getY() - INITIAL_Y);
                transitionAnimation.setToX(returnPoint.getX() - INITIAL_X);
                transitionAnimation.setToY(returnPoint.getY() - INITIAL_Y);
            }
            Platform.runLater(transitionAnimation::play);

            //change the value of false in the available places to indicate that it is empty
            mAvailableTableMutex.acquire();
            int finalIndexInTable1 = indexInTable;
            Platform.runLater(() -> mBooksLabelList.get(finalIndexInTable1).setText(""));
            mAvailableTablePlaces.put(CoordinatesProvider.getListOfTablePlaces().get(indexInTable), false);
            mAvailableTableMutex.release();

            //decrementing the counter of table, so when a new one come can see the actual value
            mTableCounterMutex.acquire();
            mTableCounter--;
            mTableCounterMutex.release();
            extraSemaphore.acquire();

            //and release the Table Semaphores
            mTableSemaphore.release();

            //simulating the return time
            Thread.sleep(DURATION_RETURNING);

            /**
             * at this moment the Professor will se if there is another professor waiting
             * for the same book, if it is the case he will release the inner semaphore
             * for the book , otherwise , if there is no professor waiting and there is
             * more than one Professor, the outer will be released
             */

            mProfessorCounterMutex.acquire();
            mPriorityMapMutex.acquire();

            if (mProfessorCounterMap.get(book) == 0 && mPriorityMap.get(book)) {
                mPriorityBooksSemaphoresMap.get(book).release();
            }


            mPriorityMapMutex.release();
            mProfessorCounterMutex.release();
            mBooksSemaphoresMap.get(book).release();


            /**
             * finally we will go out throw the chain of course
             */

            mOutChainSemaphore.acquire();


            //now we have to translate to the first place in the out chain
            if (returnPoint != null) {
                transitionAnimation.setFromX(returnPoint.getX() - INITIAL_X);
                transitionAnimation.setFromY(returnPoint.getY() - INITIAL_Y);
                transitionAnimation.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(0).getX() - INITIAL_X);
                transitionAnimation.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(0).getY() - INITIAL_Y);
            }
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            mAvailableReturnMutex.acquire();
            mAvailableReturnPlaces.put(CoordinatesProvider.getListOfReturningPlaces().get(indexInReturn), false);
            mAvailableReturnMutex.release();

            mReturningSemaphore.release();

            transitionAnimation.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(0).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(0).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(1).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(1).getY() - INITIAL_Y);
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            mOutChainSemaphore.release();


            transitionAnimation.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(1).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(1).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(2).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(2).getY() - INITIAL_Y);
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            transitionAnimation.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(2).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(2).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(3).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(3).getY() - INITIAL_Y);
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            transitionAnimation.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(3).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(3).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(4).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(4).getY() - INITIAL_Y);
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            transitionAnimation.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(4).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(4).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(5).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(5).getY() - INITIAL_Y);
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            /**
             * and now the final transition out of the scene :D
             */

            transitionAnimation.setFromX(CoordinatesProvider.getListOfOutChainPlaces().get(5).getX() - INITIAL_X);
            transitionAnimation.setFromY(CoordinatesProvider.getListOfOutChainPlaces().get(5).getY() - INITIAL_Y);
            transitionAnimation.setToX(CoordinatesProvider.getListOfOutChainPlaces().get(6).getX() - INITIAL_X);
            transitionAnimation.setToY(CoordinatesProvider.getListOfOutChainPlaces().get(6).getY() - INITIAL_Y);
            Platform.runLater(transitionAnimation::play);
            extraSemaphore.acquire();

            /**
             * at these moment the Professor is out the scene, he finish reading and
             * he released all the semaphores that he used to do his cycle
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
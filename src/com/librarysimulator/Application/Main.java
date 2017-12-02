package com.librarysimulator.Application;


import com.librarysimulator.Providers.BooksProvider;
import com.librarysimulator.Providers.CoordinatesProvider;
import com.librarysimulator.Providers.ImagesProvider;
import com.librarysimulator.Utilities.ImagesUtilities;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Semaphore;


public class Main extends Application {

    /**
     * @definition this class is the main class of the simulator,
     * it extends the Application class from the JavaFX package
     * and set all hte GUI and the Interaction and also managing the
     * threads and the Mutual exclusion of the critical sections
     */

    //variable holding the current value of the choice boxes
    private String mCurrentChoiceProfessor, mCurrentChoiceStudent, mCurrentChoiceRandom;

    //a map that contains book and their semaphores
    private final HashMap<String, Semaphore> mBooksSemaphoresMap = new HashMap<>();

    //the root node for the application
    private AnchorPane mRoot;

    //
    private ImagesUtilities mImagesCreator = new ImagesUtilities();

    // a choice box for the mBooksObservableList
    private ChoiceBox<String> mBooksStudentChoiceBox, mBooksProfessorChoiceBox, mBooksRandomChoiceBox;
    private CheckBox mRandomBookCheckBox;

    // the list holding the box to use them in the views and other things
    private ObservableList<String> mBooksObservableList;

    //we need in total of 4 semaphores for the firsts 4 places
    private final Semaphore[] mChainSemaphoresArray = {
            new Semaphore(1),
            new Semaphore(1),
            new Semaphore(1),
            new Semaphore(1)
    };

    //a mChainSemaphore semaphores in which if new thread come in , can't execute and the mChainSemaphore is full
    private final Semaphore mChainSemaphore = new Semaphore(4);

    private final Semaphore mImportSemaphore = new Semaphore(5);

    private final Semaphore mEntrySemaphore = new Semaphore(1);

    private final Semaphore mCurrentBookSemaphore = new Semaphore(1);

    //hash maps to see the available position in the Import Chain, Table, waiting sits and The out Chain
    private final HashMap<Point2D, Boolean> mAvailableImportPlaces = new HashMap<>(),
            mAvailableWaitingPlaces = new HashMap<>(),
            mAvailableTablePlaces = new HashMap<>(),
            mAvailableOutPlaces = new HashMap<>();

    //mutex semaphore to protect These maps
    private Semaphore mAvailableImportMutex = new Semaphore(1),
            mAvailableTableMutex = new Semaphore(1),
            mAvailableWaitingMutex = new Semaphore(1),
            mAvailableOutMutex = new Semaphore(1);

    // counters to counts the number of places are occupied by the people in the waiting chairs and the Table
    private int mWaitingCounter = 0, mTableCounter = 0;

    // mutex semaphores to protect the counters
    private Semaphore mWaitingCounterMutex = new Semaphore(1), mTableCounterMutex = new Semaphore(1);


    //semaphores fro the Waiting and the tables places
    private Semaphore mTableSemaphore = new Semaphore(12), mWaitingSemaphore = new Semaphore(3);

    /**
     * the start method of the application
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {


        //creating a root AnchorPane node for the Scene
        mRoot = new AnchorPane();

        mBooksObservableList = FXCollections.observableArrayList(BooksProvider.getBooksList());

        //initialization of the mBooksObservableList map and their semaphores
        initBooksMap(1);

        //initialization of the hash maps which contains the Position of available places
        // in the Waiting , Import and Table places
        initHashMaps();

        //creating , setting and adding the choice boxes and the check box to the layout
        setupChoiceBoxesAndCheckBox();

        //setup the button and their specification in separate method
        setupButtons();

        //setup the static images and their specification n separate method
        setupAndAddingImages();

        //ImagesUtilities.peoplePreview(mImagesCreator,mRoot);

        //creating the scene
        Scene mainScene = new Scene(mRoot, 1100, 660);
        mainScene.getStylesheets().add("/res/Others/style.css");

        /*
         *setting the properties of the stage
         * the scene , the title and some other attribute
         */
        stage.setTitle("Library Simulator");

        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.show();

    }

    /**
     * a simple method that create images and their attributes
     * and adding them to the scene node
     *
     * @throws URISyntaxException
     */
    private void setupAndAddingImages() throws Exception {

        ImageView chairImage = new ImageView(
                new Image(getClass().getResource("/res/Images/Things/chair.png").toURI().toString())
        );

        chairImage.setLayoutX(816);
        chairImage.setLayoutY(43);
        chairImage.setPreserveRatio(true);
        chairImage.setFitHeight(99);
        chairImage.setFitWidth(258);

        ImageView queueEntryImage = new ImageView(
                new Image(getClass().getResource("/res/Images/Things/gishet_sortie.png").toURI().toString())
        );

        queueEntryImage.setLayoutX(97);
        queueEntryImage.setLayoutY(283);
        queueEntryImage.setPreserveRatio(true);
        queueEntryImage.setFitWidth(374);
        queueEntryImage.setFitHeight(501);

        ImageView tableImage = new ImageView(
                new Image(getClass().getResource("/res/Images/Things/table.png").toURI().toString())
        );

        tableImage.setFitHeight(170);
        tableImage.setFitWidth(371);
        tableImage.setPreserveRatio(true);
        tableImage.setLayoutX(644);
        tableImage.setLayoutY(388);

        ImageView queueTakeImage = new ImageView(
                new Image(getClass().getResource("/res/Images/Things/gishet_take.png").toURI().toString())
        );

        queueTakeImage.setLayoutX(358);
        queueTakeImage.setLayoutY(-12);
        queueTakeImage.setPreserveRatio(true);
        queueTakeImage.setFitWidth(368);
        queueTakeImage.setFitHeight(146);

        ArrayList<ImageView> imagesOfEmployees = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            imagesOfEmployees.add(
                    mImagesCreator.createImageViewOfEmployee(
                            ImagesProvider.getListOfEmployees().get(i),
                            CoordinatesProvider.getListOfEmployeesPlaces().get(i))
            );

        }

        mRoot.getChildren().addAll(imagesOfEmployees);


        mRoot.getChildren().addAll(chairImage,
                queueEntryImage, tableImage, queueTakeImage);
    }

    /**
     * method the abstract all the code to create and setting
     * the choice boxes and the check box
     */
    private void setupChoiceBoxesAndCheckBox() {
    /*
     * initializing the choices boxes with the Books
     */
        System.out.println(mBooksObservableList.size());
        mBooksStudentChoiceBox = new ChoiceBox<>(mBooksObservableList);
        mBooksProfessorChoiceBox = new ChoiceBox<>(mBooksObservableList);
        mBooksRandomChoiceBox = new ChoiceBox<>(mBooksObservableList);
        mRandomBookCheckBox = new CheckBox();

        //positioning the choice boxes
        mBooksStudentChoiceBox.setLayoutX(40);
        mBooksProfessorChoiceBox.setLayoutX(40);
        mBooksRandomChoiceBox.setLayoutX(40);
        mBooksStudentChoiceBox.setLayoutY(40);
        mBooksProfessorChoiceBox.setLayoutY(90);
        mBooksRandomChoiceBox.setLayoutY(140);

        //setting the height of each choice boxes
        mBooksStudentChoiceBox.setPrefHeight(29);
        mBooksProfessorChoiceBox.setPrefHeight(29);
        mBooksRandomChoiceBox.setPrefHeight(29);

        //choosing ht default value
        mBooksStudentChoiceBox.getSelectionModel().selectFirst();
        mCurrentChoiceStudent = mBooksObservableList.get(0);
        mBooksProfessorChoiceBox.getSelectionModel().select(1);
        mCurrentChoiceProfessor = mBooksObservableList.get(1);
        mBooksRandomChoiceBox.getSelectionModel().select(2);
        mCurrentChoiceRandom = mBooksObservableList.get(2);

        //check box properties
        mRandomBookCheckBox.setLayoutX(95);
        mRandomBookCheckBox.setLayoutY(145);
        mBooksRandomChoiceBox.setDisable(true);


        /*
         * adding some listeners on the ChoiceBoxes and the Check box
         *  and capture the value to another variables to use the later Inshallah
         */

        //get the value of the Professor choice box to a variable
        mBooksProfessorChoiceBox.getSelectionModel()
                .selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                mCurrentChoiceProfessor = mBooksObservableList.get(newValue.intValue()));

        //get the value of the Student choice box to a variable
        mBooksStudentChoiceBox.getSelectionModel()
                .selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                mCurrentChoiceStudent = mBooksObservableList.get(newValue.intValue()));

        //get the value of the random choice box to a variable
        mBooksRandomChoiceBox.getSelectionModel()
                .selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                mCurrentChoiceRandom = mBooksObservableList.get(newValue.intValue())
        );

        //listener on the check box to enable or disable the Random choice box
        mRandomBookCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                mBooksRandomChoiceBox.setDisable(!newValue));


        mRoot.getChildren().addAll(mBooksProfessorChoiceBox,
                mBooksStudentChoiceBox, mRandomBookCheckBox,
                mBooksRandomChoiceBox);
    }

    /**
     * method that setup the there button and their specification
     */
    private void setupButtons() {


        //instantiate buttons and add text to them
        Button mAddProfessorButton = new Button("Add Professor");
        Button mAddStudentButton = new Button("Add Student");
        Button mAddRandomButton = new Button("Add 5 Randomly");

        //special method to set buttons
        settingButtons(mAddProfessorButton);
        settingButtons(mAddStudentButton);
        settingButtons(mAddRandomButton);

        //setting position of each button
        mAddStudentButton.setLayoutY(40);
        mAddProfessorButton.setLayoutY(90);
        mAddRandomButton.setLayoutY(140);

        // the initial position X and Y
        double initialX = CoordinatesProvider.getInitialPoint().getX();
        double initialY = CoordinatesProvider.getInitialPoint().getY();


        mAddStudentButton.setOnMouseClicked(e -> {

            //the Thread in which all the cycle of the student will be in
            new Thread(() -> {
                //an inner semaphore that we will use to play animation one by one for each thread
                Semaphore extraSemaphore = new Semaphore(0);
                // acquiring the mChainSemaphore semaphores
                String book = null;
                try {

                    mCurrentBookSemaphore.acquire();
                    book = mCurrentChoiceStudent;
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
                    int indexInTheWaitingChairs= -1 ;

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
                            if(!mAvailableWaitingPlaces.get(CoordinatesProvider.getListOfWaitingPlaces().get(i))){
                                indexInTheWaitingChairs = i;
                                waitingPoint = CoordinatesProvider.getListOfWaitingPlaces().get(i);
                                mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(i),true);
                                break;
                            }
                        }
                        mAvailableWaitingMutex.release();

                        //translation to the empty place in the waiting chair
                        TranslateTransition transitionWaiting = new TranslateTransition(Duration.millis(1000),student);
                        transitionWaiting.setFromX(importPoint.getX() - initialX);
                        transitionWaiting.setFromY(importPoint.getY() - initialY);
                        transitionWaiting.setToX(waitingPoint.getX() - initialX);
                        transitionWaiting.setToY(waitingPoint.getY() - initialY);
                        transitionWaiting.setOnFinished(event -> extraSemaphore.release());

                        Platform.runLater(transitionWaiting::play);

                        extraSemaphore.acquire();
                        //also we have to set the pace in the hash ap to false to indicate that the place is empty
                        mAvailableImportMutex.acquire();
                        mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain),false);
                        mAvailableImportMutex.release();

                        //after we have translate to the waiting place we have also tto release the import semaphore -_-
                        mImportSemaphore.release();

                        mTableSemaphore.acquire();
                        //now the reader is waiting ih the waiting chairs
                        //todo translate to the empty place and decrement the mWaiting Counter

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

//                    //increment the Value of the people who are sitting in the table
//                    mTableCounterMutex.acquire();
//                    mTableCounter++;
//                    mTableCounterMutex.release();

                        Platform.runLater(transitionToTable::play);
                        extraSemaphore.acquire();

                        mTableCounterMutex.acquire();
                        mTableCounter++;
                        mTableCounterMutex.release();

                        //we have to change the state of the actual place to false, to indicate that it is empty
                        mAvailableWaitingMutex.acquire();
                        mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs),false);
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

                                //TODO: translate to the empty place in the waiting chair
                                //translate to the empty place in the Waiting chairs
                                mAvailableWaitingMutex.acquire();
                                for (int i = 0; i < mAvailableWaitingPlaces.size(); i++) {
                                    if(!mAvailableWaitingPlaces.get(CoordinatesProvider.getListOfWaitingPlaces().get(i))){
                                        indexInTheWaitingChairs = i;
                                        waitingPoint = CoordinatesProvider.getListOfWaitingPlaces().get(i);
                                        mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(i),true);
                                        break;
                                    }
                                }
                                mAvailableWaitingMutex.release();

                                //translation to the empty place in the waiting chair
                                TranslateTransition transitionWaiting = new TranslateTransition(Duration.millis(1000),student);
                                transitionWaiting.setFromX(importPoint.getX() - initialX);
                                transitionWaiting.setFromY(importPoint.getY() - initialY);
                                transitionWaiting.setToX(waitingPoint.getX() - initialX);
                                transitionWaiting.setToY(waitingPoint.getY() - initialY);
                                transitionWaiting.setOnFinished(event -> extraSemaphore.release());

                                Platform.runLater(transitionWaiting::play);

                                extraSemaphore.acquire();
                                //also we have to set the pace in the hash ap to false to indicate that the place is empty
                                mAvailableImportMutex.acquire();
                                mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain),false);
                                mAvailableImportMutex.release();

                                //after we have translate to the waiting place we have also tto release the import semaphore -_-
                                mImportSemaphore.release();
                                //acquire the table semaphore
                                mTableSemaphore.acquire();

                                //now the reader is waiting ih the waiting chairs
                                //todo translate to the empty place and decrement the mWaiting Counter

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

//                    //increment the Value of the people who are sitting in the table
//                    mTableCounterMutex.acquire();
//                    mTableCounter++;
//                    mTableCounterMutex.release();

                                Platform.runLater(transitionToTable::play);
                                extraSemaphore.acquire();

                                mTableCounterMutex.acquire();
                                mTableCounter++;
                                mTableCounterMutex.release();

                                mAvailableWaitingMutex.acquire();
                                mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs),false);
                                mAvailableWaitingMutex.release();

                                //translate to the place
                                mWaitingCounterMutex.acquire();
                                mWaitingCounter--;
                                mWaitingCounterMutex.release();
                                mWaitingSemaphore.release();
                            } else {
                                //that means there is a place in the table
                                mTableSemaphore.acquire();
                                //TODO translate to the empty place in the Table
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

//                    //increment the Value of the people who are sitting in the table
//                    mTableCounterMutex.acquire();
//                    mTableCounter++;
//                    mTableCounterMutex.release();

                                Platform.runLater(transitionToTable::play);
                                extraSemaphore.acquire();
                                mAvailableImportMutex.acquire();
                                mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain),false);
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
                            //Todo: translate to the empty place in the waiting table
                            //translate to the empty place in the Waiting chairs
                            mAvailableWaitingMutex.acquire();
                            for (int i = 0; i < mAvailableWaitingPlaces.size(); i++) {
                                if(!mAvailableWaitingPlaces.get(CoordinatesProvider.getListOfWaitingPlaces().get(i))){
                                    indexInTheWaitingChairs = i;
                                    waitingPoint = CoordinatesProvider.getListOfWaitingPlaces().get(i);
                                    mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(i),true);
                                    break;
                                }
                            }
                            mAvailableWaitingMutex.release();

                            //translation to the empty place in the waiting chair
                            TranslateTransition transitionWaiting = new TranslateTransition(Duration.millis(1000),student);
                            transitionWaiting.setFromX(importPoint.getX() - initialX);
                            transitionWaiting.setFromY(importPoint.getY() - initialY);
                            transitionWaiting.setToX(waitingPoint.getX() - initialX);
                            transitionWaiting.setToY(waitingPoint.getY() - initialY);
                            transitionWaiting.setOnFinished(event -> extraSemaphore.release());

                            Platform.runLater(transitionWaiting::play);

                            extraSemaphore.acquire();
                            //also we have to set the pace in the hash ap to false to indicate that the place is empty
                            mAvailableImportMutex.acquire();
                            mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain),false);
                            mAvailableImportMutex.release();

                            //after we have translate to the waiting place we have also tto release the import semaphore -_-
                            mImportSemaphore.release();


                            mTableSemaphore.acquire();

                            mTableCounterMutex.acquire();
                            mTableCounter++;
                            mTableCounterMutex.release();

                            //now the reader is waiting ih the waiting chairs
                            //todo translate to the empty place and decrement the mWaiting Counter

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

//                    //increment the Value of the people who are sitting in the table
//                    mTableCounterMutex.acquire();
//                    mTableCounter++;
//                    mTableCounterMutex.release();

                            Platform.runLater(transitionToTable::play);
                            extraSemaphore.acquire();

                            mAvailableWaitingMutex.acquire();
                            mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(indexInTheWaitingChairs),false);
                            mAvailableWaitingMutex.release();

                            //translate to the place
                            mWaitingCounterMutex.acquire();
                            mWaitingCounter--;
                            mWaitingCounterMutex.release();
                            mWaitingSemaphore.release();
                        }
                    }



                    Thread.sleep(5000);



                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }).start();

        });


        mAddProfessorButton.setOnMouseClicked(e -> {

        });

        mAddRandomButton.setOnMouseClicked(event -> {

        });

        //adding button to the root node
        mRoot.getChildren().addAll(mAddStudentButton, mAddProfessorButton, mAddRandomButton);
    }

    //
    private void initHashMaps() {
        //initialization of the hash map with th importing places and the boolean of false
        for (int i = 0; i < CoordinatesProvider.getListOfImportPlaces().size(); i++) {
            mAvailableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(i), false);
        }

        //initialization of the hash map with the table places and the boolean of false
        for (int i = 0; i < CoordinatesProvider.getListOfTablePlaces().size(); i++) {
            mAvailableTablePlaces.put(CoordinatesProvider.getListOfTablePlaces().get(i), false);
        }

        //initialization of the hash map with the waiting chair places and the boolean of false
        for (int i = 0; i < CoordinatesProvider.getListOfWaitingPlaces().size(); i++) {
            mAvailableWaitingPlaces.put(CoordinatesProvider.getListOfWaitingPlaces().get(i), false);
        }

        //initialization of the hash map with the out chain places and the boolean of false
        for (int i = 0; i < CoordinatesProvider.getListOfReturnChainPlaces().size(); i++) {
            mAvailableOutPlaces.put(CoordinatesProvider.getListOfReturnChainPlaces().get(i), false);
        }
    }

    /**
     * method to set button with the same attributes
     *
     * @param button
     */
    private void settingButtons(Button button) {
        button.setPrefSize(119, 29);
        button.setId("AddButtons");
        button.setLayoutX(125);
    }

    /**
     * method that initialize the mBooksObservableList map and their semaphore
     *
     * @throws Exception
     */
    private void initBooksMap(int semaphoreValue) {
        for (String book : mBooksObservableList) {
            mBooksSemaphoresMap.put(book, new Semaphore(semaphoreValue));
        }
    }

    /**
     * the launcher method
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }


}
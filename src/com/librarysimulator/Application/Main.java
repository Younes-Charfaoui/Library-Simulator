package com.librarysimulator.Application;

import com.librarysimulator.Models.Professor;
import com.librarysimulator.Models.Student;
import com.librarysimulator.Providers.BooksProvider;
import com.librarysimulator.Providers.CoordinatesProvider;
import com.librarysimulator.Providers.ImagesProvider;
import com.librarysimulator.Utilities.ImagesUtilities;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class Main extends Application {

    /**
     * @definition this class is the main class of the simulator,
     * it extends the Application class from the JavaFX package
     * and set all the GUI and the Interaction and also starting the threads
     * for the student or Professor or starting 5 randomly
     */

    //an constant for the duration of the reading ,animation and others
    public static final int DURATION_READING = 5000;
    public static final int DURATION_ANIMATION = 1000;
    public static final int DURATION_IMPORTING = 1000;
    public static final int DURATION_RETURNING = 1000;

    //a hash map for storing the label and their position relatively to position of student
    public static final List<Label> mBooksLabelList = new ArrayList<>();
    public static final List<Label> mBooksLabelListWaiting = new ArrayList<>();

    //number of copies in each book
    private static final int NUMBER_OF_BOOKS = 1;

    //variable holding the current value of the choice boxes
    private String mCurrentChoiceProfessor, mCurrentChoiceStudent, mCurrentChoiceRandom;

    //a map that contains book and their semaphores
    public static final HashMap<String, Semaphore> mBooksSemaphoresMap = new HashMap<>(),
            mPriorityBooksSemaphoresMap = new HashMap<>();

    //hash maps for holding the number of students and professors there are waiting for each book
    public static final HashMap<String, Integer> mStudentCounterMap = new HashMap<>(),
            mProfessorCounterMap = new HashMap<>();

    //the root node for the application
    public static AnchorPane mRoot;

    //an instance of the imageUtility to create ImageViews
    private final ImagesUtilities mImagesCreator = new ImagesUtilities();

    //the list holding the books to use them in the views and other things
    private ObservableList<String> mBooksObservableList;

    //we need in total of 4 semaphores for the firsts 4 places
    public static final List<Semaphore> mChainSemaphoresList = new ArrayList<>();

    //we need in total of 6 semaphores for the finals out 6 places
    public static final List<Semaphore> mOutChainSemaphoresList = new ArrayList<>();

    //semaphores for protecting the counter of the waiting student in each books
    public static final Semaphore mStudentCounterMutex = new Semaphore(1),
            mProfessorCounterMutex = new Semaphore(1);

    //a semaphore for the entry place
    public static final Semaphore mEntrySemaphore = new Semaphore(1);

    // a semaphore for getting the user book mutually
    public static final Semaphore mCurrentBookSemaphore = new Semaphore(1);

    //hash maps to see the available position in the Import Chain, Table, waiting sits and The out Chain
    public static final HashMap<Point2D, Boolean> mAvailableImportPlaces = new HashMap<>(),
            mAvailableWaitingPlaces = new HashMap<>(),
            mAvailableTablePlaces = new HashMap<>(),
            mAvailableReturnPlaces = new HashMap<>();

    // counters to counts the number of places are occupied by the people in the waiting chairs and the Table
    public static int mWaitingCounter = 0, mTableCounter = 0;

    // mutex semaphores to protect the counters
    public static final Semaphore mWaitingCounterMutex = new Semaphore(1),
            mTableCounterMutex = new Semaphore(1),
            mAvailableImportMutex = new Semaphore(1),
            mAvailableTableMutex = new Semaphore(1),
            mAvailableWaitingMutex = new Semaphore(1),
            mAvailableReturnMutex = new Semaphore(1);

    //semaphores for for the main places: chain , import, Table , waiting chairs and out chain
    public static final Semaphore mTableSemaphore = new Semaphore(12),
            mWaitingSemaphore = new Semaphore(3),
            mReturningSemaphore = new Semaphore(2),
            mChainSemaphore = new Semaphore(4),
            mImportSemaphore = new Semaphore(5);

    //a check box for the random button to add student with same book or not
    private static CheckBox mRandomBookCheckBox = new CheckBox();

    /**
     * the start method of the application , it must be include ofr using JavaFX
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        //creating a root AnchorPane node for the Scene
        mRoot = new AnchorPane();

        //the observable array list that holding the books
        mBooksObservableList = FXCollections.observableArrayList(BooksProvider.getBooksList());

        //initialising the list of the chain semaphores
        initChainAndWaitingSemaphores();

        //initialization of the mBooksObservableList map and their semaphores
        initBooksMap(NUMBER_OF_BOOKS);

        //initialization of the hash maps which contains the Position of available places
        // in the Waiting , Import and Table places
        initHashMaps();

        //initializing the label position and their specifications
        initLabelMap();

        //creating , setting and adding the choice boxes and the check box to the layout
        setupChoiceBoxesAndCheckBox();

        //setup the button and their specification in separate method
        setupButtons();

        //setup the static images and their specification in separate method
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
     * method for initializing the labels and their specification
     */
    private void initLabelMap() {

        for (int i = 0; i < CoordinatesProvider.getListOfLabelCoordinates().size(); i++) {
            Label label = new Label();
            label.setPrefSize(77, 20);
            label.setAlignment(Pos.CENTER);
            label.setLayoutX(CoordinatesProvider.getListOfLabelCoordinates().get(i).getX());
            label.setLayoutY(CoordinatesProvider.getListOfLabelCoordinates().get(i).getY());
            mBooksLabelList.add(label);
        }

        for (int i = 0; i < CoordinatesProvider.getListOfLabelCoordinatesChairs().size(); i++) {
            Label label = new Label();
            label.setPrefSize(77, 20);
            label.setAlignment(Pos.CENTER);

            label.setLayoutX(CoordinatesProvider.getListOfLabelCoordinatesChairs().get(i).getX());
            label.setLayoutY(CoordinatesProvider.getListOfLabelCoordinatesChairs().get(i).getY());
            mBooksLabelListWaiting.add(label);
        }

        mRoot.getChildren().addAll(mBooksLabelList);
        mRoot.getChildren().addAll(mBooksLabelListWaiting);

    }

    /**
     * method for simple initialization of the list of semaphores
     */
    private void initChainAndWaitingSemaphores() {

        //initialising the list of the chain semaphores
        for (int i = 0; i < 4; i++) {
            mChainSemaphoresList.add(new Semaphore(1));
        }

        for (int i = 0; i < 6; i++) {
            mOutChainSemaphoresList.add(new Semaphore(1));
        }
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
        ChoiceBox<String> mBooksStudentChoiceBox = new ChoiceBox<>(mBooksObservableList);
        ChoiceBox<String> mBooksProfessorChoiceBox = new ChoiceBox<>(mBooksObservableList);
        ChoiceBox<String> mBooksRandomChoiceBox = new ChoiceBox<>(mBooksObservableList);
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

        mAddStudentButton.setOnMouseClicked(e -> {

            //after clicking with the mouse a new Student Thread will be started
            try {
                mCurrentBookSemaphore.acquire();
                new Student(mCurrentChoiceStudent).start();
                mCurrentBookSemaphore.release();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        });

        mAddProfessorButton.setOnMouseClicked(e -> {

            try {
                mCurrentBookSemaphore.acquire();
                new Professor(mCurrentChoiceProfessor).start();
                mCurrentBookSemaphore.release();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        });

        mAddRandomButton.setOnMouseClicked(event -> {
            //if the check box in of the random book is checked we take the value of the book from there
            if (mRandomBookCheckBox.isSelected()) {
                try {
                    mCurrentBookSemaphore.acquire();
                    new Student(mCurrentChoiceRandom).start();
                    new Student(mCurrentChoiceRandom).start();
                    new Student(mCurrentChoiceRandom).start();
                    new Professor(mCurrentChoiceRandom).start();
                    new Professor(mCurrentChoiceRandom).start();
                    mCurrentBookSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

                //else we give them randomly books to read
                try {
                    mCurrentBookSemaphore.acquire();
                    new Student(mBooksObservableList.get(new Random().nextInt(mBooksObservableList.size()))).start();
                    new Student(mBooksObservableList.get(new Random().nextInt(mBooksObservableList.size()))).start();
                    new Student(mBooksObservableList.get(new Random().nextInt(mBooksObservableList.size()))).start();
                    new Professor(mBooksObservableList.get(new Random().nextInt(mBooksObservableList.size()))).start();
                    new Professor(mBooksObservableList.get(new Random().nextInt(mBooksObservableList.size()))).start();
                    mCurrentBookSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        });

        //adding button to the root node
        mRoot.getChildren().addAll(mAddStudentButton, mAddProfessorButton, mAddRandomButton);
    }

    //initialization of hash map for the available places in different positions
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
        for (int i = 0; i < CoordinatesProvider.getListOfReturningPlaces().size(); i++) {
            mAvailableReturnPlaces.put(CoordinatesProvider.getListOfReturningPlaces().get(i), false);
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
            mPriorityBooksSemaphoresMap.put(book, new Semaphore(semaphoreValue));
            mStudentCounterMap.put(book, 0);
            mProfessorCounterMap.put(book, 0);
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
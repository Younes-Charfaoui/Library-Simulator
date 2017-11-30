package com.librarysimulator.Application;


import com.librarysimulator.Utilities.BooksProvider;
import com.librarysimulator.Utilities.CoordinatesProvider;
import com.librarysimulator.Utilities.ImagesProvider;
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
    private HashMap<String, Semaphore> booksMap = new HashMap<>();

    //the root node for the application
    private AnchorPane mRoot;

    // a choice box for the books
    private ChoiceBox<String> mBooksStudentChoiceBox, mBooksProfessorChoiceBox, mBooksRandomChoiceBox;
    private CheckBox mRandomBookCheckBox;

    // the list holding the box to use them in the views and other things
    private ObservableList<String> books;

    //we need in total of 4 semaphores for the firsts 4 places
    private Semaphore[] chainSemaphores = {
            new Semaphore(1),
            new Semaphore(1),
            new Semaphore(1),
            new Semaphore(1)
    };

    //a chain semaphores in which if new thread come in , can't execute and the chain is full
    private Semaphore chain = new Semaphore(4);

    private Semaphore imports = new Semaphore(5);

    private Semaphore entrySemaphore = new Semaphore(1);

    //a hash map to see the available position in the Import Chain
    private HashMap<Point2D, Boolean> availableImportPlaces = new HashMap<>();

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

        books = FXCollections.observableArrayList(BooksProvider.getBooksList());

        //initialization of the books map and their semaphores
        initBooksMap(1);

        //creating , setting and adding the choice boxes and the check box to the layout
        setupChoiceBoxesAndCheckBox();

        //setup the button and their specification in separate method
        setupButtons();

        //setup the static images and their specification n separate method
        setupAndAddingImages();

        //peoplePreview();

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
                    createImageViewOfEmployee(
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
        mBooksStudentChoiceBox = new ChoiceBox<>(books);
        mBooksProfessorChoiceBox = new ChoiceBox<>(books);
        mBooksRandomChoiceBox = new ChoiceBox<>(books);
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
        mBooksProfessorChoiceBox.getSelectionModel().select(1);
        mBooksRandomChoiceBox.getSelectionModel().select(2);


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
                mCurrentChoiceProfessor = books.get(newValue.intValue()));

        //get the value of the Student choice box to a variable
        mBooksStudentChoiceBox.getSelectionModel()
                .selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                mCurrentChoiceStudent = books.get(newValue.intValue()));

        //get the value of the random choice box to a variable
        mBooksRandomChoiceBox.getSelectionModel()
                .selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                mCurrentChoiceRandom = books.get(newValue.intValue())
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

        //initialization of the hash map with th importing places with the boolean of false
        for (int i = 0; i < CoordinatesProvider.getListOfImportPlaces().size(); i++) {
            availableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(i), false);
        }


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


        double initialX = CoordinatesProvider.getInitialPoint().getX();
        double initialY = CoordinatesProvider.getInitialPoint().getY();


        mAddStudentButton.setOnMouseClicked(e -> {

            //the Thread in which all the cycle of the student will be in

            new Thread(() -> {
                //an inner semaphore that we will use to play animation one by one for each thread
                Semaphore extraSemaphore = new Semaphore(0);

                // acquiring the chain semaphores
                try {
                    chain.acquire();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                ImageView student = null;
                try {

                    //a random number generator to generate numbers for choosing a student image
                    Random random = new Random();

                    //creating a new Student and setting it's parameters and adding it to the scene
                    String imageName = String.valueOf(random.nextInt(16) + 1) + ".png";
                    student = createImageViewOfStudent(imageName);

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
                    chainSemaphores[0].acquire();
                    //then if we get access we can translate throw it in The main Thread
                    Platform.runLater(transitionZero::play);
                    //waiting the animation to stop
                    extraSemaphore.acquire();

                    //getting access to the second place by its semaphore
                    chainSemaphores[1].acquire();
                    //then if we get access we can translate throw it in The main Thread
                    Platform.runLater(transitionOne::play);
                    //waiting the animation to stop
                    extraSemaphore.acquire();
                    //now we can let someone access the first places because we are in the second place
                    chainSemaphores[0].release();

                    //getting access to the third place by its semaphore
                    chainSemaphores[2].acquire();
                    //then if we get access we can translate throw it in The main Thread
                    Platform.runLater(transitionTwo::play);
                    //waiting the animation to stop
                    extraSemaphore.acquire();
                    //now we can let someone access the second places because we are in the third place
                    chainSemaphores[1].release();

                    //getting access to the final chain place by its semaphore
                    chainSemaphores[3].acquire();
                    //then if we get access we can translate throw it in The main Thread
                    Platform.runLater(transitionThree::play);
                    //waiting the animation to stop
                    extraSemaphore.acquire();
                    //now we can let someone access the third places because we are in the final place
                    chainSemaphores[2].release();


                    /**
                     * we are now in position to go to the importing place by the entry point
                     * so we will first acquire the imports semaphore which have the permits of 5
                     * then we will also have to acquire the entry semaphore for the intermediary place
                     * and in that moment we will calculate the first empty place in the import chain
                     * and we know that there is a place because we have access to it by the import semaphore
                     * which have the permits of 5, then for the map which holding the empty places with their coordinate
                     * we are using it mutually because of the entry semaphore
                     */


                    //acquiring the import semaphore which contains 5 permits
                    imports.acquire();

                    //acquiring the entry semaphore which have permits for only one
                    entrySemaphore.acquire();

                    // an int to save the position of the ImageView in the ImportChain after translate To it
                    int indexInImportChain;

                    //getting the place empty in the import place
                    Point2D importP = null;
                    for (int i = 0; i < availableImportPlaces.size(); i++) {
                        if (!availableImportPlaces.get(CoordinatesProvider.getListOfImportPlaces().get(i))) {
                            indexInImportChain = i;
                            importP = CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain);
                            availableImportPlaces.put(CoordinatesProvider.getListOfImportPlaces().get(indexInImportChain), true);
                            break;
                        }
                    }

                    //creating the translation from the chain to import via a place know as Entry points
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
                    if (importP != null) {
                        transitionImport.setToX(importP.getX() - initialX);
                        transitionImport.setToY(importP.getY() - initialY);
                    }
                    transitionImport.setOnFinished(event -> extraSemaphore.release());

                    //then we can translate to the entry point safely in the Main Thread
                    Platform.runLater(transitionEntry::play);
                    extraSemaphore.acquire();

                    //realising the Position number 4
                    chainSemaphores[3].release();


                    //finally we can translate to the import place with the indexInImportChain
                    Platform.runLater(transitionImport::play);
                    extraSemaphore.acquire();

                    //realising the chain semaphore for letting someone out to enter in the chain
                    chain.release();

                    //realising the entry semaphore for letting the other to access the import chain if there is  place
                    entrySemaphore.release();

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
     * this method have the role of creating an imageView of an Student
     * and returning it back with some specification
     *
     * @param nameOfImage
     * @return ImageView
     * @throws Exception
     */
    private ImageView createImageViewOfStudent(String nameOfImage, Point2D p) throws Exception {
        Image image = new Image(getClass().getResource("/res/Images/Student/" + nameOfImage).toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);
        imageView.setFitHeight(64);
        imageView.setPreserveRatio(true);
        imageView.setLayoutX(p.getX());
        imageView.setLayoutY(p.getY());
        return imageView;
    }

    /**
     * this method have the role of creating an imageView of an Student
     * and returning it back with some specification
     *
     * @param nameOfImage
     * @return ImageView
     * @throws Exception
     */
    private ImageView createImageViewOfStudent(String nameOfImage) throws Exception {
        Image image = new Image(getClass().getResource("/res/Images/Student/" + nameOfImage).toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);
        imageView.setFitHeight(64);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * this method have the role of creating an imageView of an Employees
     * and returning it back with some specification
     *
     * @param nameOfImage
     * @param point2D
     * @return ImageView
     * @throws Exception
     */
    private ImageView createImageViewOfEmployee(String nameOfImage, Point2D point2D) throws Exception {
        Image image = new Image(getClass().getResource("/res/Images/Employee/" + nameOfImage).toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(53);
        imageView.setFitHeight(82);
        imageView.setLayoutX(point2D.getX());
        imageView.setLayoutY(point2D.getY());
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * this method have the role of creating an imageView of an Employees
     * and returning it back with some specification
     *
     * @param nameOfImage
     * @return ImageView
     * @throws Exception
     */
    private ImageView createImageViewOfEmployee(String nameOfImage) throws Exception {
        Image image = new Image(getClass().getResource("/res/Images/Employee/" + nameOfImage).toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(53);
        imageView.setFitHeight(82);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * this method have the role of creating an imageView of an Professor
     * and returning it back with some specification
     *
     * @param nameOfImage
     * @return ImageView
     * @throws Exception
     */
    private ImageView createImageViewOfProfessor(String nameOfImage, Point2D point) throws Exception {
        Image image = new Image(getClass().getResource("/res/Images/Professor/" + nameOfImage).toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(52);
        imageView.setFitHeight(69);
        imageView.setPreserveRatio(true);
        imageView.setLayoutX(point.getX());
        imageView.setLayoutY(point.getY());
        return imageView;
    }

    /**
     * this method have the role of creating an imageView of an Professor
     * and returning it back with some specification
     *
     * @param nameOfImage
     * @return ImageView
     * @throws Exception
     */
    private ImageView createImageViewOfProfessor(String nameOfImage) throws Exception {
        Image image = new Image(getClass().getResource("/res/Images/Professor/" + nameOfImage).toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(52);
        imageView.setFitHeight(69);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    /**
     * method that initialize the books map and their semaphore
     *
     * @throws Exception
     */
    private void initBooksMap(int semaphoreValue) {
        for (String book : books) {
            booksMap.put(book, new Semaphore(semaphoreValue));
        }
    }

    //a helper method to display a preview of the complete scene
    private void peoplePreview() throws Exception {
        ArrayList<ImageView> imageWaiting = new ArrayList<>();
        ArrayList<ImageView> imageOut = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            imageWaiting.add(
                    createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfWaitingPlaces().get(i))
            );
        }

        for (int i = 0; i < 5; i++) {
            imageOut.add(
                    createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfImportPlaces().get(i))
            );
        }
        ArrayList<ImageView> chain = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            chain.add(
                    createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfChainPlaces().get(i))
            );
        }

        ArrayList<ImageView> out = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            out.add(
                    createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfReturnChainPlaces().get(i))
            );
        }
        mRoot.getChildren().addAll(out);

        ArrayList<ImageView> table = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            table.add(
                    createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfTablePlaces().get(i))
            );
        }
        mRoot.getChildren().addAll(table);


        mRoot.getChildren().addAll(imageWaiting);
        mRoot.getChildren().addAll(imageOut);
        mRoot.getChildren().addAll(chain);
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
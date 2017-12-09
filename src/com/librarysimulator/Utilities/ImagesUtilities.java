package com.librarysimulator.Utilities;

import com.librarysimulator.Providers.CoordinatesProvider;
import com.librarysimulator.Providers.ImagesProvider;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public  class ImagesUtilities {

    /**
     * @definition this class have the methods
     * to create ImageViews for different kind of type: such as student, employees and
     * professor nad return them as Image Views.
     */

    //default constructor
    public ImagesUtilities() {
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
    public ImageView createImageViewOfStudent(String nameOfImage) throws Exception {
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
    public ImageView createImageViewOfEmployee(String nameOfImage, Point2D point2D) throws Exception {
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
    public ImageView createImageViewOfEmployee(String nameOfImage) throws Exception {
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
    public ImageView createImageViewOfProfessor(String nameOfImage, Point2D point) throws Exception {
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
    public ImageView createImageViewOfProfessor(String nameOfImage) throws Exception {
        Image image = new Image(getClass().getResource("/res/Images/Professor/" + nameOfImage).toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(52);
        imageView.setFitHeight(69);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    //a helper method to display a preview of the complete scene
    public static void peoplePreview(ImagesUtilities imageCreator , AnchorPane root) throws Exception {
        ArrayList<ImageView> imageOut = new ArrayList<>();
        ArrayList<ImageView> out = new ArrayList<>();
        ArrayList<ImageView> imageWaiting = new ArrayList<>();
        ArrayList<ImageView> table = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            imageWaiting.add(
                    imageCreator.createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfWaitingPlaces().get(i))
            );
        }

        for (int i = 0; i < 5; i++) {
            imageOut.add(
                    imageCreator.createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfImportPlaces().get(i))
            );
        }
        ArrayList<ImageView> chain = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            chain.add(
                    imageCreator.createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfChainPlaces().get(i))
            );
        }

        for (int i = 0; i < 6; i++) {
            out.add(
                    imageCreator.createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfOutChainPlaces().get(i))
            );
        }
        root.getChildren().addAll(out);

        for (int i = 0; i < 12; i++) {
            table.add(
                    imageCreator.createImageViewOfStudent(
                            ImagesProvider.getListOfStudent().get(i),
                            CoordinatesProvider.getListOfTablePlaces().get(i))
            );
        }
        root.getChildren().addAll(table);
        root.getChildren().addAll(imageWaiting);
        root.getChildren().addAll(imageOut);
        root.getChildren().addAll(chain);
    }
}
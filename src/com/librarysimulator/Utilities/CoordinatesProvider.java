package com.librarysimulator.Utilities;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CoordinatesProvider {

    /**
     * @definition this class have the role of providing most of important
     * points in the scene with X and Y coordinate to to several animations
     */

    private static final Point2D INITIAL_POINT = new Point2D(23, 680);
    private static final Point2D ENTRY_POINT = new Point2D(23, 200);
    private static final Point2D BREAK_OUT_POINT = new Point2D(163, 323);
    private static final Point2D OUT_POINT = new Point2D(500, 323);


    // list of all places in the queue of the start with X and Y coordinates
    private static List<Point2D> listOfChainPlaces = new ArrayList<>();

    // list of all places in the return chain of the books with X and Y coordinates
    private static List<Point2D> listOfReturnChainPlaces = new ArrayList<>();

    // list of all places in the importing queue with X and Y coordinates
    private static List<Point2D> listOfImportPlaces = new ArrayList<>();

    // list of all places in the table with X and Y coordinates
    private static List<Point2D> listOfTablePlaces = new ArrayList<>();

    // list of all places in the waiting chairs with X and Y coordinates
    private static List<Point2D> listOfWaitingPlaces = new ArrayList<>();

    //list of all places of the Employees
    private static List<Point2D> listOfEmployeesPlaces = new ArrayList<>();

    /*
     * initialization of the lists
     */
    static {
        listOfEmployeesPlaces.add(new Point2D(393,16+4));
        listOfEmployeesPlaces.add(new Point2D(455,16+4));
        listOfEmployeesPlaces.add(new Point2D(516,10+4));
        listOfEmployeesPlaces.add(new Point2D(578,16+4));
        listOfEmployeesPlaces.add(new Point2D(640,16+4));
        listOfEmployeesPlaces.add(new Point2D(280,452));
        listOfEmployeesPlaces.add(new Point2D(280, 556));

        listOfImportPlaces.add(new Point2D(400,150));
        listOfImportPlaces.add(new Point2D(463,150));
        listOfImportPlaces.add(new Point2D(519,150));
        listOfImportPlaces.add(new Point2D(587,150));
        listOfImportPlaces.add(new Point2D(655,150));

        listOfWaitingPlaces.add(new Point2D(845,57));
        listOfWaitingPlaces.add(new Point2D(927,57));
        listOfWaitingPlaces.add(new Point2D(1006,57));

        listOfChainPlaces.add(new Point2D(23,303));
        listOfChainPlaces.add(new Point2D(23,393));
        listOfChainPlaces.add(new Point2D(23,481));
        listOfChainPlaces.add(new Point2D(23,571));

        listOfReturnChainPlaces.add(new Point2D(163,565));
        listOfReturnChainPlaces.add(new Point2D(163,461));
        listOfReturnChainPlaces.add(new Point2D(163,358));
        listOfReturnChainPlaces.add(new Point2D(245,323));
        listOfReturnChainPlaces.add(new Point2D(330,323));
        listOfReturnChainPlaces.add(new Point2D(415,323));

        listOfTablePlaces.add(new Point2D(587,401));
        listOfTablePlaces.add(new Point2D(587,483));
        listOfTablePlaces.add(new Point2D(700,571));
        listOfTablePlaces.add(new Point2D(774,571));
        listOfTablePlaces.add(new Point2D(849,571));
        listOfTablePlaces.add(new Point2D(921,571));
        listOfTablePlaces.add(new Point2D(1032,483));
        listOfTablePlaces.add(new Point2D(1032,387));
        listOfTablePlaces.add(new Point2D(700,310));
        listOfTablePlaces.add(new Point2D(786,310));
        listOfTablePlaces.add(new Point2D(846,310));
        listOfTablePlaces.add(new Point2D(920,310));
    }

    /**
     * getters of lists and variables in this class
     * @return
     */

    public static List<Point2D> getListOfWaitingPlaces() {
        return listOfWaitingPlaces;
    }

    public static List<Point2D> getListOfEmployeesPlaces() {
        return listOfEmployeesPlaces;
    }

    public static List<Point2D> getListOfChainPlaces() {
        return listOfChainPlaces;
    }

    public static List<Point2D> getListOfReturnChainPlaces() {
        return listOfReturnChainPlaces;
    }

    public static List<Point2D> getListOfImportPlaces() {
        return listOfImportPlaces;
    }

    public static List<Point2D> getListOfTablePlaces() {
        return listOfTablePlaces;
    }

    public static Point2D getInitialPoint() {
        return INITIAL_POINT;
    }

    public static Point2D getEntryPoint() {
        return ENTRY_POINT;
    }

    public static Point2D getOutPoint() {
        return OUT_POINT;
    }

    public static Point2D getBreakOutPoint() {
        return BREAK_OUT_POINT;
    }
}

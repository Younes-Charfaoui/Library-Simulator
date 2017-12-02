package com.librarysimulator.Providers;


import java.util.ArrayList;
import java.util.List;

public final class ImagesProvider {

    /**
     * @definition
     *
     * this class is providing the names of the images in
     * form of lists , we can access them via the getters
     * or some image for the
     */


    //list of the names of the professor images
    private static final List<String> listOfProfessor ;
    //list of the names of the employees images
    private static final List<String> listOfEmployees ;
    //list of the names of the student images
    private static final List<String> listOfStudent ;

    static {

        //a simple initialization of the list
        listOfEmployees = new ArrayList<>();
        listOfProfessor = new ArrayList<>();
        listOfStudent = new ArrayList<>();

        for (int i = 1; i <= 16; i++) {
            listOfStudent.add(String.valueOf(i) + ".png");
        }
        for (int i = 17; i <= 32; i++) {
            listOfProfessor.add(String.valueOf(i) + ".png");
        }
        for (int i = 33; i <= 40; i++) {
            listOfEmployees.add(String.valueOf(i) + ".png");
        }

    }


    /**
     * getters of the lists in this class
     * @return
     */
    public static List<String> getListOfStudent() {
        return listOfStudent;
    }

    public static List<String> getListOfProfessor() {
        return listOfProfessor;
    }

    public static List<String> getListOfEmployees() {
        return listOfEmployees;
    }
}

package com.librarysimulator.Providers;


import java.util.ArrayList;
import java.util.List;

public final class BooksProvider {

    /**
     * @definition
     * this class has the utility of providing
     * books in a Array list which we will use
     * in the future with the Observable List
     */

    // a simple list that hold the name of books in string format
    private static final List<String> booksList ;

    //initialization of the list with some books
    static {
        booksList = new ArrayList<>();
        booksList.add("A");
        booksList.add("B");
        booksList.add("C");
        booksList.add("D");
        booksList.add("E");
        booksList.add("F");
        booksList.add("G");
        booksList.add("H");
        booksList.add("I");
        booksList.add("J");
        booksList.add("K");
        booksList.add("L");
        booksList.add("M");
        booksList.add("N");
        booksList.add("O");
        booksList.add("P");
        booksList.add("Q");
        booksList.add("R");
        booksList.add("S");
        booksList.add("T");
        booksList.add("U");
        booksList.add("V");
        booksList.add("W");
        booksList.add("X");
        booksList.add("Y");
        booksList.add("Z");
    }

    //public getter of the list
    public static List<String> getBooksList() {
        return booksList;
    }
}

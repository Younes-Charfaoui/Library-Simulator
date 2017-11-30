package com.librarysimulator.Utilities;


import java.util.ArrayList;
import java.util.List;

public final class BooksProvider {

    /**
     * @definition
     * this class has the utilities of providing
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
    }

    //public getter of the list
    public static List<String> getBooksList() {
        return booksList;
    }
}

import CoreApplication.AsyncAlgorithm;
import CoreApplication.DataVisualiserApplication;
import SearchingAlgorithms.*;
import SortingAlgorithms.*;

import java.util.List;

/**
 * Data Visualiser
 *
 * Main class:
 * Entry point to start the application.
 *
 * @author Peter Mitchell
 * @version 2021.3
 */
public class Main {
    /**
     * Entry point for the program creates the CoreApplication.DataVisualiserApplication class.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        // Change smallMode to true if the window is too large for your screen.
        boolean smallMode = false;
        // launchMode = 0 is default, but may not work for everyone. Change to 1 if 0 does not work for you.
        // If 1 also does not work for you, use 2, and you will need to uncomment lines in getAlgorithm()
        // at the bottom of this file. The lines are marked for the ones you need for each task from 2 to 4.
        int launchMode = 1;

        switch(launchMode) {
            case 0 -> launchWithAllAlgorithms(smallMode);
            case 1 -> launchWithCustomListedAlgorithms(smallMode);
            case 2 -> launchWithNoReflection(smallMode);
            default -> System.out.println("INVALID LAUNCH MODE. Set it to 0, 1, or 2.");
        }
    }

    /**
     * This method will launch the application by loading all algorithms in the
     * SearchingAlgorithms and SortingAlgorithms folders.
     *
     * @param smallMode When true the application shrinks from default 800px height to 600px.
     */
    private static void launchWithAllAlgorithms(boolean smallMode) {
        new DataVisualiserApplication(smallMode);
    }

    /**
     * This method will launch the application by loading a specific subset of algorithms in the
     * SearchingAlgorithms and SortingAlgorithms folders. You can modify the String arrays in
     * this method to modify which are loaded, and control the order they appear in the dropdown.
     *
     * @param smallMode When true the application shrinks from default 800px height to 600px.
     */
    private static void launchWithCustomListedAlgorithms(boolean smallMode) {
        // Add or remove any class (file) names from this list.
        // Make sure that any you list here are inside the SortingAlgorithms package or it will fail to load them!
        String[] sortingAlgorithms = new String[]{"BubbleSort", "BubbleSortSlow", "SelectionSort", "InsertionSort" };

        // Same as above, but make sure these are in the SearchingAlgorithms package.
        String[] searchingAlgorithms = new String[]{"LinearSearch", "BinarySearch"};
        new DataVisualiserApplication(smallMode, sortingAlgorithms, searchingAlgorithms, true);
    }

    /**
     * Use this method if you are getting no items appearing in your dropdowns when running the application.
     * You will need to modify the method below this one by uncommenting lines associated with each task as you reach them.
     *
     * @param smallMode When true the application shrinks from default 800px height to 600px.
     */
    private static void launchWithNoReflection(boolean smallMode) {
        new DataVisualiserApplication(smallMode, Main::getAlgorithms);
    }

    /**
     * Modify this method by commenting or uncommenting the lines as you create each class to make them appear in the application.
     * You should only need to modify this method if you use the above launchWithNoReflection()
     *
     * @param main Reference to the core application.
     * @param algorithms Reference to the list to store all the algorithms into.
     */
    private static void getAlgorithms(DataVisualiserApplication main, List<AsyncAlgorithm> algorithms) {
        algorithms.add(new BubbleSort(main));
        algorithms.add(new BubbleSortSlow(main));
        //algorithms.add(new SelectionSort(main)); // Uncomment for Task 2+
        //algorithms.add(new InsertionSort(main)); // Uncomment for Task 3+
        algorithms.add(new LinearSearch(main));
        //algorithms.add(new BinarySearch(main)); // Uncomment for Task 4+
    }
}

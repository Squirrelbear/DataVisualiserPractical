package SortingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SortingAlgorithms.GnomeSort class:
 * https://www.geeksforgeeks.org/gnome-sort-a-stupid-one/
 * Implements the Gnome Sort algorithm. See link above for details.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class GnomeSort extends SortAlgorithm {
    /**
     * Creates the algorithm ready for a beginSort() to start sorting.
     *
     * @param main Reference to the CoreApplication.Main class for changing button states.
     */
    public GnomeSort(DataVisualiserApplication main) {
        super("Gnome Sort", main);
    }

    /**
     * The sort algorithm that will pause visually during the compare and swap calls.
     */
    @Override
    public void performAlgorithm() {
        int index = 0;

        while (index < dataElements.length) {
            if (index == 0)
                index++;

            if(compare(index, index-1)) {
                swap(index, index-1);
                index--;
            } else {
                index++;
            }
        }
    }
}

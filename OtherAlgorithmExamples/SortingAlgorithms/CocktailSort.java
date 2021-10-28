package SortingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SortingAlgorithms.CocktailSort class:
 * Implements the cocktail sort that is similar to bubble sort,
 * except it goes in both directions. You can read more at:
 * https://www.geeksforgeeks.org/cocktail-sort/
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class CocktailSort extends SortAlgorithm {
    /**
     * Creates the algorithm ready for a beginSort() to start sorting.
     *
     * @param main Reference to the CoreApplication.Main class for changing button states.
     */
    public CocktailSort(DataVisualiserApplication main) {
        super("Cocktail Sort", main);
    }

    /**
     * The sort algorithm that will pause visually during the compare and swap calls.
     */
    @Override
    public void performAlgorithm() {
        boolean swapped = true;
        int start = 0;
        int end = dataElements.length;

        while (swapped)
        {
            // reset the swapped flag on entering the loop, because it might be true from a previous iteration.
            swapped = false;

            // loop from bottom to top same as the bubble sort
            for (int i = start; i < end - 1; ++i)
            {
                if (compare(i+1,i)) {
                    swap(i,i+1);
                    swapped = true;
                }
            }

            // if nothing moved, then array is sorted.
            if (!swapped)
                break;

            // otherwise, reset the swapped flag so that it can be used in the next stage
            swapped = false;

            // move the end point back by one, because item at the end is in its rightful spot
            end = end - 1;

            // from top to bottom, doing the same comparison as in the previous stage
            for (int i = end - 1; i >= start; i--)
            {
                if (compare(i+1,i))
                {
                    swap(i,i+1);
                    swapped = true;
                }
            }

            // increase the starting point, because the last stage would have moved the next
            // smallest number to its rightful spot.
            start = start + 1;
        }
    }
}

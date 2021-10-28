package SortingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SortingAlgorithms.CombSort class:
 * A variation of SortingAlgorithms.BubbleSort that can compare more
 * than the next element. You can read more about it at:
 * https://www.geeksforgeeks.org/comb-sort/
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class CombSort extends SortAlgorithm {
    /**
     * Creates the algorithm ready for a beginSort() to start sorting.
     *
     * @param main Reference to the CoreApplication.Main class for changing button states.
     */
    public CombSort(DataVisualiserApplication main) {
        super("Comb Sort", main);
    }

    /**
     * The sort algorithm that will pause visually during the compare and swap calls.
     */
    @Override
    public void performAlgorithm() {
        int n = dataElements.length;

        // initialize gap
        int gap = n;

        // Initialize swapped as true to make sure that loop runs
        boolean swapped = true;

        // Keep running while gap is more than 1 and last iteration caused a swap
        while (gap != 1 || swapped)
        {
            // Find next gap
            gap = getNextGap(gap);

            // Initialize swapped as false so that we can check if swap happened or not
            swapped = false;

            // Compare all elements with current gap
            for (int i=0; i<n-gap; i++)
            {
                if (compare(i+gap, i))
                {
                    swap(i,i+gap);
                    swapped = true;
                }
            }
        }
    }

    /**
     * To find gap between elements
     * @param gap Current gap.
     * @return The new gap.
     */

    int getNextGap(int gap)
    {
        // Shrink gap by Shrink factor
        gap = (gap*10)/13;
        return Math.max(gap, 1);
    }
}

package SortingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SortingAlgorithms.PancakeSort class:
 * You can read more about the algorithm as:
 * https://www.geeksforgeeks.org/pancake-sorting/
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class PancakeSort extends SortAlgorithm {
    /**
     * Creates the algorithm ready for a beginSort() to start sorting.
     *
     * @param main Reference to the CoreApplication.Main class for changing button states.
     */
    public PancakeSort(DataVisualiserApplication main) {
        super("Pancake Sort", main);
    }

    /**
     * The sort algorithm that will pause visually during the compare and swap calls.
     */
    @Override
    public void performAlgorithm() {
        // Start from the complete array and one by one reduce current size by one
        for (int curr_size = dataElements.length; curr_size > 1; --curr_size)
        {
            // Find index of the maximum element in arr[0..curr_size-1]
            int mi = findMax(curr_size);

            // Move the maximum element to end of current array if it's not already at the end
            if (mi != curr_size-1)
            {
                // To move at the end, first move maximum number to beginning
                flip(mi);

                // Now move the maximum number to end by reversing current array
                flip(curr_size-1);
            }
        }
    }

    /**
     * Reverses arr[0..i]
     *
     * @param i Array to flip up until
     */
    private void flip(int i)
    {
        int start = 0;
        while (start < i)
        {
            swap(start, i);
            start++;
            i--;
        }
    }

    /**
     * Returns index of the max/min element in [0..n-1]
     *
     * @param n Number of elements to consider.
     * @return Max of first N elements in the array.
     */
    private int findMax(int n)
    {
        int mi, i;
        for (mi = 0, i = 0; i < n; ++i)
            if (compare(mi,i)) // arr[i] > arr[mi]
                mi = i;
        return mi;
    }
}

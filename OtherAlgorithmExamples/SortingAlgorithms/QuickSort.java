package SortingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SortingAlgorithms.QuickSort class:
 * Performs a quick sort.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class QuickSort extends SortAlgorithm {
    /**
     * Initialises the SortingAlgorithms.QuickSort ready to sort.
     *
     * @param main Reference to the CoreApplication.Main class for changing button states.
     */
    public QuickSort(DataVisualiserApplication main) {
        super("Quick Sort", main);
    }

    /**
     * The sort algorithm that will pause visually during the compare and swap calls.
     */
    @Override
    public void performAlgorithm() {
        quickSort(0,dataElements.length);
    }

    /**
     * Iterates over the partition making comparisons and performing swaps.
     *
     * @param start The start index of the partition.
     * @param stop The end index of the partition.
     * @return The index of the partition.
     */
    int partition(int start, int stop)
    {
        int pivot = stop - 1; // select last element as pivot
        int i = start;
        int j = stop - 1;
        for (;;) {
            while (compare(i, pivot) && i < stop) {
                ++i; // skip ‘low’ on left side
            }
            while (!compare(j, pivot) && j > start) {
                --j; // skip ‘high' on right side
            }
            if (i >= j) {
                break;
            }
            // swap out-of-place items
            swap(i, j);
        }
        // swap pivot to the final place (i)
        swap(pivot, i);
        return i;
    }

    /**
     * Splits up the data to subdivide the sorting using partitioning.
     *
     * @param start The start index.
     * @param stop The end index.
     */
    void quickSort(int start, int stop)
    {
        if (stop - start <= 1) {
            return;
        }
        int pivot = partition(start, stop);
        quickSort(start, pivot);
        quickSort(pivot + 1, stop);
    }
}

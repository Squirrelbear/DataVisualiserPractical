package SortingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SortingAlgorithms.BubbleSort class:
 * Performs a bubble sort.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class BubbleSort extends SortAlgorithm {
    /**
     * Initialises the SortingAlgorithms.BubbleSort ready to sort.
     *
     * @param main Reference to the CoreApplication.Main class for changing button states.
     */
    public BubbleSort(DataVisualiserApplication main) {
        super("Bubble Sort", main);
    }

    /**
     * The sort algorithm that will pause visually during the compare and swap calls.
     */
    @Override
    public void performAlgorithm() {
		// Swap is true as long as there is at least one swap that has occurred while iterating over the elements to be sorted.
        boolean swap = true;
        while (swap) {
            swap = false;
			// Iterate over all elements comparing them and swapping if adjacent elements are out of order.
            for (int i = 0; i < dataElements.length - 1; i++) {
                if (compare(i+1,i)) {
                    swap(i, i + 1);
                    swap = true;
                }
            }
        }
    }
}

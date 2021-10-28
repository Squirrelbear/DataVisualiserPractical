package SearchingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SearchingAlgorithms.JumpSearch class:
 * You can read more about this search method at:
 * https://www.geeksforgeeks.org/jump-search/
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class JumpSearch extends SearchAlgorithm {
    /**
     * Creates the algorithm ready for a beginAlgorithm() to run it.
     *
     * @param main          Reference to the main class for managing buttons.
     */
    public JumpSearch(DataVisualiserApplication main) {
        super("Jump Search", main);
    }

    /**
     * Runs the jump search to check if the number exists in the data set.
     */
    @Override
    public void performAlgorithm() {
        foundIndex = jumpSearch();
        dataSet.setStatusLabel(getSearchSummaryString());
    }

    /**
     * Searches using Jump Search as described in the link at the top of this file.
     *
     * @return Index where the target was found or -1.
     */
    private int jumpSearch() {
        int n = dataElements.length;

        // Finding block size to be jumped
        int step = (int)Math.floor(Math.sqrt(n));

        // Finding the block where element is present (if it is present)
        int prev = 0;
        while (compare(dataElements[Math.min(step, n)-1], target))
        {
            logStep("Checking ["+(Math.min(step, n)-1)+"]="+dataElements[Math.min(step, n)-1].getValue() + " is less than target.");
            prev = step;
            step += (int)Math.floor(Math.sqrt(n));
            if (prev >= n)
                return -1;
        }
        logStep("Checking ["+(Math.min(step, n)-1)+"]="+dataElements[Math.min(step, n)-1].getValue() + " is NOT less than target. First loop ended.");

        // Doing a linear search for x in block beginning with prev.
        while (compare(dataElements[prev], target))
        {
            logStep("Checking ["+(prev)+"]="+dataElements[Math.min(step, n)-1].getValue() + " is less than target.");
            prev++;

            // If we reached next block or end of array, element is not present.
            if (prev == Math.min(step, n))
                return -1;
        }
        logStep("Checking ["+(prev)+"]="+dataElements[Math.min(step, n)-1].getValue() + " is NOT less than target. Second loop ended.");

        // If element is found
        if (dataElements[prev].getValue() == target.getValue())
            return prev;

        return -1;
    }
}

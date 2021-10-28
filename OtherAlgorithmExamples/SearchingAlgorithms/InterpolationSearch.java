package SearchingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SearchingAlgorithms.InterpolationSearch class:
 * You can read more about this search method at:
 * https://www.geeksforgeeks.org/interpolation-search/
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class InterpolationSearch extends SearchAlgorithm {
    /**
     * Creates the algorithm ready for a beginAlgorithm() to run it.
     *
     * @param main          Reference to the main class for managing buttons.
     */
    public InterpolationSearch(DataVisualiserApplication main) {
        super("Interpolation Search", main);
    }

    /**
     * Runs the interpolation search to check if the number exists in the data set.
     */
    @Override
    public void performAlgorithm() {
        if(!orderLowToHigh) {
            logStep("ERROR: This algorithm only supports data sorted from Low to High.");
        }
        foundIndex = interpolationSearch(0, dataElements.length - 1);
        dataSet.setStatusLabel(getSearchSummaryString());
    }

    /**
     * @param low Start of the interpolation.
     * @param high End of the interpolation
     * @return Index of the found element or -1.
     */
    private int interpolationSearch(int low, int high)
    {
        // Since array is sorted, an element present in array must be in range defined by corner
        if (low <= high && !compare(target,dataElements[low]) && !compare(dataElements[high], target)) {
            // Probing the position with keeping uniform distribution in mind.
            int pos = low + (target.getValue() - dataElements[low].getValue())
                            * (high - low)/(dataElements[high].getValue() - dataElements[low].getValue());

            highlightAndLog(low, high, pos);

            // Condition of target found
            if (dataElements[pos].getValue() == target.getValue())
                return pos;

            // If x is larger, x is in right sub array
            else if (compare(dataElements[pos].getValue(), target.getValue()))
                return interpolationSearch(pos + 1, high);

            // If x is smaller, x is in left sub array
            else
                return interpolationSearch(low, pos - 1);
        }
        return -1;
    }
}

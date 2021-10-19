package SearchingAlgorithms;

import CoreApplication.*;

/**
 * Data Visualiser
 *
 * SearchingAlgorithms.LinearSearch class:
 * Demonstrates the simple Linear Search.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class LinearSearch extends SearchAlgorithm {
    /**
     * Creates the algorithm ready for a beginAlgorithm() to run it.
     *
     * @param main Reference to the main class for managing buttons.
     */
    public LinearSearch(DataVisualiserApplication main) {
        super("Linear Search", main);
        sleepMultiplier = 10;
    }

    /**
     * Runs the linear search algorithm to search for the target.
     */
    @Override
    public void performAlgorithm() {
		// foundIndex is a special variable defined in the SearchAlgorithm class that is used for generating the search summary string.
        foundIndex = linearSearch();
		// Set the text shown in the middle of the application to either a target found or target not found message.
        dataSet.setStatusLabel(getSearchSummaryString());
    }


    /**
     * @return Either the location where the element was found, or -1 if the element could not be found.
     */
    private int linearSearch() {
        for(int i = 0; i < dataElements.length; i++) {
            highlightAndLog(i);
            if(dataElements[i].getValue() == target.getValue()) {
                return i;
            }
        }
        return -1;
    }
}

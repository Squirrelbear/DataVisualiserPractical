package CoreApplication;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Visualiser
 *
 * CoreApplication.SearchAlgorithm class:
 * Defines a template to use for any CoreApplication.SearchAlgorithm by providing the
 * behind the scenes management of the linking of data, and running
 * the process as a separate Thread.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public abstract class SearchAlgorithm extends AsyncAlgorithm {
    /**
     * Number of comparisons done during a search.
     */
    protected int comparisons;
    /**
     * When true the compare will perform a lessThan operation, and when
     * false the compare will perform a greaterThan operation. This will
     * for all algorithms swap the resulting order of the outcome.
     */
    protected boolean orderLowToHigh;
    /**
     * A boolean value for search validation to check if the data is sorted.
     */
    protected boolean isDataSorted;
    /**
     * Sequence history used for creating the log entry.
     */
    protected final List<String> sequenceHistory;
    /**
     * Index where the element was found or -1.
     */
    protected int foundIndex;
    /**
     * Reference to the Target being searched for. (Is the temp value in the CoreApplication.DataSet).
     */
    protected DataElement target;

    /**
     * Creates the algorithm ready for a beginAlgorithm() to run it.
     *
     * @param algorithmName Name of the Algorithm.
     * @param main Reference to the main class for managing buttons.
     */
    public SearchAlgorithm(String algorithmName, DataVisualiserApplication main) {
        super(algorithmName, main);
        orderLowToHigh = true;
        isDataSorted = true;
        sequenceHistory = new ArrayList<>();
    }

    /**
     * Prepares the algorithm by linking the CoreApplication.DataSet, and then starts a thread that will call run() to start the
     * performAlgorithm() method in any class that has extended from this class.
     *
     * @param dataSet Reference to the CoreApplication.DataSet to apply the algorithm to.
     */
    @Override
    public void beginAlgorithm(DataSet dataSet) {
        if(!chooseTarget(dataSet)) {
            dataSet.setStatusLabel("Aborted search. No target selected.");
            return;
        }
        target = dataSet.getTempValueElement();
        dataSet.setShowTempOrTarget(false);
        comparisons = 0;
        foundIndex = -1;
        sequenceHistory.clear();
        validateData(dataSet);
        super.beginAlgorithm(dataSet);
    }

    /**
     * Highlights the specified element for a sleep period.
     *
     * @param element Element to highlight.
     */
    public void highlightDataElement(DataElement element) {
        element.setSortColourState(DataElement.SortColourState.Comparing);
        sleep();
        waitForPause();
        element.setSortColourState(DataElement.SortColourState.None);
    }

    /**
     * Highlights up to 3 elements. If they are the same element for 1 or more,
     * it will highlight in the priority mid > high > low.
     * Mid is given a different colour to the high/low.
     *
     * @param low Low value in search range.
     * @param high High value in search range.
     * @param mid Mid value that is being evaluated.
     */
    public void highlightLowHighMid(DataElement low, DataElement high, DataElement mid) {
        low.setSortColourState(DataElement.SortColourState.Swapping);
        high.setSortColourState(DataElement.SortColourState.Swapping);
        mid.setSortColourState(DataElement.SortColourState.Comparing);
        sleep();
        waitForPause();
        low.setSortColourState(DataElement.SortColourState.None);
        high.setSortColourState(DataElement.SortColourState.None);
        mid.setSortColourState(DataElement.SortColourState.None);
    }

    /**
     * Highlights the elements being compared and pauses for a moment. Then deselects them,
     * notified the CoreApplication.DataSet that another comparison has occurred, and then returns the result
     * of the comparison. This method will return based on whether the data is being ordered
     * from low to high values, or high to low values.
     * When sorting from low to high values it will do a less than comparison of A less than B.
     * Otherwise when sorting from high to low it will do a greater than comparison of A greater than B.
     * This will result in sorting that matches the desired result.
     *
     * @param indexA Index of the element to compare.
     * @param indexB Index of the other element to compare.
     * @return True if sorting low to high and A less than B or if sorting high to low and A greater than B.
     */
    public boolean compare(int indexA, int indexB) {
        return compare(dataElements[indexA], dataElements[indexB]);
    }

    /**
     * Highlights the elements being compared and pauses for a moment. Then deselects them,
     * notified the CoreApplication.DataSet that another comparison has occurred, and then returns the result
     * of the comparison. This method will return based on whether the data is being ordered
     * from low to high values, or high to low values.
     * When sorting from low to high values it will do a less than comparison of A less than B.
     * Otherwise when sorting from high to low it will do a greater than comparison of A greater than B.
     * This will result in sorting that matches the desired result.
     *
     * @param elementA The element to compare.
     * @param elementB The other element to compare.
     * @return True if sorting low to high and A less than B or if sorting high to low and A greater than B.
     */
    public boolean compare(DataElement elementA, DataElement elementB) {
        showDelayedCompareVisual(elementA, elementB);
        comparisons++;
        return (orderLowToHigh && elementA.getValue() < elementB.getValue())
                || (!orderLowToHigh && elementA.getValue() > elementB.getValue());
    }

    /**
     * Logs a step to the sequence history in the form: "N. stepData"
     *
     * @param stepData Data to log for the given step.
     */
    public void logStep(String stepData) {
        String stepMessage = (sequenceHistory.size()+1)+ ". " + stepData;
        sequenceHistory.add(stepMessage);
        dataSet.setStatusLabel(stepMessage);
    }

    /**
     * Gets the sequence history.
     *
     * @return The history of logged steps.
     */
    public List<String> getLoggedSteps() {
        return sequenceHistory;
    }

    /**
     * Implies the current element is being checked as a comparison.
     *
     * @param index Index to highlight.
     */
    public void highlightAndLog(int index) {
        comparisons++;
        logStep("Checking ["+index+"]="+dataElements[index].getValue());
        highlightDataElement(dataElements[index]);
    }

    /**
     * Implies the mid element is being checked as a comparison.
     * With shown low/high range around it.
     *
     * @param low Low index to highlight.
     * @param high High index to highlight.
     * @param mid Mid index to highlight.
     */
    public void highlightAndLog(int low, int high, int mid) {
        comparisons++;
        logStep("Low ["+low+"]="+dataElements[low].getValue()
                + " High ["+high+"]="+dataElements[high].getValue()
                + " Mid ["+mid+"]="+dataElements[mid].getValue());
        highlightLowHighMid(dataElements[low], dataElements[high], dataElements[mid]);
    }

    /**
     * Gets a String to show the result of the search based on the foundIndex.
     *
     * @return A String representing a summary of the search result.
     */
    public String getSearchSummaryString() {
        String result = "Target " + dataSet.getTempValueElement().getValue();
        if(foundIndex == -1) {
            result += " was not found.";
        } else {
            result += " was found at [" + foundIndex + "]=" + dataElements[foundIndex].getValue();
        }
        return result;
    }

    /**
     * @return True if sorted from low to high. False if sorted high to low.
     */
    public boolean getOrderLowToHigh() {
        return orderLowToHigh;
    }

    /**
     * Validates the data to check if the data is sorted, and to guess
     * the sorted direction based on the first two elements.
     *
     * @param dataSet Reference to the dataset to perform validation on.
     */
    private void validateData(DataSet dataSet) {
        DataElement[] elements = dataSet.getDataElements();
        // Find the first value that is different to the first element.
        int firstChangeIndex = 1;
        for(;firstChangeIndex < elements.length; firstChangeIndex++) {
            if(elements[firstChangeIndex].getValue() != elements[0].getValue()) break;
        }
        if(firstChangeIndex == elements.length) firstChangeIndex--;
        orderLowToHigh = elements[0].getValue() < elements[firstChangeIndex].getValue();
        isDataSorted = true;
        for(int i = 2; i < elements.length; i++) {
            if((!orderLowToHigh && elements[i].getValue() > elements[i-1].getValue())
            || (orderLowToHigh && elements[i].getValue() < elements[i-1].getValue())) {
                isDataSorted = false;
                break;
            }
        }
    }

    /**
     * Provides a prompt to the user to provide input of a Target.
     *
     * @param dataSet Reference to the CoreApplication.DataSet for storing the target upon success.
     * @return Success of whether a target was chosen.
     */
    private boolean chooseTarget(DataSet dataSet) {
        String input = JOptionPane.showInputDialog("Enter a valid integer to search for. Leave blank to cancel.");
        while(input != null && !input.isEmpty() && !isInteger(input)) {
            input = JOptionPane.showInputDialog("Invalid input. Enter a valid integer to search for. Leave blank to cancel.");
        }
        if(input == null || input.isEmpty()) {
            return false;
        }
        dataSet.getTempValueElement().setValue(Integer.parseInt(input));
        return true;
    }

    /**
     * Checks if the supplied String is an Integer by validating all characters.
     *
     * @param s String to test if it is an Integer.
     * @return True if the specified String is an Integer.
     */
    private static boolean isInteger(String s) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),10) < 0) return false;
        }
        return true;
    }
}

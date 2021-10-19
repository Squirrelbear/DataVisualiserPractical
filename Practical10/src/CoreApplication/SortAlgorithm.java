package CoreApplication;

/**
 * Data Visualiser
 *
 * CoreApplication.SortAlgorithm class:
 * Defines a template to use for any CoreApplication.SortAlgorithm by providing the
 * behind the scenes management of the linking of data, and running
 * the process as a separate Thread.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public abstract class SortAlgorithm extends AsyncAlgorithm {
    /**
     * The number of swaps that occurred during the current or last sort.
     */
    protected int swaps;
    /**
     * The number of comparisons that occurred during the current or last sort.
     */
    protected int comparisons;
    /**
     * When true the compare will perform a lessThan operation, and when
     * false the compare will perform a greaterThan operation. This will
     * for all algorithms swap the resulting order of the outcome.
     */
    private boolean orderLowToHigh;

    /**
     * Creates the algorithm ready for a beginSort() to start sorting.
     *
     * @param algorithmName Name of the Algorithm.
     * @param main Reference to main or managing button states.
     */
    public SortAlgorithm(String algorithmName, DataVisualiserApplication main) {
        super(algorithmName, main);
        orderLowToHigh = true;
    }

    /**
     * Prepares the algorithm by linking the CoreApplication.DataSet, and then starts a thread that will call run() to start the
     * performAlgorithm() method in any class that has extended from this class.
     *
     * @param dataSet Reference to the CoreApplication.DataSet to apply the algorithm to.
     */
    @Override
    public void beginAlgorithm(DataSet dataSet) {
        dataSet.setShowTempOrTarget(true);
        comparisons = swaps = 0;
        super.beginAlgorithm(dataSet);
        updateStatusText();
    }

    /**
     * Overwrites the value in "to" with the value in "from".
     * Pauses happen twice as the values move.
     *
     * @param to CoreApplication.DataElement to overwrite the value of.
     * @param from CoreApplication.DataElement to get the value from.
     */
    public void assign(DataElement to, DataElement from) {
        to.setSortColourState(DataElement.SortColourState.Swapping);
        sleep();
        to.setValue(from.getValue());
        sleep();
        waitForPause();
        to.setSortColourState(DataElement.SortColourState.None);
    }

    /**
     * Swaps by using assign to cycle values between the elements at the
     * specified indexes and the temp value element in the CoreApplication.DataSet.
     * Notifies the CoreApplication.DataSet of the number of swaps to display.
     *
     * @param indexA Index of the element to swap.
     * @param indexB Index of the other element to swap.
     */
    public void swap(int indexA, int indexB) {
        assign(dataSet.getTempValueElement(), dataElements[indexA]);
        assign(dataElements[indexA], dataElements[indexB]);
        assign(dataElements[indexB], dataSet.getTempValueElement());
        swaps++;
        updateStatusText();
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
        updateStatusText();
        return (orderLowToHigh && elementA.getValue() < elementB.getValue())
                || (!orderLowToHigh && elementA.getValue() > elementB.getValue());
    }

    /**
     * Changes the order used for the result of a sort. This modifies the way the compare() method works.
     *
     * @param orderLowToHigh True indicates low to high order, false indicates high to low.
     */
    public void setOrderLowToHigh(boolean orderLowToHigh) {
        this.orderLowToHigh = orderLowToHigh;
    }

    /**
     * @return True if sorting from low to high. False if sorting high to low.
     */
    public boolean getOrderLowToHigh() {
        return orderLowToHigh;
    }

    /**
     * Updates the status text to reflect the current number of swaps and comparisons.
     */
    public void updateStatusText() {
        dataSet.setStatusLabel("Swaps: " + swaps + "    Comparisons: " + comparisons);
    }
}

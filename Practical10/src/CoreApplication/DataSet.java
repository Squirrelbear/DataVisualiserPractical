package CoreApplication;

import javax.swing.*;
import java.awt.*;

/**
 * Data Visualiser
 *
 * CoreApplication.DataSet class:
 * Manages a collection of DataElements providing access to them for other
 * classes to modify their values while continuing to draw them to show
 * the current state.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class DataSet {
    /**
     * Maximum number of elements expected to be used as part of a CoreApplication.DataSet.
     */
    public static final int MAXIMUM_ELEMENTS = 100;
    /**
     * Number of elements to place on each row.
     */
    private static final int FIT_HORIZONTAL = 10;
    /**
     * Height of each individual CoreApplication.DataElement.
     */
    private static final int ELEMENT_HEIGHT = 30;
    /**
     * Width of each individual CoreApplication.DataElement.
     */
    private static final int ELEMENT_WIDTH = 60;
    /**
     * The amount of space vertically that is required to draw the CoreApplication.DataSet assuming maximum needs.
     */
    public static final int REQUIRED_HEIGHT = ELEMENT_HEIGHT * (1 + MAXIMUM_ELEMENTS / FIT_HORIZONTAL)+5;
    /**
     * The amount of space horizontally that is required to draw the CoreApplication.DataSet.
     */
    public static final int REQUIRED_WIDTH = ELEMENT_WIDTH * FIT_HORIZONTAL;

    /**
     * Defines enums to represent the different ways that data can be filled into the dataset regardless of size.
     */
    public enum DataSetFillMode {
        /**
         * Random will randomly select numbers from 1 to 99 for every cell.
         */
        Random,
        /**
         * Sorted will fill from 0 to N-1.
         */
        Sorted,
        /**
         * SortedReverse will fill from N-1 to 0.
         */
        SortedReverse,
        /**
         * Custom will allow data entry via an Input Dialog.
         */
        Custom }

    /**
     * The data stored within this CoreApplication.DataSet.
     */
    private DataElement[] dataElements;
    /**
     * All the values that were inserted after a fill operation was last conducted. Used for resetting the data.
     */
    private int[] storedValues;
    /**
     * Visual offset on the X axis for the DataElements.
     */
    private final int offsetX;
    /**
     * Visual offset on the Y axis for the DataElements.
     */
    private final int offsetY;
    /**
     * A reference to the Temp Value that is shown below the CoreApplication.DataSet used for performing swap operations in Sorts.
     */
    private final DataElement tempValueElement;
    /**
     * A message to show the current state of what is being done to the data.
     */
    private String statusMessage;
    /**
     * When true the word "Temp" is shown, when false the word "Target" is shown.
     */
    private boolean showTempText;

    /**
     * Initialises the CoreApplication.DataSet with 0s and makes it ready for populateData() to fill it with useful data.
     *
     * @param offsetX Visual offset on the X axis for the DataElements.
     * @param offsetY A reference to the Temp Value that is shown below the CoreApplication.DataSet used for performing swap operations in Sorts.
     * @param initialDataSetSize The size to initially resize the CoreApplication.DataSet to.
     */
    public DataSet(int offsetX, int offsetY, int initialDataSetSize) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        resizeDataSet(initialDataSetSize);
        tempValueElement = new DataElement(0, offsetX+80, offsetY+REQUIRED_HEIGHT-ELEMENT_HEIGHT, ELEMENT_WIDTH, ELEMENT_HEIGHT);

        setStatusLabel("Select a sort/search algorithm.");
        setShowTempOrTarget(true);
    }

    /**
     * Draws all the data elements, the temp value, and Strings for the number of swaps, and
     * number of comparisons that have been performed in sorts.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        for (DataElement dataElement : dataElements) {
            dataElement.paintTextValue(g);
        }
        g.setColor(Color.BLACK);
        g.drawString(showTempText?"Temp:":"Target", offsetX, offsetY+REQUIRED_HEIGHT-8);
        tempValueElement.paintTextValue(g);
        g.drawString(statusMessage, offsetX+80+ELEMENT_WIDTH+20, offsetY+REQUIRED_HEIGHT-8);
    }

    /**
     * Will resize if the size does not match the current size, and then
     * fill the dataset using the specified method. The values are then
     * all stored into an array to be used for the reset() if it is used.
     *
     * @param dataSetFillMode Mode to use for filling the dataset.
     * @param dataSetSize Size expected for filling the dataset.
     */
    public void populateData(DataSetFillMode dataSetFillMode, int dataSetSize) {
        // Only resize if the number of elements has changed.
        if(dataSetSize != dataElements.length) {
            resizeDataSet(dataSetSize);
        }
        // Fill with requested type of data.
        switch (dataSetFillMode) {
            case Random -> fillWithRandom();
            case Sorted -> fillWithSorted();
            case SortedReverse -> fillWithSortedReversed();
            case Custom -> fillWithCustom();
        }
        // Store values for reset
        storedValues = new int[dataElements.length];
        for(int i = 0; i < storedValues.length; i++) {
            storedValues[i] = dataElements[i].getValue();
        }
    }

    /**
     * Gets the array containing all the DataElements.
     *
     * @return A reference to the DataElements contained in this CoreApplication.DataSet.
     */
    public DataElement[] getDataElements() {
        return dataElements;
    }

    /**
     * Resets the data back to just after the last populateData() call occurred.
     */
    public void reset() {
        for(int i = 0; i < storedValues.length; i++) {
            dataElements[i].setValue(storedValues[i]);
        }
    }

    /**
     * Updates the status display to show the new message about the current state of what is being done to the data.
     *
     * @param statusMessage New message to display.
     */
    public void setStatusLabel(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Modifies the data line of the CoreApplication.DataSet output to change the text next to the temp variable.
     *
     * @param showTemp When true Temp is shown, when false Target is shown.
     */
    public void setShowTempOrTarget(boolean showTemp) {
        this.showTempText = showTemp;
    }

    /**
     * Gets a reference to the Temp variable that can show when a Temp value needs to be used for swaps.
     *
     * @return A reference to the visual Temp element.
     */
    public DataElement getTempValueElement() {
        return tempValueElement;
    }

    /**
     * The data before it is was sorted.
     *
     * @return The raw int stored values that are ready for applying as a reset.
     */
    public int[] getStoredValues() {
        return storedValues;
    }

    /**
     * Cleanup method to remove all states for colouring.
     */
    public void removeAllDataStates() {
        for (DataElement dataElement : dataElements) {
            dataElement.setSortColourState(DataElement.SortColourState.None);
        }
        tempValueElement.setSortColourState(DataElement.SortColourState.None);
    }

    /**
     * Gets a random number between 1 to 99.
     *
     * @return A value between 1 and 99 inclusively.
     */
    private int getRandom1to99() {
        return (int)(Math.random()*99+1);
    }

    /**
     * Fills with the numbers from 0 to N-1.
     */
    private void fillWithSorted() {
        for(int i = 0; i < dataElements.length; i++) {
            dataElements[i].setValue(i);
        }
    }

    /**
     * Fills with the numbers from N-1 to 0.
     */
    private void fillWithSortedReversed() {
        for(int i = 0; i < dataElements.length; i++) {
            dataElements[i].setValue(dataElements.length-i);
        }
    }

    /**
     * Fills with random numbers between 1 to 99.
     */
    private void fillWithRandom() {
        for (DataElement dataElement : dataElements) {
            dataElement.setValue(getRandom1to99());
        }
    }

    /**
     * Fills some, all or, no elements depending on entry. The user is presented with a
     * box telling them the number of values they can enter and that the numbers can be between 1 to 99.
     * If they fail to enter anything, or close the dialog the data will fill with random.
     * The method will otherwise parse numbers until it either runs out of elements to insert into, or
     * it encounters an invalid input. Any invalid input will stop any further parsing.
     */
    private void fillWithCustom() {
        String customData = JOptionPane.showInputDialog("Enter " + dataElements.length + " space separated ints (1 to 99).");
        if(customData == null || customData.isEmpty()) {
            // null occurs when the input dialog is closed, and empty is when the text is left black.
            fillWithRandom();
            JOptionPane.showMessageDialog(null, "Error: You did not enter anything, defaulting to random.");
        } else {
            String[] splitCustomData = customData.split(" ");
            int i = 0;
            // Keep looping while there are both positions to insert into, and more data to parse.
            for(; i < dataElements.length && i < splitCustomData.length; i++) {
                try {
                    // Attempt to convert the integer and check it is in the valid range.
                    int value = Integer.parseInt(splitCustomData[i]);
                    if(value < 1 || value > 99) {
                        // Terminate from invalid range.
                        break;
                    }
                    dataElements[i].setValue(value);
                } catch(Exception e) {
                    // Terminate from something that was not an integer.
                    break;
                }
            }
            if(i < dataElements.length) {
                // Error message when not all numbers were changed.
                JOptionPane.showMessageDialog(null, "Error: Less than " + dataElements.length + " valid elements or invalid element found, extra elements unchanged.");
            }
        }
    }

    /**
     * Overwrites the old DataElements array with the new one.
     * The elements are grouped to centre in the available space.
     *
     * @param newSize Size to change the array to.
     */
    private void resizeDataSet(int newSize) {
        // Calculate vertical offset to centre inside the available space.
        int rows = (int)Math.ceil((double)newSize / FIT_HORIZONTAL)+1;
        int extraYOffset = REQUIRED_HEIGHT / 2 - rows * ELEMENT_HEIGHT / 2;

        // Generate all the elements splitting between rows
        dataElements = new DataElement[newSize];
        for(int i = 0; i < dataElements.length; i++) {
            dataElements[i] = new DataElement(0,offsetX+(i%FIT_HORIZONTAL)*ELEMENT_WIDTH,
                    offsetY+(i/10)*ELEMENT_HEIGHT+extraYOffset,ELEMENT_WIDTH,ELEMENT_HEIGHT);
        }
    }
}

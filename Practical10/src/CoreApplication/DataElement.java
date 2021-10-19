package CoreApplication;

import java.awt.*;

/**
 * Data Visualiser
 *
 * CoreApplication.DataElement class:
 * Represents a single array element with configuration to render it visually.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class DataElement {
    /**
     * The current state of this CoreApplication.DataElement to modify its colour.
     */
    public enum SortColourState {
        /**
         * None = No colour change.
         */
        None,
        /**
         * Comparing = Cyan.
         */
        Comparing,
        /**
         * Swapping = Yellow.
         */
        Swapping
    }

    /**
     * Variable to represent the left most edge of the box around this element.
     */
    private final int x;
    /**
     * Variable to represent the top most edge of the box around this element.
     */
    private final int y;
    /**
     * Variable to represent the width of the box around this element.
     */
    private final int width;
    /**
     * Variable to represent the height of the box around this element.
     */
    private final int height;

    /**
     * The value stored by this element.
     */
    private int value;
    /**
     * The current state for sorting representing how this element should be drawn.
     */
    private SortColourState sortColourState;

    /**
     * Creates the element ready to be used.
     *
     * @param value The initial value to set the element to.
     * @param x The x offset for drawing the element.
     * @param y The y offset for drawing the element.
     * @param width Width of the element.
     * @param height Height of the element.
     */
    public DataElement(int value, int x, int y, int width, int height) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        sortColourState = SortColourState.None;
    }

    /**
     * Changes the state of the CoreApplication.DataElement to the specified state.
     *
     * @param sortColourState New state to change this element to.
     */
    public void setSortColourState(SortColourState sortColourState) {
        this.sortColourState = sortColourState;
    }

    /**
     * Gets the current state of the CoreApplication.DataElement.
     *
     * @return The current state of this CoreApplication.DataElement.
     */
    public SortColourState getSortColourState() {
        return sortColourState;
    }

    /**
     * Gets the current value of the CoreApplication.DataElement.
     *
     * @return The current value of the CoreApplication.DataElement.
     */
    public int getValue() {
        return value;
    }

    /**
     * Stores the specified value into this CoreApplication.DataElement.
     *
     * @param value New value to store in this CoreApplication.DataElement.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Draws the CoreApplication.DataElement to the panel.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paintTextValue(Graphics g) {
        // Draw a coloured background if the state of this element indicates it is actively being used in a sort.
        if(sortColourState != SortColourState.None) {
            g.setColor(sortColourState == SortColourState.Comparing ? Color.CYAN : Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
        // Draw a border around the edge of the element.
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        // Draw the number centered inside the border.
        String strValue = String.valueOf(value);
        g.setFont(new Font("Arial", Font.BOLD, height-10));
        int strWidth = g.getFontMetrics().stringWidth(strValue);
        g.drawString(strValue, x+width/2-strWidth/2, y+height-8);
    }
}

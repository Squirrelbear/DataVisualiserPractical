package GraphTypes;

import java.awt.*;
import CoreApplication.*;

/**
 * Data Visualiser
 *
 * GraphTypes.Histogram class:
 * Draws a simple GraphTypes.Histogram based on the provided CoreApplication.DataSet.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class Histogram extends Graph {
    /**
     * Stores the specified properties to use for rendering the GraphTypes.Histogram.
     *
     * @param dataSet The CoreApplication.DataSet to render on the CoreApplication.Graph.
     * @param offsetX The offset on the left side of the graph.
     * @param offsetY The offset at the top of the graph.
     * @param width Width of the graph.
     * @param height Height of the graph.
     */
    public Histogram(DataSet dataSet, int offsetX, int offsetY, int width, int height) {
        super(dataSet, offsetX, offsetY, width, height);
    }

    /**
     * Draws the GraphTypes.Histogram by calculating spacing and then iterating over all elements
     * to render bars based on the magnitude of their values. Colours are applied to
     * represent the swapping states both to the borders and the background of each bar.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        DataElement[] elements = dataSet.getDataElements();
        // Calculate spacing requirements.
        int elementWidth = width / elements.length;
        double heightPerValue = getHeightPerValue(elements);

        // Draw a bar and outline for every element.
        for(int i = 0; i < elements.length; i++) {
            // Change the background colour based on the sort state of the current element.
            if(elements[i].getSortColourState() == DataElement.SortColourState.None) {
                g.setColor(Color.lightGray);
            } else if(elements[i].getSortColourState() == DataElement.SortColourState.Swapping) {
                g.setColor(Color.YELLOW);
            } else if(elements[i].getSortColourState() == DataElement.SortColourState.Comparing) {
                g.setColor(Color.CYAN);
            }
            // Draw the background
            g.fillRect(offsetX+i*elementWidth+2, (int)(offsetY+height-elements[i].getValue()*heightPerValue),
                    elementWidth-4, (int)(elements[i].getValue()*heightPerValue));
            // Change the border colour based on the sort state of the current element.
            if(elements[i].getSortColourState() == DataElement.SortColourState.None) {
                g.setColor(Color.BLACK);
            } else if(elements[i].getSortColourState() == DataElement.SortColourState.Swapping) {
                g.setColor(new Color(149, 125, 28));
            } else if(elements[i].getSortColourState() == DataElement.SortColourState.Comparing) {
                g.setColor(new Color(9, 19, 95));
            }
            //Draw the border
            g.drawRect(offsetX+i*elementWidth+2, (int)(offsetY+height-elements[i].getValue()*heightPerValue),
                    elementWidth-4, (int)(elements[i].getValue()*heightPerValue));
        }

        // Draw a border around the entire CoreApplication.Graph.
        g.setColor(Color.BLACK);
        g.drawRect(offsetX,offsetY,width,height);
    }
}

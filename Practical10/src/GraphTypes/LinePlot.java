package GraphTypes;

import java.awt.*;
import CoreApplication.*;

/**
 * Data Visualiser
 *
 * GraphTypes.Histogram class:
 * Draws a simple Line Plot based on the provided CoreApplication.DataSet.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class LinePlot extends Graph {
    /**
     * Stores the specified properties to use for rendering the Line Plot.
     *
     * @param dataSet The CoreApplication.DataSet to render on the CoreApplication.Graph.
     * @param offsetX The offset on the left side of the graph.
     * @param offsetY The offset at the top of the graph.
     * @param width Width of the graph.
     * @param height Height of the graph.
     */
    public LinePlot(DataSet dataSet, int offsetX, int offsetY, int width, int height) {
        super(dataSet, offsetX, offsetY, width, height);
    }

    /**
     * Draws the line plot by determining the spacing for each element, and then
     * calculating the x and y positions for each point of the line. It is then
     * drawn as a polygon.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        DataElement[] elements = dataSet.getDataElements();
        int[] xCoords = new int[elements.length+2];
        int[] yCoords = new int[elements.length+2];

        int elementWidth = width / elements.length;
        double heightPerValue = getHeightPerValue(elements);

        for(int i = 0; i < elements.length; i++) {
            xCoords[i] = offsetX+i*elementWidth+2+elementWidth/2;
            yCoords[i] = (int)(offsetY+height-elements[i].getValue()*heightPerValue);
        }
        xCoords[elements.length] = xCoords[elements.length-1];
        yCoords[elements.length] = offsetY+height;
        xCoords[elements.length+1] = xCoords[0];
        yCoords[elements.length+1] = offsetY+height;

        g.setColor(Color.BLACK);
        g.drawPolygon(xCoords,yCoords,xCoords.length);

        g.drawRect(offsetX,offsetY,width,height);
    }
}

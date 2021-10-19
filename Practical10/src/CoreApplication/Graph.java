package CoreApplication;

import java.awt.*;

/**
 * Data Visualiser
 *
 * CoreApplication.Graph class:
 * Defines a generic CoreApplication.Graph that can be extended from to implement
 * a graph that fits inside the specified region lined to the CoreApplication.DataSet.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public abstract class Graph {

    /**
     * The CoreApplication.DataSet to render on the CoreApplication.Graph.
     */
    protected final DataSet dataSet;
    /**
     * The offset on the left side of the graph.
     */
    protected final int offsetX;
    /**
     * The offset at the top of the graph.
     */
    protected final int offsetY;
    /**
     * Width of the graph.
     */
    protected final int width;
    /**
     * Height of the graph.
     */
    protected final int height;

    /**
     * Stores the specified properties to use for rendering the CoreApplication.Graph.
     *
     * @param dataSet The CoreApplication.DataSet to render on the CoreApplication.Graph.
     * @param offsetX The offset on the left side of the graph.
     * @param offsetY The offset at the top of the graph.
     * @param width Width of the graph.
     * @param height Height of the graph.
     */
    public Graph(DataSet dataSet, int offsetX, int offsetY, int width, int height) {
        this.dataSet = dataSet;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    /**
     * Override this method to provide the visual drawing for Graphs.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public abstract void paint(Graphics g);

    /**
     * Helper method to determine spacing on Graphs based on the maximum value.
     *
     * @param elements The collection of elements from the CoreApplication.DataSet.
     * @return A number representing how much to multiply each number by to represent them on the Graphs.
     */
    protected double getHeightPerValue(DataElement[] elements) {
        double max = elements[0].getValue();
        for(int i = 1; i < elements.length; i++) {
            if(elements[i].getValue() > max) {
                max = elements[i].getValue();
            }
        }
        return height / max;
    }
}

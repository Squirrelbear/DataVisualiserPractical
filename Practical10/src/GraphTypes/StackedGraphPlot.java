package GraphTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import CoreApplication.Graph;

/**
 * Data Visualiser
 *
 * GraphTypes.StackedGraphPlot class:
 * Defines a generic multi-graph view that can show any number of other graphs.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class StackedGraphPlot extends Graph {
    /**
     * Any number of graphs that are all displayed by this multi graph view
     */
    private final List<Graph> graphList;

    /**
     * Creates a multi graph view by drawing other graphs instead.
     *
     * @param graphs Any number of graphs that are stacked in the order supplied.
     */
    public StackedGraphPlot(Graph... graphs) {
        // Data not used for this type
        super(null, 0, 0, 0, 0);
        graphList = new ArrayList<>();
        graphList.addAll(Arrays.asList(graphs));
    }

    /**
     * Draws all the graphs in the order they were added.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        graphList.forEach(graph -> graph.paint(g));
    }
}

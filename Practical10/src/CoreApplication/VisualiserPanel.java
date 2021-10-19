package CoreApplication;

import GraphTypes.DotPlot;
import GraphTypes.Histogram;
import GraphTypes.LinePlot;
import GraphTypes.StackedGraphPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Visualiser
 *
 * CoreApplication.VisualiserPanel class:
 * Manages the CoreApplication.DataSet and CoreApplication.Graph that are shown visually within this panel.
 * Messages are passed via the CoreApplication.Main class to provide input from the CoreApplication.ButtonPanel.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class VisualiserPanel extends JPanel implements ActionListener {
    /**
     * Visual width of the panel.
     */
    public static int PANEL_WIDTH = DataSet.REQUIRED_WIDTH + 20;
    /**
     * Visual Height of the panel.
     */
    public static int PANEL_HEIGHT = 600;
    /**
     * The CoreApplication.DataSet made up of a collection of DataElements.
     */
    private final DataSet dataSet;
    /**
     * A reference to the current CoreApplication.Graph being displayed.
     */
    private Graph graph;
    /**
     * A list of all CoreApplication.Graph options that can be changed using the CoreApplication.ButtonPanel.
     */
    private final List<Graph> graphsList;

    /**
     * Creates the CoreApplication.DataSet and fills it with a default set of random data using 10 elements.
     * Initialises all the Graphs ready to be hot swapped between, and initialises the timer
     * to force redrawing every 20ms.
     *
     * @param panelHeight Height of the panel recommend using ~600 for small or 800 for default.
     * @param  extraWidth Adds this amount to the panel's width. Do not give this a negative number. It will default to 0.
     */
    public VisualiserPanel(int panelHeight, int extraWidth) {
        PANEL_HEIGHT = panelHeight;
        if(extraWidth < 0) extraWidth = 0;
        PANEL_WIDTH += extraWidth;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.WHITE);

        dataSet = new DataSet(10+extraWidth/2, 10, 10);
        dataSet.populateData(DataSet.DataSetFillMode.Random, 10);
        graphsList = new ArrayList<>();
        graphsList.add(new Histogram(dataSet, 10, DataSet.REQUIRED_HEIGHT+20, PANEL_WIDTH-20, PANEL_HEIGHT-DataSet.REQUIRED_HEIGHT-30));
        graphsList.add(new LinePlot(dataSet, 10, DataSet.REQUIRED_HEIGHT+20, PANEL_WIDTH-20, PANEL_HEIGHT-DataSet.REQUIRED_HEIGHT-30));
        graphsList.add(new DotPlot(dataSet, 10, DataSet.REQUIRED_HEIGHT+20, PANEL_WIDTH-20, PANEL_HEIGHT-DataSet.REQUIRED_HEIGHT-30));
        graphsList.add(new StackedGraphPlot(graphsList.get(0), graphsList.get(1))); // GraphTypes.Histogram+Line Plot
        graphsList.add(new StackedGraphPlot(graphsList.get(2), graphsList.get(1))); // Dot+Line Plot
        setGraph(0);

        Timer updateTimer = new Timer(20, this);
        updateTimer.start();
    }

    /**
     * Draws the CoreApplication.DataSet, and the CoreApplication.Graph.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        dataSet.paint(g);
        graph.paint(g);
    }

    /**
     * Sorts the CoreApplication.DataSet by using the specified Algorithm.
     *
     * @param algorithm Algorithm to use for sorting the data.
     */
    public void applyAlgorithm(AsyncAlgorithm algorithm) {
        // Start a new thread to perform the sort.
        algorithm.beginAlgorithm(dataSet);
        repaint();
    }

    /**
     * Fills the CoreApplication.DataSet by setting the CoreApplication.DataSet to the specified size and then
     * filling using the specified method.
     *
     * @param dataSetFillMode Mode to use when filling the Data.
     * @param dataSetSize Size of CoreApplication.DataSet to fill with mode.
     */
    public void fillData(DataSet.DataSetFillMode dataSetFillMode, int dataSetSize) {
        dataSet.populateData(dataSetFillMode, dataSetSize);
    }

    /**
     * Resets the data back to just after the last fillData was called.
     */
    public void reset() {
        dataSet.reset();
    }

    /**
     * Sets the specified graph number to be the only one visible.
     *
     * @param graphIndex CoreApplication.Graph number to set as active.
     */
    public void setGraph(int graphIndex) {
        graph = graphsList.get(graphIndex);
    }

    /**
     * Called by the timer every 20ms to force an update. Will also on the first update
     * after the sort algorithm finished make the buttons become available again to perform
     * another subsequent sort.
     *
     * @param e Information about the event that has occurred.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}

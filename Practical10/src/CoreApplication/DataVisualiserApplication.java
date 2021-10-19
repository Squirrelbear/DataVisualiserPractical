package CoreApplication;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Data Visualiser
 *
 * CoreApplication.DataVisualiserApplication class:
 * Initialises the JFrame with the CoreApplication.VisualiserPanel and CoreApplication.ButtonPanel.
 * Then manages communication between the two classes.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class DataVisualiserApplication {
    /**
     * Reference to the CoreApplication.VisualiserPanel for passing input.
     */
    private final VisualiserPanel visualiserPanel;
    /**
     * Reference to the CoreApplication.ButtonPanel for changing button states.
     */
    private final ButtonPanel buttonPanel;

    /**
     * Creates the JFrame and populates it with the CoreApplication.VisualiserPanel and CoreApplication.ButtonPanel.
     *
     * @param smallMode When enabled the size of the window will be made smaller to accommodate smaller screens.
     * @param sortingAlgorithms A list of classes to load from the SortingAlgorithms package containing SortAlgorithm classes.
     * @param searchingAlgorithms A list of classes to load from the SearchingAlgorithms package containing SearchAlgorithm classes.
     */
    public DataVisualiserApplication(boolean smallMode, String[] sortingAlgorithms, String[] searchingAlgorithms) {
        JFrame frame = new JFrame("Data Visualiser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        visualiserPanel = new VisualiserPanel(smallMode ? 600 : 800, smallMode ? 0 : 300);
        frame.getContentPane().add(visualiserPanel, BorderLayout.WEST);
        buttonPanel = new ButtonPanel(this, smallMode ? 26 : 37, sortingAlgorithms, searchingAlgorithms);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates the JFrame and populates it with the CoreApplication.VisualiserPanel and CoreApplication.ButtonPanel.
     * Defaults to attempting to load all the algorithms using the ClassCollectionUtility.
     *
     * @param smallMode When enabled the size of the window will be made smaller to accommodate smaller screens.
     */
    public DataVisualiserApplication(boolean smallMode) {
        this(smallMode, ClassCollectorUtility.getAllSortingAlgorithms(), ClassCollectorUtility.getAllSearchingAlgorithms());
    }

    /**
     * Creates the JFrame and populates it with the CoreApplication.VisualiserPanel and CoreApplication.ButtonPanel.
     *
     * @param smallMode When enabled the size of the window will be made smaller to accommodate smaller screens.
     * @param loadAlgorithmsMethod A reference to the method for loading algorithms.
     */
    public DataVisualiserApplication(boolean smallMode, BiConsumer<DataVisualiserApplication, List<AsyncAlgorithm>> loadAlgorithmsMethod) {
        JFrame frame = new JFrame("Data Visualiser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        visualiserPanel = new VisualiserPanel(smallMode ? 600 : 800, smallMode ? 0 : 300);
        frame.getContentPane().add(visualiserPanel, BorderLayout.WEST);
        List<AsyncAlgorithm> algorithms = new ArrayList<>();
        loadAlgorithmsMethod.accept(this, algorithms);
        buttonPanel = new ButtonPanel(this, smallMode ? 26 : 37, algorithms);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Applies the algorithm to the CoreApplication.DataSet.
     *
     * @param algorithm Algorithm to use on the data.
     */
    public void applyAlgorithm(AsyncAlgorithm algorithm) {
        visualiserPanel.applyAlgorithm(algorithm);
    }

    /**
     * Fills the CoreApplication.DataSet by setting the CoreApplication.DataSet to the specified size and then
     * filling using the specified method.
     *
     * @param dataSetFillMode Mode to use when filling the Data.
     * @param dataSetSize Size of CoreApplication.DataSet to fill with mode.
     */
    public void fillData(DataSet.DataSetFillMode dataSetFillMode, int dataSetSize) {
        visualiserPanel.fillData(dataSetFillMode, dataSetSize);
    }

    /**
     * Resets the data back to just after the last fillData was called.
     */
    public void reset() {
        visualiserPanel.reset();
    }

    /**
     * Change the enabled status of buttons to prevent use during sorting.
     *
     * @param buttonsEnabled True enables all the buttons in the CoreApplication.ButtonPanel. False disables all the buttons.
     */
    public void setButtonsEnabled(boolean buttonsEnabled) {
        buttonPanel.setButtonsEnabled(buttonsEnabled);
    }

    /**
     * Sets the specified graph number to be the only one visible.
     *
     * @param graphIndex CoreApplication.Graph number to set as active.
     */
    public void setGraph(int graphIndex) {
        visualiserPanel.setGraph(graphIndex);
    }

    /**
     * Changes the visual to indicate whether the action is a play or pause.
     *
     * @param isPaused When true, the play symbol shows, when false the pause symbol shows.
     */
    public void setButtonPauseStateVisual(boolean isPaused) {
        buttonPanel.setPauseButtonState(isPaused);
    }
}

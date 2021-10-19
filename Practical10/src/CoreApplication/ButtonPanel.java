package CoreApplication;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Visualiser
 *
 * CoreApplication.ButtonPanel class:
 * Acts as the interaction interface to control what is shown in the
 * CoreApplication.VisualiserPanel. Messages are passed from this panel to the CoreApplication.Main
 * panel to be applied.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class ButtonPanel extends JPanel implements ActionListener, ChangeListener {
    /**
     * The default height of components in the CoreApplication.ButtonPanel.
     */
    private static int DEFAULT_ELEMENT_HEIGHT = 37;
    /**
     * The default width of most components in the CoreApplication.ButtonPanel.
     */
    private static final int DEFAULT_ELEMENT_WIDTH = 200;

    /**
     * Reference to the CoreApplication.Main class for passing updates.
     */
    private final DataVisualiserApplication main;

    //=============================================
    //  JComponents for interactive elements
    //=============================================
    /**
     * Labels to show the current values of their associated sliders.
     */
    private JLabel delayLabel, dataSetSizeLabel;
    /**
     * Buttons to begin the sorting, change the data to fill with specified configuration, and to reset data.
     */
    private JButton sortButton, fillButton, resetButton, searchButton, viewHistoryButton,
            pausePlayButton, stepThroughButton, stopButton, fastForwardButton;
    /**
     * Combo boxes to show the options for sorting algorithms, types of data to fill with, and graph options.
     */
    private JComboBox<String> sortAlgorithmChooser, fillChooser, graphChooser, searchAlgorithmChooser;
    /**
     * Sliders to change the time between updates, and the size of the data when the fill button is pressed.
     */
    private JSlider delaySlider, dataSetSizeSlider;
    /**
     * Toggles the sort direction between low to high and high to low.
     */
    private JCheckBox lowHighCheckBox;

    //=============================================
    //  Data used for showing the available options
    //=============================================
    /**
     * Manages the list of sort algorithms to pass when one is requested.
     */
    private List<SortAlgorithm> sortAlgorithmList;
    /**
     * Manages the list of sort algorithms to pass when one is requested.
     */
    private List<SearchAlgorithm> searchAlgorithmList;
    /**
     * Filled dynamically with the list of available sort algorithms using their strings.
     */
    private String[] sortAlgorithmOptionList;
    /**
     * Filled dynamically with the list of available sort algorithms using their strings.
     */
    private String[] searchAlgorithmOptionList;
    /**
     * The options for sorting that fill with random, already sorted, or custom input via an input panel.
     */
    private final String[] fillOptionList = {"Random", "Sorted", "Sorted Reverse", "Custom"};
    /**
     * CoreApplication.Graph options that change the graph visual at any time when changed.
     */
    private final String[] graphOptionList = {"Histogram","Line Plot","Dot Plot","Histogram+Line Plot","Dot+Line Plot"};
    /**
     * The time spent sleeping when showing visuals for the swaps and comparisons. Lower number = faster.
     */
    public static int pauseDelayTime = 100;
    /**
     * The history dialog to show current history of sorts and searches.
     */
    private final HistoryViewerDialog historyViewerDialog;
    /**
     * Reference to the current algorithm to play/pause/step through/stop
     */
    private AsyncAlgorithm currentAlgorithm;

    /**
     * Creates all the components ready for use.
     *
     * @param main Reference to the CoreApplication.Main class to pass updates.
     * @param elementHeight Preferred element height. Recommend ~25 for small, or 37 for default.
     * @param sortingAlgorithmClasses A list of names that MUST match the classes under SortingAlgorithms.className.
     * @param searchingAlgorithmClasses A list of names that MUST match the classes under SearchingAlgorithms.className.
     */
    public ButtonPanel(DataVisualiserApplication main, int elementHeight, String[] sortingAlgorithmClasses, String[] searchingAlgorithmClasses) {
        DEFAULT_ELEMENT_HEIGHT = elementHeight;
        this.main = main;
        setPreferredSize(new Dimension(220, VisualiserPanel.PANEL_HEIGHT));
        setBackground(Color.LIGHT_GRAY);
        createSortAlgorithms(sortingAlgorithmClasses);
        createSearchAlgorithms(searchingAlgorithmClasses);
        historyViewerDialog = new HistoryViewerDialog();
        createComponents();
        setButtonsEnabled(true);
    }

    /**
     * Creates all the components ready for use.
     *
     * @param main Reference to the CoreApplication.Main class to pass updates.
     * @param elementHeight Preferred element height. Recommend ~25 for small, or 37 for default.
     * @param algorithms A list of algorithms to be added to the algorithm lists.
     */
    public ButtonPanel(DataVisualiserApplication main, int elementHeight, List<AsyncAlgorithm> algorithms) {
        DEFAULT_ELEMENT_HEIGHT = elementHeight;
        this.main = main;
        setPreferredSize(new Dimension(220, VisualiserPanel.PANEL_HEIGHT));
        setBackground(Color.LIGHT_GRAY);
        sortAlgorithmList = new ArrayList<>();
        searchAlgorithmList = new ArrayList<>();

        for(AsyncAlgorithm algorithm : algorithms) {
            if(algorithm instanceof SortAlgorithm) {
                sortAlgorithmList.add((SortAlgorithm) algorithm);
            } else if(algorithm instanceof SearchAlgorithm) {
                searchAlgorithmList.add((SearchAlgorithm) algorithm);
            }
        }

        sortAlgorithmOptionList = new String[sortAlgorithmList.size()];
        for(int i = 0; i < sortAlgorithmOptionList.length; i++) {
            sortAlgorithmOptionList[i] = sortAlgorithmList.get(i).getName();
        }

        searchAlgorithmOptionList = new String[searchAlgorithmList.size()];
        for(int i = 0; i < searchAlgorithmOptionList.length; i++) {
            searchAlgorithmOptionList[i] = searchAlgorithmList.get(i).getName();
        }
        historyViewerDialog = new HistoryViewerDialog();
        createComponents();
        setButtonsEnabled(true);
    }

    /**
     * Creates all the available sorting algorithms and generates a list of Strings to represent them.
     *
     * @param classNames A list of names that MUST match the classes under SortingAlgorithms.className.
     */
    public void createSortAlgorithms(String[] classNames) {
        sortAlgorithmList = new ArrayList<>();
        StringBuilder failedNameList = new StringBuilder();

        for(String className : classNames) {
            boolean success = attemptLoadSortByClassName(className);
            if(!success) {
                failedNameList.append("\n").append(className);
            }
        }

        if(failedNameList.length() > 0) {
            JOptionPane.showMessageDialog(null,
                    "WARNING! The following sort algorithms failed to load. Make sure they were"
                            + " named correctly in Main.java if loading specific. \nOr if loading all, make "
                            + "sure that only classes extending SortAlgorithm are in the SortingAlgorithms folder."
                            + failedNameList);
        }

        sortAlgorithmOptionList = new String[sortAlgorithmList.size()];
        for(int i = 0; i < sortAlgorithmOptionList.length; i++) {
            sortAlgorithmOptionList[i] = sortAlgorithmList.get(i).getName();
        }
    }

    /**
     * Attempts to use reflection to generate a class of the specified name.
     *
     * @param className The name of a class that must conform to SortingAlgorithms.className.
     * @return True if the object was successfully created.
     */
    private boolean attemptLoadSortByClassName(String className) {
        try {
            Class<?> clazz = Class.forName("SortingAlgorithms." + className);
            if(!SortAlgorithm.class.isAssignableFrom(clazz)) {
                System.out.println("The class \""+className+"\" you are trying to load does not extend from SortAlgorithm!");
                return false;
            }
            Constructor<?> ctor = clazz.getConstructor(DataVisualiserApplication.class);
            SortAlgorithm object = (SortAlgorithm)ctor.newInstance(main);
            sortAlgorithmList.add(object);
            return true;
        } catch(ClassNotFoundException e) {
            System.out.println("Class Not Found Exception: " + e.getMessage());
        } catch (InstantiationException e) {
            System.out.println("Instantiation Exception: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.out.println("Invocation Target Exception: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.out.println("No Such Method Exception: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println("Illegal Access Exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Creates all the available searching algorithms and generates a list of Strings to represent them.
     *
     * @param classNames A list of names that MUST match the classes under SearchingAlgorithms.className.
     */
    public void createSearchAlgorithms(String[] classNames) {
        searchAlgorithmList = new ArrayList<>();
        StringBuilder failedNameList = new StringBuilder();

        for(String className : classNames) {
            boolean success = attemptLoadSearchByClassName(className);
            if(!success) {
                failedNameList.append("\n").append(className);
            }
        }

        if(failedNameList.length() > 0) {
            JOptionPane.showMessageDialog(null,
                    "WARNING! The following search algorithms failed to load. Make sure they were"
                            + " named correctly in Main.java if loading specific. \nOr if loading all, make "
                            + "sure that only classes extending SearchAlgorithm are in the SearchingAlgorithms folder."
                            + failedNameList);
        }

        searchAlgorithmOptionList = new String[searchAlgorithmList.size()];
        for(int i = 0; i < searchAlgorithmOptionList.length; i++) {
            searchAlgorithmOptionList[i] = searchAlgorithmList.get(i).getName();
        }
    }

    /**
     * Attempts to use reflection to generate a class of the specified name.
     *
     * @param className The name of a class that must conform to SortingAlgorithms.className.
     * @return True if the object was successfully created.
     */
    private boolean attemptLoadSearchByClassName(String className) {
        try {
            Class<?> clazz = Class.forName("SearchingAlgorithms." + className);
            if(!SearchAlgorithm.class.isAssignableFrom(clazz)) {
                System.out.println("The class \""+className+"\" you are trying to load does not extend from SearchAlgorithm!");
                return false;
            }
            Constructor<?> ctor = clazz.getConstructor(DataVisualiserApplication.class);
            SearchAlgorithm object = (SearchAlgorithm) ctor.newInstance(main);
            searchAlgorithmList.add(object);
            return true;
        } catch(ClassNotFoundException e) {
            System.out.println("Class Not Found Exception: " + e.getMessage());
        } catch (InstantiationException e) {
            System.out.println("Instantiation Exception: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.out.println("Invocation Target Exception: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.out.println("No Such Method Exception: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println("Illegal Access Exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks which interface element was interacted with and then executes an
     * appropriate action. Including the buttons, checkbox, and graph drop down menu.
     *
     * @param e Information about the event that caused this method to be called.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == sortButton) {
            SortAlgorithm sortAlgorithm = sortAlgorithmList.get(sortAlgorithmChooser.getSelectedIndex());
            sortAlgorithm.setOrderLowToHigh(lowHighCheckBox.isSelected());
            currentAlgorithm = sortAlgorithm;
            main.applyAlgorithm(currentAlgorithm);
        } else if(e.getSource() == fillButton) {
            main.fillData(choiceToFillMode(), dataSetSizeSlider.getValue());
        } else if(e.getSource() == resetButton) {
            main.reset();
        } else if(e.getSource() == lowHighCheckBox) {
            lowHighCheckBox.setText(lowHighCheckBox.isSelected() ? "Sort Low to High" : "Sort High to Low");
        } else if(e.getSource() == graphChooser) {
            main.setGraph(graphChooser.getSelectedIndex());
        } else if(e.getSource() == searchButton) {
            currentAlgorithm = searchAlgorithmList.get(searchAlgorithmChooser.getSelectedIndex());
            main.applyAlgorithm(currentAlgorithm);
        } else if(e.getSource() == viewHistoryButton) {
            historyViewerDialog.show();
        } else if(e.getSource() == pausePlayButton) {
            if(currentAlgorithm.isPaused.get()) {
                currentAlgorithm.resumeAlgorithm();
                pausePlayButton.setText("\u23F8\u23F8");
            } else {
                currentAlgorithm.pauseAlgorithm();
                pausePlayButton.setText("\u25B6");
            }
        } else if(e.getSource() == stopButton) {
            currentAlgorithm.killAlgorithm();
        } else if(e.getSource() == stepThroughButton) {
            currentAlgorithm.stepNextAlgorithm();
        } else if(e.getSource() == fastForwardButton) {
            currentAlgorithm.fastForwardAlgorithm();
        }
    }

    /**
     * Handles the changing states of the sliders. Updates the labels to show the
     * current value of the sliders.
     *
     * @param e Information about the event that caused this method to be called.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == delaySlider) {
            pauseDelayTime = delaySlider.getValue();
            delayLabel.setText("Delay: " + pauseDelayTime + " ms");
        } else if(e.getSource() == dataSetSizeSlider) {
            int snapValue = (int)(Math.round(dataSetSizeSlider.getValue()/10.0)*10);
            dataSetSizeLabel.setText("Data Set Size: " + snapValue);
        }
    }

    /**
     * Enables/Disables all buttons that should not be interacted with while
     * a sort is occurring to prevent any unexpected change to the data mid-sort.
     *
     * @param buttonsEnabled Enabled state of buttons set to this value.
     */
    public void setButtonsEnabled(boolean buttonsEnabled) {
        fillButton.setEnabled(buttonsEnabled);
        sortButton.setEnabled(buttonsEnabled);
        resetButton.setEnabled(buttonsEnabled);
        fillChooser.setEnabled(buttonsEnabled);
        sortAlgorithmChooser.setEnabled(buttonsEnabled);
        lowHighCheckBox.setEnabled(buttonsEnabled);
        dataSetSizeSlider.setEnabled(buttonsEnabled);
        searchAlgorithmChooser.setEnabled(buttonsEnabled);
        searchButton.setEnabled(buttonsEnabled);

        pausePlayButton.setEnabled(!buttonsEnabled);
        stepThroughButton.setEnabled(!buttonsEnabled);
        stopButton.setEnabled(!buttonsEnabled);
        fastForwardButton.setEnabled(!buttonsEnabled);
        setPauseButtonState(buttonsEnabled);
    }

    /**
     * Changes the visual to indicate whether the action is a play or pause.
     *
     * @param isPaused When true, the play symbol shows, when false the pause symbol shows.
     */
    public void setPauseButtonState(boolean isPaused) {
        pausePlayButton.setText(isPaused ? "\u25B6" : "| |");
    }

    /**
     * Used to convert the index from the fillOptionList to the enum used by CoreApplication.DataSet.
     *
     * @return An appropriate data fill mode based on the index.
     */
    private DataSet.DataSetFillMode choiceToFillMode() {
        return switch (fillChooser.getSelectedIndex()) {
            case 1 -> DataSet.DataSetFillMode.Sorted;
            case 2 -> DataSet.DataSetFillMode.SortedReverse;
            case 3 -> DataSet.DataSetFillMode.Custom;
            default -> DataSet.DataSetFillMode.Random;
        };
    }

    /**
     * Creates all the components for this panel. Then adds them to a separate JPanel to
     * make them centre vertically.
     */
    @SuppressWarnings("DuplicatedCode")
    private void createComponents() {
        JLabel title = createTitleLabel();
        delayLabel = createLabel("Delay: " + pauseDelayTime + " ms");
        delaySlider = createSlider(1,300, pauseDelayTime, 0);
        pausePlayButton = createSmallButton("\u23F8\u23F8");
        stepThroughButton = createSmallButton("\u25B7|");
        fastForwardButton = createSmallButton("\u25B6\u25B6");
        stopButton = createSmallButton("\u23F9");
        stopButton.setForeground(Color.RED);

        dataSetSizeLabel = createLabel("Data Set Size: 10");
        dataSetSizeSlider = createSlider(10,100, 10, 10);
        JLabel fillLabel = createLabel("Fill Data With");
        fillChooser = createComboBox(fillOptionList);
        fillButton = createButton("Fill Data");
        resetButton = createButton("Reset Data");

        JLabel sortLabel = createLabel("Sort Algorithms");
        sortAlgorithmChooser = createComboBox(sortAlgorithmOptionList);
        lowHighCheckBox = createCheckBox();
        sortButton = createButton("Sort");

        JLabel searchLabel = createLabel("Search Algorithms");
        searchAlgorithmChooser = createComboBox(searchAlgorithmOptionList);
        searchButton = createButton("Search");

        JLabel graphLabel = createLabel("Graph Type");
        graphChooser = createComboBox(graphOptionList);

        viewHistoryButton = createButton("View History");

        add(title);
        add(delayLabel);
        add(delaySlider);
        add(pausePlayButton);
        add(stepThroughButton);
        add(fastForwardButton);
        add(stopButton);
        add(dataSetSizeLabel);
        add(dataSetSizeSlider);
        add(fillLabel);
        add(fillChooser);
        add(fillButton);
        add(resetButton);
        add(sortLabel);
        add(sortAlgorithmChooser);
        add(lowHighCheckBox);
        add(sortButton);
        add(searchLabel);
        add(searchAlgorithmChooser);
        add(searchButton);
        add(graphLabel);
        add(graphChooser);
        add(viewHistoryButton);
        add(createAuthorLabel());
    }

    /**
     * Creates a label for the title.
     *
     * @return A JLabel prepared with all shared properties.
     */
    private JLabel createTitleLabel() {
        JLabel label = new JLabel("Options");
        label.setFont(new Font("Arial", Font.BOLD, DEFAULT_ELEMENT_HEIGHT-10));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(DEFAULT_ELEMENT_WIDTH, DEFAULT_ELEMENT_HEIGHT));
        return label;
    }

    /**
     * Creates a label with any message.
     *
     * @param text Message to display.
     * @return A JLabel prepared with all shared properties.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, Math.max(DEFAULT_ELEMENT_HEIGHT-20,10)));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(DEFAULT_ELEMENT_WIDTH, DEFAULT_ELEMENT_HEIGHT-10));
        return label;
    }

    /**
     * Creates a small text label with the author displayed.
     *
     * @return A JLabel prepared with all shared properties.
     */
    private JLabel createAuthorLabel() {
        JLabel label = new JLabel("Created by Peter Mitchell (2021)");
        label.setFont(new Font("Arial", Font.BOLD, 10));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(DEFAULT_ELEMENT_WIDTH, 15));
        return label;
    }

    /**
     * Creates a button with any message. Includes attaching listener.
     *
     * @param text Message to display.
     * @return A JButton prepared with all shared properties.
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(DEFAULT_ELEMENT_WIDTH, DEFAULT_ELEMENT_HEIGHT));
        return button;
    }

    /**
     * Creates a JComboBox with any list of options. Includes attaching listener.
     *
     * @param options The list of options to display.
     * @return A JComboBox prepared with all shared properties.
     */
    private JComboBox<String> createComboBox(String[] options) {
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setPreferredSize(new Dimension(DEFAULT_ELEMENT_WIDTH, DEFAULT_ELEMENT_HEIGHT));
        comboBox.addActionListener(this);
        return comboBox;
    }

    /**
     * Creates a JSlider with the specified properties. Includes attaching a listener.
     * Will optionally include snapping to tick spacing if tickSpacing is set to 1 or more.
     *
     * @param min Minimum value for the slider.
     * @param max Maximum value for the slider.
     * @param initial Start value for the slider.
     * @param tickSpacing 0 will not set the tick spacing with snapping, any value greater than 0 will snap to specified increments.
     * @return A JSlider prepared with all shared properties.
     */
    private JSlider createSlider(int min, int max, int initial, int tickSpacing) {
        JSlider slider = new JSlider(min,max);
        slider.setValue(initial);
        slider.setPreferredSize(new Dimension(DEFAULT_ELEMENT_WIDTH, DEFAULT_ELEMENT_HEIGHT));
        slider.addChangeListener(this);
        if(tickSpacing > 0) {
            slider.setSnapToTicks(true);
            slider.setMajorTickSpacing(tickSpacing);
        }
        return slider;
    }

    /**
     * Creates a JCheckBox with the text Sort Low to High and selected initial selection state with a listener attached.
     *
     * @return A JCheckBox with all shared properties.
     */
    private JCheckBox createCheckBox() {
        JCheckBox checkBox = new JCheckBox("Sort Low to High");
        checkBox.setPreferredSize(new Dimension(DEFAULT_ELEMENT_WIDTH, 20));
        checkBox.setSelected(true);
        checkBox.addActionListener(this);
        return checkBox;
    }

    /**
     * Creates a button with a listener and let the button determine its own size.
     *
     * @param text Text to display on the button.
     * @return The newly created small button.
     */
    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        return button;
    }
}

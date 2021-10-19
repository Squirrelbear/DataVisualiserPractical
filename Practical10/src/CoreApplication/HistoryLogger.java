package CoreApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Data Visualiser
 *
 * CoreApplication.HistoryLogger class:
 * Provides a static method to append a completed sort to the log file.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class HistoryLogger {
    /**
     * The file for logs to be saved in.
     */
    public static final String LOG_FILE_NAME = "HistoryLog.txt";
    /**
     * Used to notify the CoreApplication.HistoryViewerDialog that the file has been updated for new content to be shown.
     */
    public static boolean logUpdatedRecently = false;

    /**
     * Generates a log entry based on the algorithm and writes the log to the log file.
     *
     * @param asyncAlgorithm Algorithm that is to have a log entry written about it.
     */
    public static void appendToHistory(AsyncAlgorithm asyncAlgorithm) {
        String logMessage;
        if(asyncAlgorithm instanceof SortAlgorithm) {
            logMessage = createLogMessage((SortAlgorithm) asyncAlgorithm);
        } else {
            logMessage = createLogMessage((SearchAlgorithm) asyncAlgorithm);
        }
        writeDataToLogFile(logMessage);
        logUpdatedRecently = true;
    }

    /**
     * Creates an appropriate message based on the data that was just sorted.
     *
     * @param sortAlgorithm Reference to the sort algorithm that was just completed
     * @return The log message based on the algorithm that was completed as a String.
     */
    private static String createLogMessage(SortAlgorithm sortAlgorithm) {
        int[] unsortedElements = sortAlgorithm.getDataSet().getStoredValues();
        boolean lowToHigh = sortAlgorithm.getOrderLowToHigh();
        DataElement[] sortedElements = sortAlgorithm.getDataSet().getDataElements();
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(sortAlgorithm.getName());
        resultBuilder.append("\nSwaps: ");
        resultBuilder.append(sortAlgorithm.swaps);
        resultBuilder.append(" Comparisons: ");
        resultBuilder.append(sortAlgorithm.comparisons);
        resultBuilder.append(" Sorted: ");
        resultBuilder.append(((lowToHigh) ? "Low to High" : "High to Low"));
        resultBuilder.append("\nDataSize: ");
        resultBuilder.append(unsortedElements.length);
        resultBuilder.append("\nUnsorted Data: ");
        for (int unsortedElement : unsortedElements) {
            resultBuilder.append(unsortedElement);
            resultBuilder.append(" ");
        }
        resultBuilder.append("\nSorted Data: ");
        String sortedSuccessMessage = "Yes";
        for(int i = 0 ; i < sortedElements.length; i++) {
            resultBuilder.append(sortedElements[i].getValue());
            resultBuilder.append(" ");
            if(i > 0 && ((lowToHigh && sortedElements[i].getValue() < sortedElements[i-1].getValue())
                          || (!lowToHigh && sortedElements[i].getValue() > sortedElements[i-1].getValue()))) {
                sortedSuccessMessage = "NO - SORT FAILED!";
            }
        }
        resultBuilder.append("\nSorted Successfully: ");
        resultBuilder.append(sortedSuccessMessage);
        resultBuilder.append("\n\n");
        return resultBuilder.toString();
    }

    /**
     * Creates an appropriate message based on the data that was just searched.
     *
     * @param searchAlgorithm Reference to the search algorithm that was just completed
     * @return The log message based on the algorithm that was completed as a String.
     */
    private static String createLogMessage(SearchAlgorithm searchAlgorithm) {
        boolean lowToHigh = searchAlgorithm.getOrderLowToHigh();
        DataElement[] elements = searchAlgorithm.getDataSet().getDataElements();
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(searchAlgorithm.getName());
        resultBuilder.append("\nComparisons: ");
        resultBuilder.append(searchAlgorithm.comparisons);
        resultBuilder.append(" Guessed Order: ");
        resultBuilder.append(((lowToHigh) ? "Low to High" : "High to Low"));
        resultBuilder.append(" Sorted: ");
        resultBuilder.append(searchAlgorithm.isDataSorted);
        resultBuilder.append("\nDataSize: ");
        resultBuilder.append(elements.length);
        resultBuilder.append("\nSearched Data: ");
        for (DataElement element : elements) {
            resultBuilder.append(element.getValue());
            resultBuilder.append(" ");
        }
        resultBuilder.append("\nLogged Steps searching for: ");
        resultBuilder.append(searchAlgorithm.target.getValue());
        for(String loggedStep : searchAlgorithm.getLoggedSteps()) {
            resultBuilder.append("\n");
            resultBuilder.append(loggedStep);
        }
        resultBuilder.append("\nSearch Result: ");
        resultBuilder.append(searchAlgorithm.getSearchSummaryString());
        resultBuilder.append("\n\n");
        return resultBuilder.toString();
    }

    /**
     * If no file exists with the log file name it is created.
     * Then the file is opened and appended to writing any data specified.
     *
     * @param data Data to append to the file.
     */
    public static void writeDataToLogFile(String data) {
        try {
            if(!Files.exists(Paths.get(LOG_FILE_NAME))) {
                Files.createFile(Paths.get(LOG_FILE_NAME));
            }
            Files.write(Paths.get(LOG_FILE_NAME), data.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            System.out.println("ERROR: failed to write to log file.\n"+e.getMessage()+"\n");
            e.printStackTrace();
        }
    }
}

package CoreApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Data Visualiser
 *
 * CoreApplication.ReportGenerator class:
 * This class takes the logged history and generates a report based on
 * its contents. This includes a report based on both sorting and searching.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class ReportGenerator {
    /**
     * HistoryData class:
     * This is used as a data store to contain information about
     * the data read from the log file to generate the report.
     */
    static class HistoryData {
        /**
         * Name of the algorithm used to generate this entry, and the data it was applied to.
         */
        public String algorithmName, data;
        /**
         * Search data uses: comparisons, dataSize, target, steps, and foundTarget.
         * Sort data uses: comparisons, swaps, and dataSize.
         */
        public int comparisons, swaps, dataSize, target, steps, foundTarget;
        /**
         * Low to High indicates the direction of sorting, or assumed direction for searching.
         * isSorted indicates if the data was successfully sorted, or when searched if there data was already sorted.
         * isSortFunction indicates if the data stored in this element is either for a sort or a search.
         */
        boolean lowToHigh, isSorted, isSortFunction;
    }

    /**
     * The history data that has been loaded into the CoreApplication.ReportGenerator.
     */
    private final List<HistoryData> history;

    /**
     * Creates a new CoreApplication.ReportGenerator with an empty history ready for generateReport().
     */
    public ReportGenerator() {
        history = new ArrayList<>();
    }

    /**
     * Attempts to load the history file and read its contents to generate a report.
     * The report is generated if possible and logged out to the same file.
     *
     * @return Success of the report generation.
     */
    public boolean generateReport() {
        if(!Files.exists(Path.of(HistoryLogger.LOG_FILE_NAME))) {
            System.out.println("Failed to load file. Did not exist.");
            return false;
        }
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(new File(HistoryLogger.LOG_FILE_NAME));
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load file.");
            return false;
        }
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("\nBEGIN REPORT\n");
        readAllHistoryData(fileScanner, reportBuilder);
        fileScanner.close();

        addSortReportOnSpecificDatasets(reportBuilder);
        addSearchReportOnSpecificDatasets(reportBuilder);
        reportBuilder.append("\nEND REPORT\n\n");
        HistoryLogger.writeDataToLogFile(reportBuilder.toString());
        HistoryLogger.logUpdatedRecently = true;
        return true;
    }

    /**
     * Filters and sorts the data for searches to generate a report focusing on the results for
     * searches within unique ordered datasets.
     *
     * @param reportBuilder The report to be logged once complete.
     */
    private void addSearchReportOnSpecificDatasets(StringBuilder reportBuilder) {
        List<HistoryData> onlySortHistory = history.stream().filter(historyData -> !historyData.isSortFunction).collect(Collectors.toList());
        List<HistoryData> uniqueOrderedData = onlySortHistory.stream().filter(distinctByKey(historyData -> historyData.data)).collect(Collectors.toList());
        if(uniqueOrderedData.size() == 0) {
            reportBuilder.append("Skipping report on searching. No searches found.");
            return;
        }
        reportBuilder.append("\nReporting History Based on Searching Specific Datasets\n");
        for(HistoryData uniqueOrderedDataElement : uniqueOrderedData) {
            List<HistoryData> sharedDataElements = onlySortHistory.stream().filter(historyData -> historyData.data.equals(uniqueOrderedDataElement.data)).collect(Collectors.toList());
            reportBuilder.append("Data to be Searched: ");
            reportBuilder.append(uniqueOrderedDataElement.data);
            reportBuilder.append("\n");
            reportBuilder.append("Is Sorted: ");
            reportBuilder.append(uniqueOrderedDataElement.isSorted);
            reportBuilder.append(" Order: ");
            reportBuilder.append(uniqueOrderedDataElement.lowToHigh ? "Low to High" : "High to Low");
            reportBuilder.append("\n");
            List<HistoryData> uniqueSearches = sharedDataElements.stream().filter(distinctByKey(historyData -> historyData.target)).sorted(Comparator.comparingInt(h -> h.target)).collect(Collectors.toList());
            for(HistoryData uniqueSearch : uniqueSearches) {
                List<HistoryData> matchSearch = sharedDataElements.stream().filter(historyData -> historyData.target == uniqueSearch.target).collect(Collectors.toList());
                reportBuilder.append("Search Target: ");
                reportBuilder.append(uniqueSearch.target);
                reportBuilder.append("\n");
                for(HistoryData data : matchSearch) {
                    reportBuilder.append(data.algorithmName);
                    reportBuilder.append(" Comparisons: ");
                    reportBuilder.append(data.comparisons);
                    reportBuilder.append(" Found At: ");
                    reportBuilder.append(data.foundTarget);
                    reportBuilder.append(" With Steps: ");
                    reportBuilder.append(data.steps);
                    reportBuilder.append("\n");
                }
            }
            reportBuilder.append("\n");
        }
        reportBuilder.append("End of Reporting History Based on Searching Specific Datasets\n\n");
    }

    /**
     * Filters and sorts the data related to sorts and generates a report based on the repeated use
     * of the same datasets.
     *
     * @param reportBuilder The report to be logged to once complete.
     */
    private void addSortReportOnSpecificDatasets(StringBuilder reportBuilder) {
        List<HistoryData> onlySortHistory = history.stream().filter(historyData -> historyData.isSortFunction).collect(Collectors.toList());
        List<HistoryData> uniqueOrderedData = onlySortHistory.stream().filter(distinctByKey(historyData -> historyData.data)).collect(Collectors.toList());
        if(uniqueOrderedData.size() == 0) {
            reportBuilder.append("Skipping report on sorting. No sorts found.");
            return;
        }
        reportBuilder.append("\nReporting History Based on Sorting Specific Datasets\n");
        for(HistoryData uniqueOrderedDataElement : uniqueOrderedData) {
            List<HistoryData> sharedDataElements = onlySortHistory.stream().filter(historyData -> historyData.data.equals(uniqueOrderedDataElement.data)).collect(Collectors.toList());
            reportBuilder.append("Data to be Sorted: ");
            reportBuilder.append(uniqueOrderedDataElement.data);
            reportBuilder.append("\n");
            reportBuilder.append("Data Size: ");
            reportBuilder.append(uniqueOrderedDataElement.dataSize);
            reportBuilder.append("\n");
            List<HistoryData> sortedLowToHigh = sharedDataElements.stream().filter(historyData -> historyData.lowToHigh).collect(Collectors.toList());
            List<HistoryData> sortedHighToLow = sharedDataElements.stream().filter(historyData -> !historyData.lowToHigh).collect(Collectors.toList());

            addSwapsAndComparisonsToReport(reportBuilder,sortedLowToHigh, "Sorted Order: Low to High\n");
            addSwapsAndComparisonsToReport(reportBuilder,sortedHighToLow,"Sorted Order: High to Low\n");
        }
        reportBuilder.append("End of Reporting History Based on Sorting Specific Datasets\n\n");
    }

    /**
     * Generates a subset of the report generation for sorted data that is reused for both when
     * data is sorted low to high and high to low for a single data set. It skips over outputting anything
     * if there is no match for the supplied filtered dataset.
     *
     * @param reportBuilder The report to be logged to once complete.
     * @param historyDataCollection The filtered data to output lines about.
     * @param descriptor A descriptor to identify the content if there are elements.
     */
    private void addSwapsAndComparisonsToReport(StringBuilder reportBuilder, List<HistoryData> historyDataCollection, String descriptor) {
        if(historyDataCollection.size() != 0) {
            reportBuilder.append(descriptor);
            for(HistoryData sharedDataElement : historyDataCollection) {
                reportBuilder.append(sharedDataElement.algorithmName);
                reportBuilder.append(" Swaps: ");
                reportBuilder.append(sharedDataElement.swaps);
                reportBuilder.append(" Comparisons: ");
                reportBuilder.append(sharedDataElement.comparisons);
                reportBuilder.append("\n");
            }
            reportBuilder.append("\n");
        }
    }

    /**
     * Used to apply a filter for unique values.
     *
     * @param keyExtractor Lambda to use for filtering.
     * @param <T> DataType to use for Lambda.
     * @return True if the filtered data does not yet contain a element based on the lambda.
     */
    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    /**
     * Wipes the current loaded history and reads in a fresh view of the history file ready for analysis.
     * Any malformed data that is read in will be counted as an error. This will only happen if users
     * modify the original data in unexpected ways.
     *
     * @param fileScanner Reference to the file to read in the data.
     * @param reportBuilder Reference to the report builder to report on success of reading the data.
     */
    private void readAllHistoryData(Scanner fileScanner, StringBuilder reportBuilder) {
        history.clear();
        int sortCount = 0, searchCount = 0, failedReadCount = 0, existingReportsCount = 0;
        while(fileScanner.hasNextLine()) {
            String dataLine = fileScanner.nextLine();
            if(dataLine.equals("BEGIN REPORT")) {
                existingReportsCount++;
                // keep reading until reaching the "END REPORT"
                while(fileScanner.hasNextLine() && !dataLine.equals("END REPORT")) {
                    dataLine = fileScanner.nextLine();
                }
                if(fileScanner.hasNextLine())
                    fileScanner.nextLine(); // Empty separator line
            } else {
                try {
                    HistoryData data = null;
                    if(dataLine.contains("Search")) {
                        data = readAsSearch(dataLine, fileScanner);
                        searchCount++;
                    } else if(dataLine.contains("Sort")) {
                        data = readAsSort(dataLine, fileScanner);
                        sortCount++;
                    }
                    if(data != null) {
                        history.add(data);
                    }
                } catch(Exception e) {
                    failedReadCount++;
                }
            }
        }
        // Output a summary based on what was read in.
        reportBuilder.append("Analysed Data and found: ");
        reportBuilder.append(existingReportsCount);
        reportBuilder.append(" existing report(s), ");
        reportBuilder.append(sortCount);
        reportBuilder.append(" sort occurrence(s), ");
        reportBuilder.append(searchCount);
        reportBuilder.append(" search occurrence(s), and ");
        reportBuilder.append(failedReadCount);
        reportBuilder.append(" error(s) while reading.\n");
    }

    /**
     * Parses the data from the file expecting a specific format to match
     * how the CoreApplication.HistoryLogger wrote the sort data. Any error from reading here
     * will fail to generate the entry and be caught by the readAllHistoryData method.
     *
     * @param algorithmName Name of the algorithm used for sorting.
     * @param fileScanner Reference to the fileScanner to read in the data.
     * @return A valid HistoryData object representing a sort type.
     */
    private HistoryData readAsSort(String algorithmName, Scanner fileScanner) {
        HistoryData historyData = new HistoryData();
        historyData.isSortFunction = true;
        historyData.algorithmName = algorithmName;
        fileScanner.next(); // "Swaps: "
        historyData.swaps = fileScanner.nextInt();
        fileScanner.next(); // "Comparisons: "
        historyData.comparisons = fileScanner.nextInt();
        String lowHigh = fileScanner.nextLine(); // "Sorted: Low to High" or "Sorted: High to Low"
        historyData.lowToHigh = lowHigh.charAt(lowHigh.length()-1) == 'h';
        fileScanner.next(); // "DataSize: "
        historyData.dataSize = fileScanner.nextInt();
        fileScanner.next(); // "Unsorted "
        fileScanner.next(); // "Data:"
        historyData.data = fileScanner.nextLine().trim();
        fileScanner.nextLine(); // "Sorted Data: ...."
        fileScanner.next(); // "Sorted"
        fileScanner.next(); // "Successfully:"
        String successSort = fileScanner.next();
        historyData.isSorted = successSort.charAt(0) == 'Y';
        fileScanner.nextLine(); // Empty
        fileScanner.nextLine(); // Empty line leading to next entry
        return historyData;
    }

    /**
     * Parses the data from the file expecting a specific format to match
     * how the CoreApplication.HistoryLogger wrote the search data. Any error from reading here
     * will fail to generate the entry and be caught by the readAllHistoryData method.
     *
     * @param algorithmName Name of the algorithm used for searching.
     * @param fileScanner Reference to the fileScanner to read in the data.
     * @return A valid HistoryData object representing a search type.
     */
    private HistoryData readAsSearch(String algorithmName, Scanner fileScanner) {
        HistoryData historyData = new HistoryData();
        historyData.isSortFunction = false;
        historyData.algorithmName = algorithmName;
        fileScanner.next(); // "Comparisons: "
        historyData.comparisons = fileScanner.nextInt();
        String lowHighAndSorted = fileScanner.nextLine();
        historyData.lowToHigh = lowHighAndSorted.contains("Low to High");
        historyData.isSorted = lowHighAndSorted.contains("true");
        fileScanner.next(); // "DataSize: "
        historyData.dataSize = fileScanner.nextInt();
        fileScanner.next(); // "Searched "
        fileScanner.next(); // "Data:"
        historyData.data = fileScanner.nextLine().trim();
        String target = fileScanner.nextLine().replace("Logged Steps searching for: ", "").trim();
        historyData.target = Integer.parseInt(target);
        String readLine;
        int maxStep = 1;
        while(Character.isDigit((readLine = fileScanner.nextLine()).charAt(0))) {
            maxStep = Integer.parseInt(readLine.split("\\.")[0]);
        }
        historyData.steps = maxStep;
        if(readLine.contains("found at")) {
            historyData.foundTarget = Integer.parseInt(readLine.split("\\[")[1].split("]")[0]);
        } else {
            historyData.foundTarget = -1;
        }
        fileScanner.nextLine(); // Empty line leading to next entry
        return historyData;
    }
}

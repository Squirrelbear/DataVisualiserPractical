package CoreApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Data Visualiser
 *
 * CoreApplication.HistoryViewerDialog class:
 * A simple dialog to view the contents of the history text file.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class HistoryViewerDialog implements ActionListener {
    /**
     * Reference to the JFrame for making it visible.
     */
    private final JFrame historyFrame;
    /**
     * Buttons to hide the dialog, attempt to delete the file, and generate a report.
     */
    private JButton hideButton, deleteFileButton, generateReportButton;
    /**
     * Text area where the file is read into.
     */
    private JTextArea historyTextArea;
    /**
     * A label to indicate the state of the file.
     */
    private JLabel fileStatusLabel;

    /**
     * Initialises the dialog, reads in the file to the text area, and
     * starts a timer to periodically check if there are updates to
     * require a new read of the file.
     */
    public HistoryViewerDialog() {
        historyFrame = new JFrame("Data Visualiser History");
        historyFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        historyFrame.setResizable(false);
        historyFrame.getContentPane().add(createContentPanel());
        historyFrame.pack();

        loadFromFile();
        Timer updateTimer = new Timer(100, this);
        updateTimer.start();
    }

    /**
     * Makes the dialog visible.
     */
    public void show() {
        historyFrame.setVisible(true);
    }

    /**
     * Creates all the necessary elements to display as part of the dialog.
     *
     * @return A JPanel containing all the elements to be displayed in the dialog.
     */
    private JPanel createContentPanel() {
        JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(800, 390));
        result.setLayout(new BorderLayout());

        historyTextArea = new JTextArea(21,70);
        historyTextArea.setEditable(false);
        JScrollPane textScrollPane = new JScrollPane(historyTextArea);
        hideButton = new JButton("Hide Dialog");
        hideButton.addActionListener(this);
        deleteFileButton = new JButton("Delete History File");
        deleteFileButton.addActionListener(this);

        JLabel historyFileLabel = new JLabel("History Stored In: ");
        JTextField historyFileNameTextField = new JTextField(HistoryLogger.LOG_FILE_NAME);
        historyFileNameTextField.setEditable(false);
        fileStatusLabel = new JLabel("File Status: Pending");
        fileStatusLabel.setPreferredSize(new Dimension(220,10));

        generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(this);

        JPanel leftPanel = new JPanel();
        leftPanel.add(historyFileLabel);
        leftPanel.add(historyFileNameTextField);
        leftPanel.add(fileStatusLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.add(generateReportButton);
        rightPanel.add(hideButton);
        rightPanel.add(deleteFileButton);

        result.add(textScrollPane, BorderLayout.NORTH);
        result.add(leftPanel, BorderLayout.WEST);
        result.add(rightPanel, BorderLayout.EAST);

        return result;
    }

    /**
     * If the file exists the user is asked if they wish to delete it.
     * Only if they say yes it will attempt to delete the file.
     */
    private void attemptDeleteFile() {
        // Only do something if the file exists.
        if(Files.exists(Paths.get(HistoryLogger.LOG_FILE_NAME))) {
            int option = JOptionPane.showConfirmDialog(historyFrame, "Are you sure you want to delete \""
                                                                + HistoryLogger.LOG_FILE_NAME + "\"?");
            if(option != JOptionPane.YES_OPTION) return;
            // Only continue if the user answered yes to wanting to delete it.
            try {
                Files.delete(Path.of(HistoryLogger.LOG_FILE_NAME));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(historyFrame, "Error: Failed to delete file.");
                return;
            }
            // Clear the text area as the file has now been deleted.
            historyTextArea.setText("");
            fileStatusLabel.setText("File Status: Deleted");
        }
    }

    /**
     * Attempts to read in from the file. If it is successfully read, the data is
     * read and stored into the text area.
     */
    private void loadFromFile() {
        if(!Files.exists(Paths.get(HistoryLogger.LOG_FILE_NAME))) {
            fileStatusLabel.setText("File Status: File Does Not Exist Yet.");
            return;
        }

        try {
            FileReader reader = new FileReader(HistoryLogger.LOG_FILE_NAME);
            historyTextArea.read(reader, null);
            reader.close();
            // Move to the end of the file to see the most recent entry.
            int len = historyTextArea.getDocument().getLength();
            historyTextArea.setCaretPosition(len);
            fileStatusLabel.setText("File Status: Loaded");
        } catch (IOException e) {
            fileStatusLabel.setText("File Status: Failed Load");
        }
    }

    /**
     * If the event was from the timer the file is reloaded if
     * it has changed recently, if the button was the hide button
     * it will hide the dialog, and if the delete button is pressed
     * it will attempt to delete the file.
     *
     * @param e The event that triggered this actionPerformed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof Timer) {
            // Only read the file in again if it has recently changed.
            if(HistoryLogger.logUpdatedRecently) {
                HistoryLogger.logUpdatedRecently = false;
                loadFromFile();
            }
        } else if(e.getSource() == hideButton) {
            historyFrame.setVisible(false);
        } else if(e.getSource() == deleteFileButton) {
            attemptDeleteFile();
        } else if(e.getSource() == generateReportButton) {
            ReportGenerator generator = new ReportGenerator();
            boolean success = generator.generateReport();
            if(!success) {
                JOptionPane.showMessageDialog(historyFrame,
                        "The report could not be generated due to an error. "
                        + "Make sure the history file exists and you have valid data inside it.");
            }
        }
    }
}

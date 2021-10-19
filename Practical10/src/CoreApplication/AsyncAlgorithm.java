package CoreApplication;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Data Visualiser
 *
 * CoreApplication.AsyncAlgorithm class:
 * Defines a template to use for any CoreApplication.SortAlgorithm or CoreApplication.SearchAlgorithm by providing the
 * behind the scenes management of the linking of data, and running
 * the process as a separate Thread.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public abstract class AsyncAlgorithm implements Runnable {
    /**
     * Reference to CoreApplication.Main to manage button toggles.
     */
    private final DataVisualiserApplication main;
    /**
     * The name of the algorithm used to display visually for selection.
     */
    private final String algorithmName;
    /**
     * Reference to the CoreApplication.DataSet.
     */
    protected DataSet dataSet;
    /**
     * Reference to the DataElements to be modified directly during sorting.
     */
    protected DataElement[] dataElements;
    /**
     * The Thread used for managing this sort algorithm.
     */
    private Thread algorithmThread;
    /**
     * Multiplier to augment the amount of time to be slept.
     */
    protected double sleepMultiplier;
    /**
     * When true, the execution will pause at predetermined places waiting for a play or step through action.
     */
    protected final AtomicBoolean isPaused;
    /**
     * When true, after a pause has occurred the pause will be immediately reapplied.
     */
    protected final AtomicBoolean isStepping;
    /**
     * When true, the thread will die at the next possible time.
     */
    private final AtomicBoolean killThread;
    /**
     * When true, the delay time is ignored forcing the algorithm to run at max speed.
     */
    private final AtomicBoolean fastForward;

    /**
     * Creates the algorithm ready for a beginAlgorithm() to start it
     *
     * @param algorithmName Name of the Algorithm.
     * @param main Reference to main or managing button states.
     */
    public AsyncAlgorithm(String algorithmName, DataVisualiserApplication main) {
        this.algorithmName = algorithmName;
        this.main = main;
        algorithmThread = null;
        sleepMultiplier = 1;
        isPaused = new AtomicBoolean();
        isStepping = new AtomicBoolean();
        killThread = new AtomicBoolean();
        fastForward = new AtomicBoolean();
    }

    /**
     * Prepares the algorithm by linking the CoreApplication.DataSet, and then starts a thread that will call run() to start the
     * performAlgorithm() method in any class that has extended from this class.
     *
     * @param dataSet Reference to the CoreApplication.DataSet to apply the algorithm to.
     */
    @SuppressWarnings("deprecation")
    public void beginAlgorithm(DataSet dataSet) {
        // Disable the buttons in the CoreApplication.ButtonPanel until after sorting is done.
        main.setButtonsEnabled(false);
        isPaused.set(false);
        isStepping.set(false);
        killThread.set(false);
        fastForward.set(false);
        this.dataSet = dataSet;
        this.dataElements = dataSet.getDataElements();
        // This is a hack to fix an issue with the thread not being stopped properly under some conditions.
        // If the thread is still active when a new one is being started, kill the thread.
        // It should not be running anyway. Only a problem right after the stop button has been used.
        if(algorithmThread != null && algorithmThread.isAlive()) {
            algorithmThread.stop();
            // In the case that the thread did need to be stopped this probably needs to be applied.
            dataSet.removeAllDataStates();
            algorithmThread = null;
        }
        if (algorithmThread == null) {
            algorithmThread = new Thread(this, algorithmName);
            algorithmThread.start();
        }
    }

    /**
     * Called when a thread begins the CoreApplication.AsyncAlgorithm by calling start() via the beginAlgorithm() method.
     */
    public void run() {
        performAlgorithm();
        HistoryLogger.appendToHistory(this);
        algorithmThread = null;
        main.setButtonsEnabled(true);
    }

    /**
     * Terminates a thread early through use of the stop button. No data will be logged for the run.
     * Termination happens after the next sleep period if it is NOT paused in waitForPause().
     * Thread interruption from this method only cleanly kills the Thread if the Thread is already paused
     * with the process in a Thread.onSpinWait() state.
     */
    public void killAlgorithm() {
        killThread.set(true);
        algorithmThread.interrupt();
        if(isPaused.get()) {
            threadKillCleanup();
        }
    }

    /**
     * Pauses the algorithm at the next colour marking point until an appropriate continue or terminate occurs.
     */
    public void pauseAlgorithm() {
        isPaused.set(true);
    }

    /**
     * Resumes normal playing of the algorithm.
     */
    public void resumeAlgorithm() {
        isPaused.set(false);
    }

    /**
     * This will resume play if it is paused and pause it again once a pause point is reached.
     */
    public void stepNextAlgorithm() {
        isStepping.set(true);
        isPaused.set(false);
    }

    /**
     * Resumes the algorithm if it is paused and skips all remaining time delays.
     */
    public void fastForwardAlgorithm() {
        fastForward.set(true);
        isPaused.set(false);
    }

    /**
     * Infinitely loops while a pause is active. Will reapply the pause after it ends if
     * the stepNext button has been pressed.
     */
    @SuppressWarnings("deprecation")
    protected void waitForPause() {
        if(isPaused.get()) {
            main.setButtonPauseStateVisual(true);
        }
        while(isPaused.get()) {
            Thread.onSpinWait();
        }
        // Pause again as soon as the next waitForPause() is called.
        if(isStepping.get()) {
            isStepping.set(false);
            isPaused.set(true);
        } else {
            main.setButtonPauseStateVisual(false);
        }
        // If the thread is supposed to be ended, terminate forcefully and clean up.
        if(killThread.get()) {
            threadKillCleanup();
            Thread.currentThread().stop();
        }
    }

    /**
     * Gets the name of this Algorithm.
     *
     * @return The name of this Algorithm.
     */
    public String getName() {
        return algorithmName;
    }

    /**
     * Gets the CoreApplication.DataSet for this Algorithm.
     *
     * @return Reference to the CoreApplication.DataSet being used by this Algorithm.
     */
    public DataSet getDataSet() {
        return dataSet;
    }

    /**
     * Performs the algorithm based on the implementation of a class that extends this class.
     */
    public abstract void performAlgorithm();

    /**
     * Pauses the sort algorithm for a period based on the slider defined in the CoreApplication.ButtonPanel.
     * Does nothing if the algorithm is currently fast forwarding.
     */
    protected void sleep() {
        sleep(1);
    }

    /**
     * Pauses the sort algorithm for a period based on the slider defined in the CoreApplication.ButtonPanel.
     * Does nothing if the algorithm is currently fast forwarding.
     *
     * @param multiplier Multiplies the sleep by the specified amount.
     */
    protected void sleep(double multiplier) {
        if(fastForward.get()) {
            return;
        }
        try {
            Thread.sleep((int)(ButtonPanel.pauseDelayTime*multiplier*sleepMultiplier));
        } catch (InterruptedException e) {
            // Do nothing, ignore that the sleep was interrupted.
        }
    }

    /**
     * Sets the state of both elements to comparing, then delays for any
     * sleep period and pausing followed by resetting the elements back
     * to no colour state.
     *
     * @param elementA Reference to the first element being compared.
     * @param elementB Reference to the second element being compared.
     */
    protected void showDelayedCompareVisual(DataElement elementA, DataElement elementB) {
        elementA.setSortColourState(DataElement.SortColourState.Comparing);
        elementB.setSortColourState(DataElement.SortColourState.Comparing);
        sleep(2);
        waitForPause();
        elementA.setSortColourState(DataElement.SortColourState.None);
        elementB.setSortColourState(DataElement.SortColourState.None);
    }

    /**
     * Shows a status message to indicate algorithm was ended prematurely,
     * clears all state data from the dataset, wipes the reference to the thread,
     * and activates the buttons again.
     */
    private void threadKillCleanup() {
        dataSet.setStatusLabel("Algorithm ended with stop. Logging skipped.");
        // Wipe all the colours
        dataSet.removeAllDataStates();
        //algorithmThread = null;
        main.setButtonsEnabled(true);
    }
}

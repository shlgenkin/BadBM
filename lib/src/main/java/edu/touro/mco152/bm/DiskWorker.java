package edu.touro.mco152.bm;

import edu.touro.mco152.bm.commandOperations.*;
import edu.touro.mco152.bm.observers.DBObserver;
import edu.touro.mco152.bm.observers.RunPanelObserver;
import edu.touro.mco152.bm.observers.RulesMessageObserver;
import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;

import static edu.touro.mco152.bm.App.*;

/**
 * Run the disk benchmarking as a Swing-compliant thread (only one of these threads can run at once.) Cooperates with
 * Swing to provide and make use of interim and final progress and information, which is also recorded as needed to the
 * persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes. The DiskRun
 * class is used to keep track of and persist info about each benchmark at a higher level (a run), while the DiskMark
 * class described each iteration's result, which is displayed by the UI as the benchmark run progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks. It is instantiated by the startBenchmark()
 * method.
 * <p>
 * To be Swing compliant this class extends SwingWorker and declares that its final return (when doInBackground() is
 * finished) is of type Boolean, and declares that intermediate results are communicated to Swing using an instance of
 * the DiskMark class.
 */

public class DiskWorker {

    private static DiskWorkerInterface DWModel;

    public DiskWorker(DiskWorkerInterface inputDWModel) {
        // Assign argument to DWModel
        DWModel = inputDWModel;
        ReceiverWriteRead.unregisterAllObservers();//Clear observer list from previous usages.
        //Register necessary observers
        ReceiverWriteRead.registerObserver(new RunPanelObserver());
        ReceiverWriteRead.registerObserver(new DBObserver());
        ReceiverWriteRead.registerObserver(new RulesMessageObserver());
    }

    public boolean startDiskWorker() {
        /**
         * The previous configuration was (which may help with QA):
         * We 'got here' because: a) End-user clicked 'Start' on the benchmark UI,
         * which triggered the start-benchmark event associated with the App::startBenchmark()
         * method.  b) startBenchmark() then instantiated a DiskWorker, and called
         * its (super class's) execute() method, causing Swing to eventually
         * call this doInBackground() method.
         *
         * Currently, this method (previously named doInBackground) was renamed to startDiskWorker()
         * and is somewhat different than before. Firstly, instead of being called by
         * Swing automatically (i.e. event-driven) it is now called manually, sometimes from the DWModel,
         * to facilitate Swing independence. Secondly, this method utilizes the command pattern by instantiating an
         * executor and processing certain operations, currently the read and write operations, via the executor.
         * The current way of utilizing the command pattern is by instantiating a commandBuilder and only specifying
         * the common parameters that will be used for all operations. The commandBuilder is reused for each operation
         * with the unique parameters specified with each new use followed by a call to the build() method.
         */
        System.out.println("*** starting new worker thread");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        msg("num files: " + App.numOfMarks + ", num blks: " + App.numOfBlocks
                + ", blk size (kb): " + App.blockSizeKb + ", blockSequence: " + App.blockSequence);
        Gui.updateLegend();  // init chart legend info
        var serialCommandExecutor = new SerialCommandExecutor(); // init the executor to process some operations
        var commandBuilder = new CommandBuilder(); //Instantiate a command builder that will be used to process the commands
        commandBuilder.setDWModel(DWModel).setNumOfMarks(App.numOfMarks).setNumOfBlocks(App.numOfBlocks).
                setBlockSizeKB(App.blockSizeKb).setBlockSeq(App.blockSequence); //Specify all details except the type of command to be used.
        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }

        /**
         * The GUI allows either a write, read, or both types of BMs to be started. They are done serially.
         */

        if (App.writeTest) {
            serialCommandExecutor.executeCommand(
                    commandBuilder.setCommandType("write").build());
            //Process a command via the executor with the commandBuilder and specify the type of command which is "write"
        }

        /**
         * Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
         * make it more 'fair'. For example a networking benchmark might close and re-open sockets,
         * a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
         */

        // try renaming all files to clear catch
        if (App.readTest && App.writeTest && !DWModel.isCancelledDWI()) {
            JOptionPane.showMessageDialog(Gui.mainFrame,
                    "For valid READ measurements please clear the disk cache by\n" +
                            "using the included RAMMap.exe or flushmem.exe utilities.\n" +
                            "Removable drives can be disconnected and reconnected.\n" +
                            "For system drives use the WRITE and READ operations \n" +
                            "independantly by doing a cold reboot after the WRITE",
                    "Clear Disk Cache Now", JOptionPane.PLAIN_MESSAGE);
        }

        // Same as above, just for Read operations instead of Writes. Specify the type "read" for the commandBuilder.
        if (readTest) {
            serialCommandExecutor.executeCommand(
                    commandBuilder.setCommandType("read").build());
        }

        App.nextMarkNumber += App.numOfMarks;
        return true;
    }

    public void cancel(boolean b) {
        DWModel.cancelDWI(b);
    }
}

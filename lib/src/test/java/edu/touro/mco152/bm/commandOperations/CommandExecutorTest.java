package edu.touro.mco152.bm.commandOperations;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskWorkerConsoleModel;
import edu.touro.mco152.bm.observers.TestObserver;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for command executors. At the current time, it only tests the serial command executor with and without
 * the commandBuilder for command executors. The last line of the serialCommandExecutor test also tests the observer
 * ensuring the TestObserver was notified.
 */
class CommandExecutorTest {

    @BeforeAll
    public static void registerObservers() {
        ReceiverWriteRead.registerObserver(new TestObserver());
    }

    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     *
     * @author lcmcohen
     */
    private void setupDefaultAsPerProperties() {
        /// Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();
        System.out.println(App.getConfigString());
        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference
        App.dataDir = new File(App.locationDir.getAbsolutePath() + File.separator + App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        } else {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }

    @Test
    public void SerialCommandExecutorTest() {
        setupDefaultAsPerProperties();
        var serialCommandExecutor = new SerialCommandExecutor();
        assertTrue(serialCommandExecutor.executeCommand(new ReadCommand(
                new DiskWorkerConsoleModel(),
                25, 2, 4, DiskRun.BlockSequence.SEQUENTIAL)));
        assertTrue(serialCommandExecutor.executeCommand(new WriteCommand(
                new DiskWorkerConsoleModel(),
                25, 2, 4, DiskRun.BlockSequence.SEQUENTIAL)));

        assertTrue(TestObserver.notified);//Ensure TestObserver's update() method was called
    }

    /**
     * This test ensures the command builder functions properly by setting some parameters, running a command, and
     * comparing the parameters set in the command builder to the parameters returned by a DiskRun object. A DiskRun
     * object is obtained via the TestObserver.
     */
    @Test
    public void SerialCommandExecutorTestWithCommandBuilder() {
        setupDefaultAsPerProperties();
        var serialCommandExecutor = new SerialCommandExecutor();
        var commandBuilder = new CommandBuilder().
                setDWModel(new DiskWorkerConsoleModel()).
                setNumOfMarks(25).setNumOfBlocks(2).setBlockSizeKB(4).setBlockSeq(DiskRun.BlockSequence.SEQUENTIAL);
        DiskRun diskRun;

        //Write tests
        assertTrue(serialCommandExecutor.executeCommand(commandBuilder.setCommandType("write").build()));
        diskRun = TestObserver.getRun();
        assertEquals(25, diskRun.getNumMarks());
        assertEquals(2, diskRun.getNumBlocks());
        //Change some values, rerun the command, and compare.
        commandBuilder.setNumOfMarks(10).setNumOfBlocks(1).setBlockSizeKB(4).setBlockSeq(DiskRun.BlockSequence.SEQUENTIAL);
        assertTrue(serialCommandExecutor.executeCommand(commandBuilder.setCommandType("write").build()));
        diskRun = TestObserver.getRun(); //Obtain a new disk run with updated parameters.
        assertEquals(10, diskRun.getNumMarks());
        assertEquals(1, diskRun.getNumBlocks());

        //Read tests
        assertTrue(serialCommandExecutor.executeCommand(commandBuilder.setCommandType("read").build()));
        diskRun = TestObserver.getRun();
        assertEquals(10, diskRun.getNumMarks());
        assertEquals(1, diskRun.getNumBlocks());
        //Change some values, rerun the command, and compare.
        commandBuilder.setNumOfMarks(15).setNumOfBlocks(2).setBlockSizeKB(4).setBlockSeq(DiskRun.BlockSequence.SEQUENTIAL);
        assertTrue(serialCommandExecutor.executeCommand(commandBuilder.setCommandType("read").build()));
        diskRun = TestObserver.getRun();
        assertEquals(15, diskRun.getNumMarks());
        assertEquals(2, diskRun.getNumBlocks());
    }

}
package edu.touro.mco152.bm.commandOperations;

import edu.touro.mco152.bm.*;
import edu.touro.mco152.bm.observers.IObserver;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.App.msg;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

/**
 * This is a receiver class which encapsulates all of the logic and actions necessary to accomplish the specified
 * operations. This class currently contains only the read and write operations. Both operations share multiple
 * resources, so it seems much more efficient and organized to have both operations in one class. Additionally, the
 * class acts as the "Subject" in the observer pattern. Upon completion of a benchmark, this class notifies registered
 * observers by calling their update() methods. Observers can be registered and unregistered in this class as well.
 */
public class ReceiverWriteRead {
    static final LinkedList<IObserver> observersList = new LinkedList<>();
    DiskWorkerInterface DWModel;
    DiskRun run;
    int wUnitsComplete = 0, rUnitsComplete = 0, unitsComplete;
    int numOfMarks, numOfBlocks, blockSizeKb;
    DiskRun.BlockSequence blockSequence;
    int wUnitsTotal, rUnitsTotal, unitsTotal;
    float percentComplete;
    int blockSize;
    byte[] blockArr;
    int startFileNum = App.nextMarkNumber;

    ReceiverWriteRead(DiskWorkerInterface inputDWModel,
                      int inNumOfMarks, int inNumOfBlocks, int inBlockSizeKB, DiskRun.BlockSequence inBlockSeq) {
        DWModel = inputDWModel;
        numOfMarks = inNumOfMarks;
        numOfBlocks = inNumOfBlocks;
        blockSizeKb = inBlockSizeKB;
        blockSequence = inBlockSeq;
        wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        unitsTotal = wUnitsTotal + rUnitsTotal;
        blockSize = blockSizeKb * KILOBYTE;
        blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }
    }

    public static void registerObserver(IObserver inputIO) {
        observersList.add(inputIO);
    }

    public static void unregisterObserver(IObserver inputIO) {
        observersList.remove(inputIO);
    }

    public static void unregisterAllObservers() {
        observersList.clear();
    }

    /**
     * This method is a duplicate of the targetTxSizeKB() method in App.java. This method is necessary to ensure this
     * class's blockSizeKB, numOfBlocks, and numOfMarks are used instead of the App.java ones.
     */
    public long targetTxSizeKb() {
        return blockSizeKb * numOfBlocks * numOfMarks;
    }

    /**
     * The code in this method was originally duplicated in different methods. The similar code was extracted and placed
     * in one method for better organization and modularization.
     */
    private void configBasicInfo() {
        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize(targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));
        msg("disk info: (" + run.getDiskInfo() + ")");
        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());
    }

    public boolean writeOperation() {
        DiskMark wMark;
        run = new DiskRun(DiskRun.IOMode.WRITE, blockSequence);
        configBasicInfo();

        // Create a test data file using the default file system and config-specified location
        if (!App.multiFile) {
            testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        }

        /**
         * Begin an outer loop for specified duration (number of 'marks') of benchmark,
         * that keeps writing data (in its own loop - for specified # of blocks). Each 'Mark' is timed
         * and is reported to the GUI for display as each Mark completes.
         */
        for (int m = startFileNum; m < startFileNum + numOfMarks && !DWModel.isCancelledDWI(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            wMark = new DiskMark(WRITE);    // starting to keep track of a new bench Mark
            wMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (App.writeSyncEnable) {
                mode = "rwd";
            }

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek(rLoc * blockSize);
                        } else {
                            rAccFile.seek(b * blockSize);
                        }
                        rAccFile.write(blockArr, 0, blockSize);
                        totalBytesWrittenInMark += blockSize;
                        wUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;

                        /**
                         * Report to GUI what percentage level of Entire BM (#Marks * #Blocks) is done.
                         */
                        DWModel.setProgressDWI((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }

            /**
             * Compute duration, throughput of this Mark's step of BM
             */
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);
            msg("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s     "
                    + "(" + Util.displayString(mbWritten) + "MB written in "
                    + Util.displayString(sec) + " sec)");
            App.updateMetrics(wMark);

            /**
             * Let the GUI know the interim result described by the current Mark
             */
            DWModel.publishDWI(wMark);

            // Keep track of statistics to be displayed and persisted after all Marks are done.
            run.setRunMax(wMark.getCumMax());
            run.setRunMin(wMark.getCumMin());
            run.setRunAvg(wMark.getCumAvg());
            run.setEndTime(new Date());
        } // END outer loop for specified duration (number of 'marks') for WRITE bench mark
        notifyObservers();
        return true;
    }

    public boolean readOperation() {
        DiskMark rMark;
        run = new DiskRun(DiskRun.IOMode.READ, App.blockSequence);
        configBasicInfo();
        for (int m = startFileNum; m < startFileNum + App.numOfMarks && !DWModel.isCancelledDWI(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            rMark = new DiskMark(READ);
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, "r")) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek(rLoc * blockSize);
                        } else {
                            rAccFile.seek(b * blockSize);
                        }
                        rAccFile.readFully(blockArr, 0, blockSize);
                        totalBytesReadInMark += blockSize;
                        rUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;
                        DWModel.setProgressDWI((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            msg("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
                    + "(MBread " + mbRead + " in " + sec + " sec)");
            App.updateMetrics(rMark);
            DWModel.publishDWI(rMark);

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());
        }
        notifyObservers();
        return true;
    }

    private void notifyObservers() {
        if (!observersList.isEmpty()) {
            for (IObserver IO : observersList) {
                IO.update(run);
            }
        }
    }

}

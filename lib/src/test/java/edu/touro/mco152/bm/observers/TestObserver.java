package edu.touro.mco152.bm.observers;

import edu.touro.mco152.bm.observers.IObserver;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This class tests the observer functionality. A different method in CommandExecutorTest checks if the update() method of this class was called by asserting the notified variable is true.
 */
public class TestObserver implements IObserver {
    public static boolean notified = false;
    @Override
    public void update(DiskRun run) {
        notified = true;
    }
}

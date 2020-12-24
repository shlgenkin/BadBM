package edu.touro.mco152.bm.observers;

import edu.touro.mco152.bm.observers.IObserver;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This class is a test observer that facilitates various tests.
 * It helps with testing the observer function by changing the notified variable to true when the update method is
 * invoked. A different method in CommandExecutorTest then asserts the notified variable is true.
 * It is also used by other tests to obtain a run object.
 */
public class TestObserver implements IObserver {

    public static boolean notified = false;
    private static DiskRun run;

    public static DiskRun getRun() {
        return run;
    }

    @Override
    public void update(DiskRun run) {
        notified = true;
        TestObserver.run = run;
    }
}

package edu.touro.mco152.bm.observers;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This is an interface that stipulates all IObservers objects contain an update method that accepts a DiskRun object.
 * This class is necessary to facilitate the execution of relevant tasks and notifications upon completion of
 * benchmark.
 */
public interface IObserver {
    void update(DiskRun run);
}

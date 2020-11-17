package edu.touro.mco152.bm;

import java.util.List;

/**
 * Original implementation of DiskWorker was Swing dependent. This interface
 * was created to facilitate Swing independence for DIP compatibility.
 * The basic logic was to replace Swing-dependent methods such as publish()
 * with other ones such as publishDWI() (DWI stands for Disk Worker Interface).
 * In some cases, the interface methods act as wrappers for Swing methods and
 * function just like Swing methods would by simply passing on the argument
 * to the corresponding Swing method.
 * This does not have to be the cases for all implementations of this interface.
 */

public interface DiskWorkerInterface {

    boolean isCancelledDWI();

    void setProgressDWI(int i);

    void publishDWI(DiskMark dm);

    void startDWModel();

    void cancelDWI(boolean b);
}

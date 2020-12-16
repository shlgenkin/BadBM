package edu.touro.mco152.bm.observers;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

/**
 * This is an observer class that updates the GUI upon completion of a benchmark.
 */
public class RunPanelObserver implements IObserver {
    @Override
    public void update(DiskRun run) {
        Gui.runPanel.addRun(run);
    }
}

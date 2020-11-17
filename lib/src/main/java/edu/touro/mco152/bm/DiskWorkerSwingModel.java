package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.util.List;

import static edu.touro.mco152.bm.App.*;

public class DiskWorkerSwingModel extends SwingWorker<Boolean, DiskMark> implements DiskWorkerInterface {

    @Override
    public Boolean doInBackground() throws Exception {
        /**
         * Simply calls startDiskWorker of DiskWorker.java to manage everything.
         * If necessary, startDiskWorker will call this classes Swing replacement
         * methods such as setProgressDWI() or publishDWI() which will in turn
         * pass on the argument to the corresponding Swing method.
         */
        worker.startDiskWorker();
        return true;
    }

    @Override
    public void process(List<DiskMark> markList) {
        /**
         * We are passed a list of one or more DiskMark objects that our thread has previously
         * published to Swing. Reference Professor Cohen's video - Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4 for more details. I did but was still confused.
         */
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }

    @Override
    public void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }

    @Override
    public boolean isCancelledDWI() {
        return isCancelled();
    }

    @Override
    public void setProgressDWI(int i) {
        setProgress(i);
    }

    @Override
    public void publishDWI(DiskMark dm) {
        publish(dm);
    }

    @Override
    public void startDWModel() {
        // Set up Swing UI properly and call execute for Swing to run doInBackground.
        addPropertyChangeListener((final PropertyChangeEvent event) -> {
            switch (event.getPropertyName()) {
                case "progress":
                    int value = (Integer) event.getNewValue();
                    Gui.progressBar.setValue(value);
                    long kbProcessed = (value) * App.targetTxSizeKb() / 100;
                    Gui.progressBar.setString(kbProcessed + " / " + App.targetTxSizeKb());
                    break;
                case "state":
                    switch ((StateValue) event.getNewValue()) {
                        case STARTED:
                            Gui.progressBar.setString("0 / " + App.targetTxSizeKb());
                            break;
                        case DONE:
                            break;
                    } // end inner switch
                    break;
            }
        });
        execute();
    }

    @Override
    public void cancelDWI(boolean b) {
        cancel(b);
    }


}

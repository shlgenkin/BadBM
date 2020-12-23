package edu.touro.mco152.bm.observers;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This observer class sends a message via various messaging services (such as Slack) based on certain rules upon a
 * benchmark completion.
 */
public class RulesMessageObserver implements IObserver {
    @Override
    public void update(DiskRun run) {
        if (App.readTest && run.getRunMax() > run.getRunAvg() * 1.03) {
            SlackManager.sendMsgWithDefaultConfig();
        }
    }
}

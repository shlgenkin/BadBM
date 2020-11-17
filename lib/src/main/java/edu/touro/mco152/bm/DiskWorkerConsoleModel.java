package edu.touro.mco152.bm;

public class DiskWorkerConsoleModel implements DiskWorkerInterface {

    @Override
    public boolean isCancelledDWI() {
        return false;
    }

    @Override
    public void setProgressDWI(int i) {
        System.out.println("Progress: " + i + ".");
    }

    @Override
    public void publishDWI(DiskMark dm) {
        System.out.println(dm);
    }

    @Override
    public void startDWModel() {
    }

    @Override
    public void cancelDWI(boolean b) {

    }

}

package edu.touro.mco152.bm.persist;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class DiskRunTest {

    /**
     * These are tests of the setMin and getMin methods found in the DiskRun class.
     * This test meets the right and cross-check results of the Right-BICEP testing principles.
     * The "right" requirement is met by simply validating the basic results.
     * The "cross-check results" aspect is tested by confirming the result with the getRunMin() method.
     *
     * It also meets the conformance of the CORRECT testing principles.
     * The "conformance" detail is met by ensuring the values are formatted properly.
     */

    @Test
    void getAndSetMinTest() {
        DiskRun dR1 = new DiskRun();
        dR1.setMin(0.434);
        assertEquals(dR1.getMin(), String.valueOf(0.43));

        //Cross-check Results
        DecimalFormat DF = new DecimalFormat("###.##");
        assertEquals(DF.format(dR1.getRunMin()), String.valueOf(0.434)); //Intentional Error - expected value should be 0.43

        DiskRun dR2 = new DiskRun();
        dR2.setMin(-1);
        assertEquals(dR2.getMin(), "- -");
    }

    /**
     * The "performance characteristics" of Right-BICEP are met by ensuring the result is produced quicker than 50ms.
     *
     * The "time" requirement of CORRECT is met by ensuring the test takes less than 50ms to run.
     */
    @Test
    @Timeout(value = 50, unit = TimeUnit.MILLISECONDS)
    void getAndSetMinPerformanceTest() {
        DiskRun dR1 = new DiskRun();
        dR1.setMin(1.326);
        assertEquals(dR1.getMin(), String.valueOf(1.33));
    }
}
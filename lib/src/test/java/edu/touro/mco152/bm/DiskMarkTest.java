package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;

class DiskMarkTest {

    /**
     * This is a parameterized test of the setCumMin and getCumMin methods found in the DiskMark class. JUnit executes this test the same number of times as elements specified in @ValueSource annotation, and assigns a different value from those elements to the method argument each iteration.
     * This test meets the right, boundary conditions, error conditions, and cross-check results of the Right-BICEP testing principles.
     * The "right" requirement is met by simply validating the basic results.
     * The "boundary conditions" are tested by supplying the double min and max values as well as 0 and other numbers.
     * The "error conditions" requirement is met by attempting all cases, even not setting the setCumMin and ensuring that no errors are thrown.
     * The "cross-check results" aspect is tested by confirming the method with the getMinAsString() method.
     *
     * It also meets the existence requirement of the CORRECT testing principles.
     * The "existence" detail is met by ensuring the values are present and not null even when the setCumMin is not invoked.
     * @param dblNum is a double that is set by JUnit and expected result of getCumMin .
     */
    @ParameterizedTest
    @ValueSource(doubles = {Double.MIN_VALUE, -3, -1, 0, 1, 2, 3, Double.MAX_VALUE, })
    void setAndGetCumMinTest(double dblNum) {
        DiskMark dM1 = new DiskMark(DiskMark.MarkType.READ);
        dM1.setCumMin(dblNum);
        assertEquals(dM1.getCumMin(), dblNum);

        //Cross-check getCumMin method by using getMinAsString()
        DecimalFormat df = new DecimalFormat("###.###");
        assertEquals(dM1.getMinAsString(), df.format(dblNum));

        DiskMark dM2 = new DiskMark(DiskMark.MarkType.READ);
        assertEquals(dM2.getCumMin(), 0.0);
        dM2.setCumMin(Double.MIN_VALUE);
    }
}
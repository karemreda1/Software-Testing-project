package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Data-flow target (function 1): Validation.checkMovieId
 * Variable of interest: digits (def/use across uniqueDigits set).
 *
 * Criteria:
 * - All-defs
 * - All-uses
 * - All DU-paths
 */
public class ValidationCheckMovieIdDataFlowTest {

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
    }

    @Test
    public void allDefs_allUses_allDUPaths_forDigits() {
        // def(digits) -> contains(false) -> add(digits)
        assertEquals(Validation.MOVIE_ID_OK, Validation.checkMovieId("The Dark Knight", "TDK123"));

        // def(digits) -> contains(true) (duplicate)
        assertEquals(Validation.MOVIE_ID_NUMBERS_NOT_UNIQUE, Validation.checkMovieId("Mad Max", "MM123"));
    }
}

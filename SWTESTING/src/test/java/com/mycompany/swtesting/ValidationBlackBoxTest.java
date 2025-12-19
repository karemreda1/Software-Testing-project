package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Black-box tests for module: Validation.java
 * Partitions + boundaries for public validators.
 */
public class ValidationBlackBoxTest {

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
    }

    @Test
    public void isValidMovieTitle_partitions() {
        assertTrue(Validation.isValidMovieTitle("The Dark Knight"));
        assertFalse(Validation.isValidMovieTitle(""));           // empty
        assertFalse(Validation.isValidMovieTitle("The 2"));      // digit
        assertFalse(Validation.isValidMovieTitle(null));         // null
    }

    @Test
    public void checkMovieId_boundaryAndPartition() {
        // title capitals: TDK -> expect TDK + 3 digits
        assertEquals(Validation.MOVIE_ID_OK, Validation.checkMovieId("The Dark Knight", "TDK123"));
        assertEquals(Validation.MOVIE_ID_LETTERS_WRONG, Validation.checkMovieId("The Dark Knight", "TDK12"));   // short
        assertEquals(Validation.MOVIE_ID_LETTERS_WRONG, Validation.checkMovieId("The Dark Knight", "TDK1234")); // long
        assertEquals(Validation.MOVIE_ID_LETTERS_WRONG, Validation.checkMovieId("The Dark Knight", "XXX123"));  // wrong letters
    }

    @Test
    public void isValidMovieGenres_partitions() {
        assertTrue(Validation.isValidMovieGenres(Arrays.asList("action", "drama")));
        assertFalse(Validation.isValidMovieGenres(Collections.emptyList()));           // empty
        assertFalse(Validation.isValidMovieGenres(Arrays.asList("action", "")));       // empty element
        assertFalse(Validation.isValidMovieGenres(Arrays.asList("act!on")));           // invalid chars
        assertFalse(Validation.isValidMovieGenres(null));                               // null
    }

    @Test
    public void isValidUserName_andUserId_partitions() {
        assertTrue(Validation.isValidUserName("Alice"));
        assertFalse(Validation.isValidUserName("Alice1"));
        assertFalse(Validation.isValidUserName(""));

        // User ID partitions: 9 digits OR 8 digits + 1 letter
        assertTrue(Validation.isValidUserId("123456789"));
        assertTrue(Validation.isValidUserId("12345678A"));
        assertFalse(Validation.isValidUserId("12345678"));   // too short
        assertFalse(Validation.isValidUserId("1234567890")); // too long
    }
}

package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationTest {

    @Before
    public void setUp() {
        // Clear static sets (uniqueDigits, userIds, movieIds if you have it)
        Validation.resetState();
    }

    // ===== Movie title tests =====

    @Test
    public void testValidMovieTitle_AllWordsCapitalized() {
        assertTrue(Validation.isValidMovieTitle("The Dark Knight"));
    }

    @Test
    public void testInvalidMovieTitle_WordNotCapitalized() {
        assertFalse(Validation.isValidMovieTitle("The dark Knight"));
    }

    // ===== Movie ID tests =====

    @Test
    public void testValidMovieId_CorrectPrefixAndUniqueDigits() {
        String title = "The Dark Knight";   // capitals: TDK
        String movieId = "TDK123";          // TDK + 3 digits
        int status = Validation.checkMovieId(title, movieId);
        assertEquals(Validation.MOVIE_ID_OK, status);
    }

    @Test
    public void testMovieIdLettersWrong_WrongPrefix() {
        String title = "The Dark Knight";   // capitals: TDK
        String movieId = "TDN123";          // prefix mismatch
        int status = Validation.checkMovieId(title, movieId);
        assertEquals(Validation.MOVIE_ID_LETTERS_WRONG, status);
    }

    @Test
    public void testMovieIdNumbersNotUnique_DigitsUsedBefore() {
        String title1 = "The Dark Knight";      // capitals TDK
        String title2 = "Harry Potter";         // capitals HP

        // First use of 123 → OK
        assertEquals(Validation.MOVIE_ID_OK,
                     Validation.checkMovieId(title1, "TDK123"));

        // Second time digits 123 → should be NOT_UNIQUE
        assertEquals(Validation.MOVIE_ID_NUMBERS_NOT_UNIQUE,
                     Validation.checkMovieId(title2, "HP123"));
    }

    // ===== Genre tests =====

    @Test
    public void testValidMovieGenres_SingleGenre() {
        java.util.List<String> genres =
                java.util.Arrays.asList("horror");
        assertTrue(Validation.isValidMovieGenres(genres));
    }

    @Test
    public void testInvalidMovieGenres_ContainsEmpty() {
        java.util.List<String> genres =
                java.util.Arrays.asList("action", "   ");
        assertFalse(Validation.isValidMovieGenres(genres));
    }

    // ===== User name tests =====

    @Test
    public void testValidUserName_NormalName() {
        assertTrue(Validation.isValidUserName("John Doe"));
    }

    @Test
    public void testInvalidUserName_StartsWithSpace() {
        assertFalse(Validation.isValidUserName(" John"));
    }

    @Test
    public void testInvalidUserName_ContainsDigits() {
        assertFalse(Validation.isValidUserName("John1 Doe"));
    }

    // ===== User ID tests =====

    @Test
    public void testValidUserId_8DigitsAndLetter() {
        // length = 9, starts with digit, last char letter
        assertTrue(Validation.isValidUserId("12345678A"));
    }

    @Test
    public void testValidUserId_9DigitsNoLetter() {
        assertTrue(Validation.isValidUserId("123456789"));
    }

    @Test
    public void testInvalidUserId_WrongLength() {
        assertFalse(Validation.isValidUserId("1234567"));   // too short
    }

    @Test
    public void testInvalidUserId_StartsWithLetter() {
        assertFalse(Validation.isValidUserId("A23456789"));
    }

    @Test
    public void testInvalidUserId_NotUnique() {
        assertTrue(Validation.isValidUserId("12345678A"));
        assertFalse(Validation.isValidUserId("12345678A")); // second time = not unique
    }
}

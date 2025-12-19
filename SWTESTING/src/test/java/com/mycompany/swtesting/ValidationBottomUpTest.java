package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class ValidationBottomUpTest {

    @Before
    public void setUp() {
        Validation.resetState();
    }

    @Test
    public void testValidMovieTitle() {
        assertTrue(Validation.isValidMovieTitle("Bullet Train"));
    }

    @Test
    public void testInvalidMovieTitle() {
        assertFalse(Validation.isValidMovieTitle("Bullet train"));
    }
        
    @Test
    public void testInvalidMovieTitle2() {
        assertFalse(Validation.isValidMovieTitle("bullet train"));
    }
    
    @Test
    public void testValidMovieId() {
        assertEquals(
            Validation.MOVIE_ID_OK,
            Validation.checkMovieId("The Matrix", "TM123")
        );
    }

    @Test
    public void testDuplicateMovieIdNumbers() {
        Validation.checkMovieId("The Matrix", "TM123");
        assertEquals(
            Validation.MOVIE_ID_NUMBERS_NOT_UNIQUE,
            Validation.checkMovieId("Toy Story", "TS123")
        );
    }

    @Test
    public void testInvalidUserName() {
        assertFalse(Validation.isValidUserName(" Mohamed"));
    }
    
    @Test
    public void testMovieIdWithoutLetters() {
        assertEquals(
        Validation.MOVIE_ID_LETTERS_WRONG,
        Validation.checkMovieId("The Matrix", "123")
    );
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class UtilReadMoviesBottomUpTest {

    private List<Movie> movies;

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
        movies = new ArrayList<>();
    }

    @Test
    public void testValidSingleMovie() throws Exception {
        File f = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("The Matrix,TM123");
            pw.println("Action,Sci Fi");
        }

        assertTrue(Util.readMovies(f.getAbsolutePath(), movies));
        assertEquals(1, movies.size());
        assertEquals("The Matrix", movies.get(0).getMovieName());
    }

    @Test
    public void testValidMultipleMovies() throws Exception {
        File f = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("The Matrix,TM123");
            pw.println("Action");
            pw.println("John Wick,JW456");
            pw.println("Action,Drama");
        }

        assertTrue(Util.readMovies(f.getAbsolutePath(), movies));
        assertEquals(2, movies.size());
    }


    @Test
    public void testInvalidMovieTitle() throws Exception {
        File f = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("the matrix,TM123");
            pw.println("Action");
        }

        assertFalse(Util.readMovies(f.getAbsolutePath(), movies));
        assertEquals(
            "ERROR: Movie Title the matrix is wrong",
            Util.firstError
        );
    }

    @Test
    public void testMovieIdWithoutLetters() throws Exception {
        File f = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("The Matrix,123");
            pw.println("Action");
        }

        assertFalse(Util.readMovies(f.getAbsolutePath(), movies));
        assertTrue(Util.firstError.contains("Movie Id letters"));
    }

    @Test
    public void testMovieIdLettersMismatch() throws Exception {
        File f = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("The Matrix,AB123");
            pw.println("Action");
        }

        assertFalse(Util.readMovies(f.getAbsolutePath(), movies));
        assertTrue(Util.firstError.contains("Movie Id letters"));
    }

    @Test
    public void testDuplicateMovieIdNumbers() throws Exception {
        File f = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("The Matrix,TM123");
            pw.println("Action");
            pw.println("Toy Story,TS123");
            pw.println("Drama");
        }

        assertFalse(Util.readMovies(f.getAbsolutePath(), movies));
        assertTrue(Util.firstError.contains("aren’t unique"));
    }


    @Test
    public void testFirstErrorOnlyReturned() throws Exception {
        File f = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("the matrix,123"); // title + id error
            pw.println("Action");
        }

        assertFalse(Util.readMovies(f.getAbsolutePath(), movies));
        assertEquals(
            "ERROR: Movie Title the matrix is wrong",
            Util.firstError
        );
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.swtesting;

/**
 *
 * @author Ahmad Aledlbi
 */
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UtilReadUsersBottomUpTest {

    private List<Movie> movies;
    private List<User> users;

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();

        movies = new ArrayList<>();
        users = new ArrayList<>();

        Movie m1 = new Movie();
        m1.setMovieName("Inception");
        m1.setMovieId("I123");
        m1.addGenres("Action");

        Movie m2 = new Movie();
        m2.setMovieName("Titanic");
        m2.setMovieId("T456");
        m2.addGenres("Drama");

        movies.add(m1);
        movies.add(m2);
    }



    @Test
    public void testValidUserFile() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Alice,12345678A");
            pw.println("I123");
        }

        assertTrue(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getUserName());
    }

    @Test
    public void testMultipleValidUsers() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Alice,12345678A");
            pw.println("I123");
            pw.println("Bob,12345678B");
            pw.println("T456");
        }

        assertTrue(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertEquals(2, users.size());
    }



    @Test
    public void testUserNameStartsWithSpace() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println(" Alice,12345678A");
            pw.println("I123");
        }

        assertFalse(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertEquals(
                "ERROR: User Name  Alice is wrong",
                Util.firstError
        );
    }


    @Test
    public void testUserNameWithNumbers() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Ali3e,12345678A");
            pw.println("I123");
        }

        assertFalse(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertTrue(Util.firstError.contains("User Name"));
    }


    @Test
    public void testUserIdWrongLength() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Alice,123");
            pw.println("I123");
        }

        assertFalse(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertEquals(
                "ERROR: User Id 123 is wrong",
                Util.firstError
        );
    }

    @Test
    public void testUserIdStartsWithLetters() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Alice,ABC123456");
            pw.println("I123");
        }

        assertFalse(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertTrue(Util.firstError.contains("User Id"));
    }

    @Test
    public void testDuplicateUserIds() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Alice,12345678A");
            pw.println("I123");
            pw.println("Bob,12345678A");
            pw.println("T456");
        }

        assertFalse(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertTrue(Util.firstError.contains("User Id"));
    }

    @Test
    public void testLikedMovieDoesNotExist() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Alice,12345678A");
            pw.println("X999");
        }

        assertFalse(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertTrue(Util.firstError.contains("Liked movie ID"));
    }

    @Test
    public void testMultipleLikedMovies() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Alice,12345678A");
            pw.println("I123,T456");
        }

        assertTrue(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertEquals(2, users.get(0).getLikedMovies().size());
    }


    @Test
    public void testMissingLikedMoviesLine() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Alice,12345678A");
        }

        assertFalse(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertTrue(Util.firstError.contains("Missing liked movies"));
    }

    @Test
    public void testFirstErrorOnlyReturned() throws Exception {
        File f = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println(" Alice,ABC");
            pw.println("X999");
        }

        assertFalse(Util.readUsers(f.getAbsolutePath(), users, movies));
        assertEquals(
                "ERROR: User Name  Alice is wrong",
                Util.firstError
        );
    }
}
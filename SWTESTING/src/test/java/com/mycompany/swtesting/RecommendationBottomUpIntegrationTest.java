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
import java.util.*;

import static org.junit.Assert.*;

public class RecommendationBottomUpIntegrationTest {

    private List<Movie> movies;
    private List<User> users;

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
        movies = new ArrayList<>();
        users = new ArrayList<>();
    }

    @Test
    public void testActionRecommendation() throws Exception {
        File moviesFile = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(moviesFile)) {
            pw.println("The Matrix,TM123");
            pw.println("Action");
            pw.println("Mad Max,MM456");
            pw.println("Action");
            pw.println("Titanic,T789");
            pw.println("Drama");
        }
        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));

        File usersFile = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(usersFile)) {
            pw.println("Mohamed,12345678M");
            pw.println("TM123");
        }
        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, movies));

        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);
        List<String> mohamedRecs = recs.get(users.get(0));

        assertNotNull(mohamedRecs);
        assertTrue(mohamedRecs.contains("Mad Max"));
        assertFalse(mohamedRecs.contains("The Matrix"));
        assertFalse(mohamedRecs.contains("Titanic"));
    }

    @Test
    public void testHorrorRecommendation() throws Exception {
        File moviesFile = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(moviesFile)) {
            pw.println("Conjuring,C123");
            pw.println("Horror");
            pw.println("Insidious,I456");
            pw.println("Horror");
            pw.println("Frozen,F789");
            pw.println("Animation");
        }
        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));

        File usersFile = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(usersFile)) {
            pw.println("Mostafa,12345678O");
            pw.println("C123");
        }
        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, movies));

        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);
        List<String> mostafaRecs = recs.get(users.get(0));

        assertNotNull(mostafaRecs);
        assertTrue(mostafaRecs.contains("Insidious"));
        assertFalse(mostafaRecs.contains("Conjuring"));
        assertFalse(mostafaRecs.contains("Frozen"));
    }

    @Test
    public void testMultipleGenresRecommendation() throws Exception {
        File moviesFile = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(moviesFile)) {
            pw.println("Matrix,M123");
            pw.println("Action,SciFi");
            pw.println("Titanic,T456");
            pw.println("Drama,Romance");
            pw.println("Mad Max,MM789");
            pw.println("Action");
        }
        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));

        File usersFile = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(usersFile)) {
            pw.println("Ahmad,12345678A");
            pw.println("M123,T456");
        }
        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, movies));

        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);
        List<String> ahmadRecs = recs.get(users.get(0));

        assertNotNull(ahmadRecs);
        assertTrue(ahmadRecs.contains("Mad Max"));
        assertFalse(ahmadRecs.contains("Matrix"));
        assertFalse(ahmadRecs.contains("Titanic"));
    }

    @Test
    public void testNoRecommendationIfAllLiked() throws Exception {
        File moviesFile = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(moviesFile)) {
            pw.println("Matrix,M123");
            pw.println("Action");
            pw.println("Titanic,T456");
            pw.println("Drama");
        }
        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));

        File usersFile = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(usersFile)) {
            pw.println("Ehab,12345678E");
            pw.println("M123,T456");
        }
        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, movies));

        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);
        List<String> ehabRecs = recs.get(users.get(0));

        assertNotNull(ehabRecs);
        assertEquals(0, ehabRecs.size()); // All liked, no recommendations
    }

    @Test
    public void testMultipleUsersRecommendations() throws Exception {
        File moviesFile = File.createTempFile("movies", ".txt");
        try (PrintWriter pw = new PrintWriter(moviesFile)) {
            pw.println("Matrix,M123");
            pw.println("Action");
            pw.println("Titanic,T456");
            pw.println("Drama");
            pw.println("Frozen,F789");
            pw.println("Animation");
        }
        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));

        File usersFile = File.createTempFile("users", ".txt");
        try (PrintWriter pw = new PrintWriter(usersFile)) {
            pw.println("Mohamed,12345678M");
            pw.println("M123");
            pw.println("Mostafa,12345678O");
            pw.println("T456");
        }
        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, movies));

        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);

        List<String> mohamedRecs = recs.get(users.get(0));
        assertNotNull(mohamedRecs);
        assertFalse(mohamedRecs.contains("Matrix"));
        assertTrue(mohamedRecs.contains("Frozen") == false);
        assertEquals(0, mohamedRecs.size());

        List<String> mostafaRecs = recs.get(users.get(1));
        assertNotNull(mostafaRecs);
        assertFalse(mostafaRecs.contains("Titanic"));
        assertEquals(0, mostafaRecs.size());
    }
}
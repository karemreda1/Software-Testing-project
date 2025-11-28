package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class UtilRecommendationTest {

    private List<Movie> movies;
    private List<User> users;

    @Before
    public void setUp() {
        movies = new ArrayList<>();
        users = new ArrayList<>();

        // Movie 1: action, drama
        Movie m1 = new Movie();
        m1.setMovieName("The Dark Knight");
        m1.setMovieId("TDK123");
        m1.addGenres("action");
        m1.addGenres("drama");

        // Movie 2: action only
        Movie m2 = new Movie();
        m2.setMovieName("Mad Max");
        m2.setMovieId("MM456");
        m2.addGenres("action");

        // Movie 3: horror only
        Movie m3 = new Movie();
        m3.setMovieName("The Conjuring");
        m3.setMovieId("TC789");
        m3.addGenres("horror");

        movies.add(m1);
        movies.add(m2);
        movies.add(m3);

        // User 1 likes m1 (action/drama)
        User u1 = new User();
        u1.setUserName("Alice");
        u1.setUserId("12345678A");
        u1.addLikedMovie("TDK123");

        // User 2 likes m3 (horror)
        User u2 = new User();
        u2.setUserName("Bob");
        u2.setUserId("12345678B");
        u2.addLikedMovie("TC789");

        users.add(u1);
        users.add(u2);
    }

    @Test
    public void testRecommendations_UserLikesAction() {
        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);

        User alice = users.get(0);
        List<String> aliceRecs = recs.get(alice);

        assertNotNull(aliceRecs);
        assertTrue(aliceRecs.contains("Mad Max"));
        assertFalse(aliceRecs.contains("The Dark Knight")); // don't recommend liked
    }

    @Test
    public void testRecommendations_UserLikesUniqueGenre_NoOtherMovies() {
        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);

        User bob = users.get(1);
        List<String> bobRecs = recs.get(bob);

        assertNotNull(bobRecs);
        assertEquals(0, bobRecs.size()); // only horror movie is the one he already likes
    }
}

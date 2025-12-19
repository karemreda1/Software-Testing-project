package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Black-box tests for module: Util.java
 * Focus: file formats, partitions, and boundaries (no internal-structure assertions).
 */
public class UtilBlackBoxTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
    }

    private File writeTempFile(String name, String content) throws Exception {
        File f = temp.newFile(name);
        Files.write(f.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return f;
    }

    private List<Movie> buildMoviesForUsers() {
        List<Movie> allMovies = new ArrayList<>();

        Movie m1 = new Movie();
        m1.setMovieName("The Dark Knight");
        m1.setMovieId("TDK123");
        m1.setGenre(Arrays.asList("action", "drama"));
        allMovies.add(m1);

        Movie m2 = new Movie();
        m2.setMovieName("Mad Max");
        m2.setMovieId("MM456");
        m2.setGenre(Collections.singletonList("action"));
        allMovies.add(m2);

        return allMovies;
    }

    @Test
    public void readMovies_validFile_parsesMovies() throws Exception {
        String content =
                "The Dark Knight,TDK123\n" +
                "action, drama\n" +
                "Mad Max,MM456\n" +
                "action\n";

        File moviesFile = writeTempFile("movies_ok.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertNull(Util.firstError);
        assertEquals(2, movies.size());
    }

    @Test
    public void readMovies_invalidFormat_missingComma_fails() throws Exception {
        String content =
                "The Dark Knight TDK123\n" +   // missing comma
                "action\n";

        File moviesFile = writeTempFile("movies_bad_format.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertFalse(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertNotNull(Util.firstError);
    }

    @Test
    public void readUsers_validFile_withKnownMovies_parsesUsers() throws Exception {
        String content =
                "Alice,12345678A\n" +
                "TDK123,MM456\n" +
                "Bob,123456789\n" +
                "MM456\n";

        File usersFile = writeTempFile("users_ok.txt", content);
        List<User> users = new ArrayList<>();

        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, buildMoviesForUsers()));
        assertNull(Util.firstError);
        assertEquals(2, users.size());
    }

    @Test
    public void readUsers_invalidUserId_boundary_fails() throws Exception {
        String content =
                "Alice,1234567A\n" +  // boundary violation
                "TDK123\n";

        File usersFile = writeTempFile("users_bad_id.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, buildMoviesForUsers()));
        assertNotNull(Util.firstError);
    }

    @Test
    public void recommendations_blackBox_basicOverlap_recommendsUnseen() {
        List<Movie> movies = new ArrayList<>();

        Movie a = new Movie();
        a.setMovieName("The Dark Knight");
        a.setMovieId("TDK123");
        a.setGenre(Arrays.asList("action", "drama"));
        movies.add(a);

        Movie b = new Movie();
        b.setMovieName("Mad Max");
        b.setMovieId("MM456");
        b.setGenre(Collections.singletonList("action"));
        movies.add(b);

        Movie c = new Movie();
        c.setMovieName("Toy Story");
        c.setMovieId("TS789");
        c.setGenre(Collections.singletonList("family"));
        movies.add(c);

        List<User> users = new ArrayList<>();

        User u1 = new User();
        u1.setUserName("Alice");
        u1.setUserId("12345678A");
        u1.setLikedMovies(Collections.singletonList("TDK123"));
        users.add(u1);

        User u2 = new User();
        u2.setUserName("Bob");
        u2.setUserId("123456789");
        u2.setLikedMovies(Collections.singletonList("TS789"));
        users.add(u2);

        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);

        List<String> aliceRecs = recs.get(u1);
        assertNotNull(aliceRecs);
        assertTrue(aliceRecs.contains("Mad Max"));
        assertFalse(aliceRecs.contains("The Dark Knight"));
    }
}

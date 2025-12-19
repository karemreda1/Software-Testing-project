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
 * White-box tests for module: Util.java (large target: readUsers).
 *
 * Coverage goals:
 * - Statement coverage
 * - Branch coverage
 * - Condition coverage
 * - Path (basis) coverage
 */
public class UtilWhiteBoxTest {

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

    private List<Movie> buildMovies() {
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

    // -------------------- Statement + Branch coverage --------------------

    @Test
    public void readUsers_success_pathStatementsCovered() throws Exception {
        String content =
                "Alice,12345678A\n" +
                "TDK123,MM456\n";

        File usersFile = writeTempFile("users_ok.txt", content);
        List<User> users = new ArrayList<>();

        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, buildMovies()));
        assertNull(Util.firstError);
        assertEquals(1, users.size());
    }

    @Test
    public void readUsers_missingSecondLine_branchFailsEarly() throws Exception {
        String content =
                "Alice,12345678A\n"; // missing liked line

        File usersFile = writeTempFile("users_missing_likes.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, buildMovies()));
        assertNotNull(Util.firstError);
        assertTrue(Util.firstError.startsWith("ERROR: Missing liked movies line"));
    }

    @Test
    public void readUsers_invalidLikedMovieId_triggersExistsFalseBranch() throws Exception {
        String content =
                "Alice,12345678A\n" +
                "NOPE999\n";

        File usersFile = writeTempFile("users_bad_like.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, buildMovies()));
        assertNotNull(Util.firstError);
        assertTrue(Util.firstError.startsWith("ERROR: Liked movie ID"));
    }

    // -------------------- Condition coverage --------------------
    // Target condition:
    // if (m != null && m.getMovieId() != null && m.getMovieId().equals(movieId)) { ... }

    @Test
    public void readUsers_conditionCoverage_mIsNull_allowsConditionFalse() throws Exception {
        List<Movie> allMovies = new ArrayList<>();
        allMovies.add(null); // m != null == false

        String content =
                "Alice,12345678A\n" +
                "TDK123\n";

        File usersFile = writeTempFile("users_cond_m_null.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, allMovies));
        assertNotNull(Util.firstError);
    }

    @Test
    public void readUsers_conditionCoverage_movieIdNull_allowsSecondConjunctFalse() throws Exception {
        List<Movie> allMovies = new ArrayList<>();
        Movie m = new Movie();
        m.setMovieName("X");
        m.setMovieId(null); // m.getMovieId() != null == false
        m.setGenre(Collections.singletonList("action"));
        allMovies.add(m);

        String content =
                "Alice,12345678A\n" +
                "TDK123\n";

        File usersFile = writeTempFile("users_cond_id_null.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, allMovies));
        assertNotNull(Util.firstError);
    }

    @Test
    public void readUsers_conditionCoverage_equalsTrue_allConjunctsTrue() throws Exception {
        String content =
                "Alice,12345678A\n" +
                "TDK123\n";

        File usersFile = writeTempFile("users_cond_true.txt", content);
        List<User> users = new ArrayList<>();

        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, buildMovies()));
        assertNull(Util.firstError);
        assertEquals(1, users.size());
    }

    // -------------------- Path coverage (basis path) --------------------

    @Test
    public void readUsers_path_allMoviesNull_causesInvalidLikedMovieError() throws Exception {
        String content =
                "Alice,12345678A\n" +
                "TDK123\n";

        File usersFile = writeTempFile("users_allMovies_null.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, null));
        assertNotNull(Util.firstError);
        assertTrue(Util.firstError.startsWith("ERROR: Liked movie ID "));
        assertEquals(0, users.size());
    }

}

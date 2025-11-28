package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UtilReadUsersTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
    }

    private File writeTempFile(String filename, String content) throws Exception {
        File f = temp.newFile(filename);
        Files.write(f.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return f;
    }

    @Test
    public void readUsers_success_parsesUsersAndLikedMovies() throws Exception {
        String content =
                "Alice,12345678A\n" +
                "TDK123, MM456\n" +
                "Bob,123456789\n" +
                "TC789\n";

        File usersFile = writeTempFile("users.txt", content);
        List<User> users = new ArrayList<>();

        List<Movie> allMovies = new ArrayList<>();
        Movie a = new Movie(); a.setMovieId("TDK123"); allMovies.add(a);
        Movie b = new Movie(); b.setMovieId("MM456");  allMovies.add(b);
        Movie c = new Movie(); c.setMovieId("TC789");  allMovies.add(c);




        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, allMovies));
        assertNull(Util.firstError);
        assertEquals(2, users.size());

        User u1 = users.get(0);
        assertEquals("Alice", u1.getUserName());
        assertEquals("12345678A", u1.getUserId());
        assertEquals(2, u1.getLikedMovies().size());
        assertEquals("TDK123", u1.getLikedMovies().get(0));
        assertEquals("MM456", u1.getLikedMovies().get(1));

        User u2 = users.get(1);
        assertEquals("Bob", u2.getUserName());
        assertEquals("123456789", u2.getUserId());
        assertEquals(1, u2.getLikedMovies().size());
        assertEquals("TC789", u2.getLikedMovies().get(0));
    }

    @Test
    public void readUsers_invalidUserName_setsFirstErrorAndFails() throws Exception {
        String content =
                "John1,12345678A\n" +  // name contains digit
                "TDK123\n";

        File usersFile = writeTempFile("users_invalid_name.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, new ArrayList<Movie>()));
        assertNotNull(Util.firstError);
        assertEquals("ERROR: User Name John1 is wrong", Util.firstError);
        assertEquals(0, users.size());
    }

    @Test
    public void readUsers_invalidUserId_setsFirstErrorAndFails() throws Exception {
        String content =
                "Alice,123\n" + // wrong length
                "TDK123\n";

        File usersFile = writeTempFile("users_invalid_id.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, new ArrayList<Movie>()));
        assertEquals("ERROR: User Id 123 is wrong", Util.firstError);
    }

    @Test
    public void readUsers_missingCommaInHeader_setsGenericError() throws Exception {
        String content =
                "Alice 12345678A\n" + // missing comma
                "TDK123\n";

        File usersFile = writeTempFile("users_missing_comma.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, new ArrayList<Movie>()));
        assertEquals("Error", Util.firstError);
    }

    @Test
    public void readUsers_whitespaceOnlyLine_shouldBeIgnored_andStillParses() throws Exception {
        // Expected behavior: whitespace-only lines should be ignored (robust parsing).
        String content =
                "   \n" +
                "Alice,12345678A\n" +
                "TDK123\n";

        File usersFile = writeTempFile("users_whitespace_line.txt", content);
        List<User> users = new ArrayList<>();
        List<Movie> allMovies = new ArrayList<>();
        Movie a = new Movie(); a.setMovieId("TDK123"); allMovies.add(a);
        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, allMovies));

        assertNull(Util.firstError);
        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getUserName());
        assertEquals("12345678A", users.get(0).getUserId());
        assertEquals(1, users.get(0).getLikedMovies().size());
        assertEquals("TDK123", users.get(0).getLikedMovies().get(0));
    }
@Test
    public void readUsers_duplicateUserId_failsOnSecondUser_keepsFirstUser() throws Exception {
        String content =
                "Alice,12345678A\n" +
                "TDK123\n" +
                "Bob,12345678A\n" + // duplicate
                "MM456\n";

        File usersFile = writeTempFile("users_duplicate_id.txt", content);
        List<User> users = new ArrayList<>();
        List<Movie> allMovies = new ArrayList<>();
        Movie a = new Movie(); a.setMovieId("TDK123"); allMovies.add(a);
        Movie b = new Movie(); b.setMovieId("MM456");  allMovies.add(b);
       assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, allMovies));
        assertNotNull(Util.firstError);
        assertEquals("ERROR: User Id 12345678A is wrong", Util.firstError);
        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getUserName());
    }

    
    @Test
    public void readUsers_invalidLikedMovieId_shouldFail() throws Exception {
        String content =
                "Alice,12345678A\n" +
                "NOT_A_REAL_ID\n";

        File usersFile = writeTempFile("users_invalid_liked_id.txt", content);
        List<User> users = new ArrayList<>();

        List<Movie> movies = new ArrayList<>();
        Movie m = new Movie();
        m.setMovieName("Mad Max");
        m.setMovieId("MM456");
        m.addGenres("action");
        movies.add(m);

        // Expected behavior (if liked movies were validated): should return false.
        // Current behavior: returns true and accepts invalid liked movie IDs → defect.
        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, movies));
        assertNotNull("Expected firstError to be set for invalid liked movie id", Util.firstError);
    }


    @Test
    public void readUsers_fileEndsAfterHeader_shouldFail() throws Exception {
        // Input format requires a liked-movies line after each user header.
        // Expected behavior: return false and set an error when the file ends early.
        String content =
                "Alice,12345678A\n";

        File usersFile = writeTempFile("users_header_only.txt", content);
        List<User> users = new ArrayList<>();

        assertFalse(Util.readUsers(usersFile.getAbsolutePath(), users, new ArrayList<Movie>()));
        assertNotNull("Expected firstError to be set when liked-movies line is missing", Util.firstError);
    }

}

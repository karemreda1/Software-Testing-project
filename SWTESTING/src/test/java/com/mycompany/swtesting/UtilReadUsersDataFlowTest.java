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
 * Data-flow target (function 3): Util.readUsers
 * Variable of interest: exists (def/use around liked-movie existence check).
 */
public class UtilReadUsersDataFlowTest {

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

    @Test
    public void allDefs_allUses_allDUPaths_forExistsFlag() throws Exception {
        List<Movie> allMovies = new ArrayList<>();
        Movie m = new Movie();
        m.setMovieName("The Dark Knight");
        m.setMovieId("TDK123");
        m.setGenre(Collections.singletonList("action"));
        allMovies.add(m);

        // Case A: def(exists=false) -> set true -> use(!exists)=false (success)
        File okUsers = writeTempFile("ok_users.txt",
                "Alice,12345678A\n" +
                "TDK123\n");

        List<User> usersOk = new ArrayList<>();
        assertTrue(Util.readUsers(okUsers.getAbsolutePath(), usersOk, allMovies));
        assertNull(Util.firstError);

        Util.clearFirstError();
        Validation.resetState();

        // Case B: def(exists=false) -> never set true -> use(!exists)=true (fail)
        File badUsers = writeTempFile("bad_users.txt",
                "Bob,123456789\n" +
                "NOPE999\n");

        List<User> usersBad = new ArrayList<>();
        assertFalse(Util.readUsers(badUsers.getAbsolutePath(), usersBad, allMovies));
        assertNotNull(Util.firstError);
        assertTrue(Util.firstError.startsWith("ERROR: Liked movie ID"));
    }
}

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
import java.util.Map;

import static org.junit.Assert.*;

public class IntegrationEndToEndTest {

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
    public void endToEnd_validInputs_producesExpectedRecommendationsFile() throws Exception {
        // Arrange: valid movies + users files
        String moviesContent =
                "The Dark Knight,TDK123\n" +
                "action, drama\n" +
                "Mad Max,MM456\n" +
                "action\n" +
                "The Conjuring,TC789\n" +
                "horror\n";

        String usersContent =
                "Alice,12345678A\n" +
                "TDK123\n" +
                "Bob,12345678B\n" +
                "TC789\n";

        File moviesFile = writeTempFile("movies.txt", moviesContent);
        File usersFile = writeTempFile("users.txt", usersContent);
        File outFile = temp.newFile("recommendations.txt");

        List<Movie> movies = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Act: run the same flow as SWTESTING.main()
        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertTrue(Util.readUsers(usersFile.getAbsolutePath(), users, movies));

        Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);

        StringBuilder data = new StringBuilder();
        for (User user : users) {
            data.append(user.getUserName()).append(",").append(user.getUserId()).append("\n");
            data.append(String.join(", ", recs.get(user))).append("\n");
        }

        Writefile.writeToFile(outFile.getAbsolutePath(), data.toString());

        // Assert: exact output format (2 lines per user)
        String expected =
                "Alice,12345678A\n" +
                "Mad Max\n" +
                "Bob,12345678B\n" +
                "\n";

        String actual = new String(Files.readAllBytes(outFile.toPath()), StandardCharsets.UTF_8);
        assertEquals(expected, actual);
    }

    @Test
    public void endToEnd_invalidMoviesFile_writesFirstError_andStops() throws Exception {
        // Arrange: invalid movie title (word not capitalized) should fail readMovies
        String moviesContent =
                "The dark Knight,TDK123\n" +
                "action\n";

        File moviesFile = writeTempFile("movies_bad.txt", moviesContent);
        File outFile = temp.newFile("recommendations.txt");

        List<Movie> movies = new ArrayList<>();

        // Act: simulate the SWTESTING.main() failure behavior (write firstError then return)
        if (!Util.readMovies(moviesFile.getAbsolutePath(), movies)) {
            Writefile.writeToFile(outFile.getAbsolutePath(), Util.firstError);
        }

        // Assert
        String actual = new String(Files.readAllBytes(outFile.toPath()), StandardCharsets.UTF_8);
        assertEquals("ERROR: Movie Title The dark Knight is wrong", actual);
    }
}

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

public class UtilReadMoviesTest {

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
    public void readMovies_success_parsesMoviesAndGenres() throws Exception {
        String content =
                "The Dark Knight,TDK123\n" +
                "action, drama\n" +
                "Mad Max,MM456\n" +
                "action\n";

        File moviesFile = writeTempFile("movies.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertNull(Util.firstError);
        assertEquals(2, movies.size());

        Movie m1 = movies.get(0);
        assertEquals("The Dark Knight", m1.getMovieName());
        assertEquals("TDK123", m1.getMovieId());
        assertEquals(2, m1.getGenre().size());
        assertEquals("action", m1.getGenre().get(0));
        assertEquals("drama", m1.getGenre().get(1));

        Movie m2 = movies.get(1);
        assertEquals("Mad Max", m2.getMovieName());
        assertEquals("MM456", m2.getMovieId());
        assertEquals(1, m2.getGenre().size());
        assertEquals("action", m2.getGenre().get(0));
    }

    @Test
    public void readMovies_invalidTitle_setsFirstErrorAndFails() throws Exception {
        String content =
                "The dark Knight,TDK123\n" +
                "action\n";

        File moviesFile = writeTempFile("movies_invalid_title.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertFalse(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertNotNull(Util.firstError);
        assertTrue(Util.firstError.contains("ERROR: Movie Title The dark Knight is wrong"));
        assertEquals(0, movies.size());
    }

    @Test
    public void readMovies_invalidMovieIdLetters_setsFirstErrorAndFails() throws Exception {
        String content =
                "The Dark Knight,TDN123\n" +  // should be TDK123
                "action\n";

        File moviesFile = writeTempFile("movies_invalid_id_letters.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertFalse(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertNotNull(Util.firstError);
        assertTrue(Util.firstError.contains("ERROR: Movie Id letters TDN123 are wrong"));
    }

    @Test
    public void readMovies_nonUniqueDigits_failsOnSecondMovie_keepsFirstMovie() throws Exception {
        String content =
                "The Dark Knight,TDK123\n" +
                "action\n" +
                "Harry Potter,HP123\n" +   // digits 123 already used
                "fantasy\n";

        File moviesFile = writeTempFile("movies_duplicate_digits.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertFalse(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertNotNull(Util.firstError);
        // Avoid fragile Unicode apostrophe matching ("aren’t") by checking a stable substring:
        assertTrue(Util.firstError.contains("Movie Id numbers HP123"));
        // The first movie should have been fully added before the failure on movie 2:
        assertEquals(1, movies.size());
        assertEquals("The Dark Knight", movies.get(0).getMovieName());
    }

    @Test
    public void readMovies_invalidGenres_setsFirstErrorAndFails() throws Exception {
        String content =
                "The Dark Knight,TDK123\n" +
                "action, drama1\n"; // 'drama1' is invalid by regex

        File moviesFile = writeTempFile("movies_invalid_genre.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertFalse(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertNotNull(Util.firstError);
        assertEquals("ERROR: Movie Genre for The Dark Knight is wrong", Util.firstError);
        assertEquals(0, movies.size());
    }

    @Test
    public void readMovies_missingCommaInTitleLine_returnsGenericError() throws Exception {
        String content =
                "The Dark Knight TDK123\n" + // missing comma
                "action\n";

        File moviesFile = writeTempFile("movies_missing_comma.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertFalse(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertEquals("Error", Util.firstError);
    }

    @Test
    public void readMovies_blankLineBetweenTitleAndGenres_isIgnored_andStillParses() throws Exception {
        // Util.readMovies() trims lines and skips empty lines (it does NOT increment li when a line is empty),
        // so a blank line between the title/id line and the genres line SHOULD be ignored safely.
        String content =
            "The Dark Knight,TDK123\n" +
            "\n" +
            "action\n";

        File moviesFile = writeTempFile("movies_blank_line.txt", content);
        List<Movie> movies = new ArrayList<>();

        assertTrue(Util.readMovies(moviesFile.getAbsolutePath(), movies));
        assertNull(Util.firstError);
        assertEquals(1, movies.size());
        assertEquals("The Dark Knight", movies.get(0).getMovieName());
        assertEquals("TDK123", movies.get(0).getMovieId());
        assertEquals(1, movies.get(0).getGenre().size());
        assertEquals("action", movies.get(0).getGenre().get(0));
    }

    @Test
    public void readMovies_nonexistentFile_setsGenericError() {
        List<Movie> movies = new ArrayList<>();
        String missingPath = new File(System.getProperty("java.io.tmpdir"), "missing_movies_" + System.nanoTime() + ".txt").getAbsolutePath();
        assertFalse(Util.readMovies(missingPath, movies));
        assertEquals("Error", Util.firstError);
    }
}

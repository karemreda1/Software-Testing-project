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

/**
 * Data-flow tests for Util.firstError behavior.
 *
 * Goal: exercise All-Defs / All-Uses / All-DU paths for the variable Util.firstError
 * without calling private helpers (setFirstError is private).
 *
 * Def sites (reachable from tests):
 *  - Util.clearFirstError(): firstError := null
 *  - Util.readMovies/readUsers: firstError := "<msg>" (via internal setFirstError) iff firstError == null
 *
 * Use sites:
 *  - Reads of Util.firstError in assertions (value must be stable after first definition).
 */
public class UtilFirstErrorDataFlowTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
    }

    @Test
    public void allDefs_allUses_allDUPaths_firstError_isSetOnce_andNotOverwritten() throws Exception {
        // File 1: triggers the generic "Error" path (missing comma in name,id line).
        File badFormat = temp.newFile("movies_bad_format.txt");
        Files.write(badFormat.toPath(),
                ("BadLineWithoutComma\n").getBytes(StandardCharsets.UTF_8));

        // File 2: triggers a different error message (invalid movie title).
        // Validation.isValidMovieTitle(name) should fail for "1@" based on existing Validation rules.
        File badTitle = temp.newFile("movies_bad_title.txt");
        Files.write(badTitle.toPath(),
                ("1@,ABC123\nAction\n").getBytes(StandardCharsets.UTF_8));

        List<Movie> movies = new ArrayList<>();

        // Def: clearFirstError() already executed in setUp -> firstError == null.
        assertNull(Util.firstError);

        // Def: firstError becomes "Error" (via readMovies -> private setFirstError).
        assertFalse(Util.readMovies(badFormat.getAbsolutePath(), movies));
        assertEquals("Error", Util.firstError);

        // Use: capture the first definition.
        String first = Util.firstError;

        // Attempted def: readMovies would like to set a different message, but must not overwrite.
        assertFalse(Util.readMovies(badTitle.getAbsolutePath(), movies));
        assertEquals(first, Util.firstError);

        // New DU path: clear then redefine via a different failing input.
        Util.clearFirstError();
        assertNull(Util.firstError);

        assertFalse(Util.readMovies(badTitle.getAbsolutePath(), movies));
        assertNotNull(Util.firstError);
        assertTrue("Expected movie-title error message after clearing firstError",
                Util.firstError.startsWith("ERROR: Movie Title"));
    }
}

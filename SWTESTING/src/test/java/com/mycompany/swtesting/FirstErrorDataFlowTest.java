package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FirstErrorDataFlowTest {

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
    }

    @Test
    public void firstError_isSetOnlyOnce_andNotOverwrittenLater() {
        assertFalse(Util.readMovies("Z:\\\\missing_movies_file_12345.txt", new ArrayList<Movie>()));
        assertEquals("Error", Util.firstError);

        // Even if another operation fails, firstError should remain unchanged unless cleared.
        assertFalse(Util.readUsers("Z:\\\\missing_users_file_12345.txt", new ArrayList<User>(), new ArrayList<Movie>()));
        assertEquals("Error", Util.firstError);
    }
}

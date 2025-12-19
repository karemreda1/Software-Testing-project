package com.mycompany.swtesting;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Top-Down integration test using a test harness that mirrors the high-level workflow:
 * readMovies -> readUsers -> recommend -> write.
 *
 * This file is intentionally additive and does not modify existing tests.
 * It uses stubs first (Top-Down), then integrates the real recommender.
 */
public class TopDownIntegrationTest {

    interface MoviesReader {
        boolean read(String path, List<Movie> movies);
    }

    interface UsersReader {
        boolean read(String path, List<User> users, List<Movie> movies);
    }

    interface Recommender {
        Map<User, List<String>> run(List<User> users, List<Movie> movies);
    }

    interface Writer {
        void write(String outPath, String content);
    }

    static class CaptureWriter implements Writer {
        String lastPath;
        String lastContent;

        @Override
        public void write(String outPath, String content) {
            lastPath = outPath;
            lastContent = content;
        }
    }

    static boolean runPipeline(
            String moviesPath,
            String usersPath,
            String outPath,
            MoviesReader moviesReader,
            UsersReader usersReader,
            Recommender recommender,
            Writer writer
    ) {
        List<Movie> movies = new ArrayList<>();
        List<User> users = new ArrayList<>();

        if (!moviesReader.read(moviesPath, movies)) {
            writer.write(outPath, Util.firstError);
            return false;
        }

        if (!usersReader.read(usersPath, users, movies)) {
            writer.write(outPath, Util.firstError);
            return false;
        }

        Map<User, List<String>> recs = recommender.run(users, movies);

        StringBuilder data = new StringBuilder();
        for (User user : users) {
            data.append(user.getUserName()).append(",").append(user.getUserId()).append("\n");
            data.append(String.join(", ", recs.get(user))).append("\n");
        }

        writer.write(outPath, data.toString());
        return true;
    }

    @Before
    public void setUp() {
        Validation.resetState();
        Util.clearFirstError();
    }

    @Test
    public void topDown_level1_allLowerModulesStubbed_pipelineFormatsOutput() {
        CaptureWriter writer = new CaptureWriter();

        MoviesReader moviesStub = (p, movies) -> {
            Movie liked = new Movie();
            liked.setMovieName("Liked");
            liked.setMovieId("L111");
            liked.addGenres("action");
            movies.add(liked);

            Movie rec = new Movie();
            rec.setMovieName("Rec");
            rec.setMovieId("R222");
            rec.addGenres("action");
            movies.add(rec);
            return true;
        };

        UsersReader usersStub = (p, users, movies) -> {
            User u = new User();
            u.setUserName("Alice");
            u.setUserId("12345678A");
            u.addLikedMovie("L111");
            users.add(u);
            return true;
        };

        Recommender recStub = (users, movies) -> {
            Map<User, List<String>> map = new HashMap<>();
            map.put(users.get(0), Collections.singletonList("Rec"));
            return map;
        };

        assertTrue(runPipeline("movies.txt", "users.txt", "recommendations.txt",
                moviesStub, usersStub, recStub, writer));

        assertEquals("recommendations.txt", writer.lastPath);
        assertEquals("Alice,12345678A\nRec\n", writer.lastContent);
    }

    @Test
    public void topDown_level2_realRecommender_stubReaders() {
        CaptureWriter writer = new CaptureWriter();

        MoviesReader moviesStub = (p, movies) -> {
            Movie liked = new Movie();
            liked.setMovieName("The Dark Knight");
            liked.setMovieId("TDK123");
            liked.addGenres("action");
            movies.add(liked);

            Movie rec = new Movie();
            rec.setMovieName("Mad Max");
            rec.setMovieId("MM234");
            rec.addGenres("action");
            movies.add(rec);
            return true;
        };

        UsersReader usersStub = (p, users, movies) -> {
            User u = new User();
            u.setUserName("Alice");
            u.setUserId("12345678A");
            u.addLikedMovie("TDK123");
            users.add(u);
            return true;
        };

        assertTrue(runPipeline("m", "u", "out",
                moviesStub, usersStub, Util::FindRecommendationForAll, writer));

        assertEquals("Alice,12345678A\nMad Max\n", writer.lastContent);
    }
}

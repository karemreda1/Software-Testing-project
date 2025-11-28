/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.swtesting;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Validation {

    private static Set<String> userIds = new HashSet<>();

    // ----------------- Movie Validation -----------------
    
    // Check if each word in the title starts with a capital letter
    public static boolean isValidMovieTitle(String title) {
        if (title == null || title.isEmpty()) return false;
        String[] words = title.split("\\s+");
        for (String w : words) {
            if (w.length() == 0 || !Character.isUpperCase(w.charAt(0))) return false;
        }
        return true;
    }

    // Movie ID: all capital letters of title + 3 unique numbers
    public static Set<String> uniqueDigits = new HashSet<>();
    private static Set<String> movieIds = new HashSet<>();
public static final int MOVIE_ID_OK = 0;
public static final int MOVIE_ID_LETTERS_WRONG = 1;
public static final int MOVIE_ID_NUMBERS_NOT_UNIQUE = 2;

public static int checkMovieId(String title, String movieId) {
    if (title == null || movieId == null) return MOVIE_ID_LETTERS_WRONG;

    StringBuilder capitals = new StringBuilder();
    for (char c : title.toCharArray()) {
        if (Character.isUpperCase(c)) capitals.append(c);
    }

    String pattern = capitals.toString() + "\\d{3}";
    if (!movieId.matches(pattern)) return MOVIE_ID_LETTERS_WRONG;

    String digits = movieId.substring(capitals.length());
    if (uniqueDigits.contains(digits)) return MOVIE_ID_NUMBERS_NOT_UNIQUE;

    uniqueDigits.add(digits);
    return MOVIE_ID_OK;
}

public static boolean isValidMovieId(String title, String movieId) {
    return checkMovieId(title, movieId) == MOVIE_ID_OK;
}

public static void resetState() {
    uniqueDigits.clear();
    userIds.clear();
}




  public static boolean isValidMovieGenres(List<String> genres) {
    if (genres == null || genres.isEmpty()) {
        return false;
    }

    for (String g : genres) {
        if (g == null) {
            return false;
        }

        String trimmed = g.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        // Only letters and spaces, words separated by single or multiple spaces
        if (!trimmed.matches("[A-Za-z]+( [A-Za-z]+)*")) {
            return false;
        }
    }

    return true;
}

    // ----------------- User Validation -----------------

    // User Name: only alphabets and spaces, no starting space
    public static boolean isValidUserName(String name) {
    if (name == null || name.isEmpty()) return false;

    
    if (Character.isWhitespace(name.charAt(0))) {
        System.out.println("ERROR: User Name cannot start with a space.");
        return false;
    }

    
    return name.matches("[A-Za-z ]+");
}

    // User ID: alphanumeric, exact length 9, start with numbers, may end with 1 alphabet
     public static boolean isValidUserId(String id) {
        if (id == null) return false;
        if (id.length() != 9) return false;
        if (userIds.contains(id)) return false; // unique check
        boolean matches = id.matches("\\d{8}[A-Za-z]?") || id.matches("\\d{9}");
        if (matches) userIds.add(id);
        return matches;
    }

//     Optional: validate liked movies (all IDs exist in movies list)
    public static boolean areValidLikedMovies(List<String> likedMovies, List<Movie> allMovies) {
        Set<String> movieIds = new HashSet<>();
        for (Movie m : allMovies) movieIds.add(m.getMovieId());

        for (String id : likedMovies) {
            if (!movieIds.contains(id)) return false;
        }
        return true;
    }
}

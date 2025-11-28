/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.swtesting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author DELL
 */
class Util {
    
    public static String firstError = null;

public static void clearFirstError() {
    firstError = null;
}

private static void setFirstError(String msg) {
    if (firstError == null) firstError = msg;
}

   public static boolean readMovies(String filename, List<Movie> moviesArr) {
    try (BufferedReader bufread = new BufferedReader(new FileReader(filename))) {
        String myline;
        Movie currentMovie = null;
        int li = 0;

        while ((myline = bufread.readLine()) != null) {
            myline = myline.trim();
    if (myline.isEmpty()) {
  
    continue;
}

            if (li % 2 == 0) {
                String[] nameId = myline.split(",");
                if (nameId.length < 2) {
                    setFirstError("Error");
                    return false;
                }

               
                String name = nameId[0].trim();
                String id = nameId[1].trim();
                if (!Validation.isValidMovieTitle(name)) {
                    setFirstError("ERROR: Movie Title " + name + " is wrong");
                    return false;
                }

                int idStatus = Validation.checkMovieId(name, id);
                if (idStatus == Validation.MOVIE_ID_LETTERS_WRONG) {
                    setFirstError("ERROR: Movie Id letters " + id + " are wrong");
                    return false;
                }
                if (idStatus == Validation.MOVIE_ID_NUMBERS_NOT_UNIQUE) {
                    setFirstError("ERROR: Movie Id numbers " + id + " aren’t unique");
                    return false;
                }

                currentMovie = new Movie();
                currentMovie.setMovieName(name);
                currentMovie.setMovieId(id);

            } else {
                if (currentMovie != null) {
                    String[] genres = myline.split(",");
                    List<String> genreList = new ArrayList<>();
                    for (String g : genres) genreList.add(g.trim());

                    if (!Validation.isValidMovieGenres(genreList)) {
                        setFirstError("ERROR: Movie Genre for " + currentMovie.getMovieName() + " is wrong");
                        return false;
                        }




                    for (String g : genreList) currentMovie.addGenres(g);
                    moviesArr.add(currentMovie);
                    currentMovie = null;
                }
            }
            li++;
        }

        return true; 
   } catch (IOException e) {
    setFirstError("Error");
    return false;
}

}


  public static boolean readUsers(String filename, List<User> usersArr, List<Movie> allMovies) {
    try (BufferedReader bufread = new BufferedReader(new FileReader(filename))) {
        String myline;
        User currentUser = null;
        int li = 0;

        while ((myline = bufread.readLine()) != null) {
            myline = myline.trim();                  // Ticket 2: ignore spaces-only lines
            if (myline.isEmpty()) continue;

            if (li % 2 == 0) { // User name + ID
                String[] nameId = myline.split(",");
                if (nameId.length < 2) {
                    setFirstError("Error");
                    return false;
                }

                String name = nameId[0].trim();
                String id = nameId[1].trim();

                if (!Validation.isValidUserName(name)) {
                    setFirstError("ERROR: User Name " + name + " is wrong");
                    return false;
                }

                if (!Validation.isValidUserId(id)) {
                    setFirstError("ERROR: User Id " + id + " is wrong");
                    return false;
                }

                currentUser = new User();
                currentUser.setUserName(name);
                currentUser.setUserId(id);

            } else { // Liked movies
                if (currentUser != null) {
                    String[] likedMovies = myline.split(",");
                    for (String movieId : likedMovies) {
                        movieId = movieId.trim();
                        if (movieId.isEmpty()) continue;

                        //  Ticket 1: validate movieId exists in allMovies
                        boolean exists = false;
                        if (allMovies != null) {
                            for (Movie m : allMovies) {
                                if (m != null && m.getMovieId() != null && m.getMovieId().equals(movieId)) {
                                    exists = true;
                                    break;
                                }
                            }
                        }

                        if (!exists) {
                            setFirstError("ERROR: Liked movie ID " + movieId + " is invalid (not found in movies.txt)");
                            return false;
                        }

                        currentUser.addLikedMovie(movieId);
                    }

                    usersArr.add(currentUser);
                    currentUser = null;
                }
            }
            li++;
        }

        // Ticket 3: file ended after header without liked line
        if (currentUser != null) {
            setFirstError("ERROR: Missing liked movies line for user " + currentUser.getUserName());
            return false;
        }

        return true;

    } catch (IOException e) {
        setFirstError("Error");
        return false;
    }
}



    
    
   public static Map<User, List<String>> FindRecommendationForAll(List<User> allUsers, List<Movie> allMovies) {
    Map<User, List<String>> recommendationsMap = new HashMap<>();

    for (User user : allUsers) {
        List<String> recommendedTitles = new ArrayList<>();

        
        List<String> likedGenres = new ArrayList<>();
        for (String likedMovieId : user.getLikedMovies()) {
            for (Movie movie : allMovies) {
                if (movie.getMovieId().equals(likedMovieId)) {
                    likedGenres.addAll(movie.getGenre());
                    break;
                }
            }
        }

      
        for (Movie movie : allMovies) {
            if (user.getLikedMovies().contains(movie.getMovieId())) {
                continue; 
            }
            for (String genre : movie.getGenre()) {
                if (likedGenres.contains(genre)) {
                    recommendedTitles.add(movie.getMovieName());
                    break;
                }
            }
        }

       
        recommendationsMap.put(user, recommendedTitles);
    }

    return recommendationsMap; 
}
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.swtesting;

import static com.mycompany.swtesting.Writefile.writeToFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author DELL
 */
public class SWTESTING {

     public static void main(String[] args) {
        Util util = new Util();
        Util.clearFirstError();          
        Validation.resetState();         
        List<Movie> movies = new ArrayList<>();
        List<User> users = new ArrayList<>();
         if (!util.readMovies("movies.txt", movies)) {
        writeToFile("recommendations.txt", Util.firstError);
        return;
    }


    
   if (!util.readUsers("users.txt", users, movies)) {
        writeToFile("recommendations.txt", Util.firstError);
        return;
    }

        
        
    System.out.println("---- Movies List ----");
    for (Movie movie : movies) {
        System.out.println("Movie: " + movie.getMovieName() + " (" + movie.getMovieId() + ")");
        System.out.println("Genres: " + String.join(", ", movie.getGenre()));
        System.out.println();
 
    }
    System.out.println("---- Users List ----");
    for (User user : users) {
        System.out.println("User: " + user.getUserName() + " (" + user.getUserId() + ")");
        System.out.println("Liked Movies: " + String.join(", ", user.getLikedMovies()));
        System.out.println(); 
    }
    
     

  Map<User, List<String>> recs = Util.FindRecommendationForAll(users, movies);
    StringBuilder data = new StringBuilder();

    for (User user : users) {
        data.append(user.getUserName()).append(",").append(user.getUserId()).append("\n");
        data.append(String.join(", ", recs.get(user))).append("\n");
    }

    writeToFile("recommendations.txt", data.toString());
}
}
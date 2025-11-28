/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.swtesting;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
class User {
    private String userName;
    private String userId;
    private List<String> likedMovies;  // Changed from array to List

    // Constructor
    public User() {
        likedMovies = new ArrayList<>();  // Initialize the list
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getLikedMovies() {
        return likedMovies;
    }

   // Setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLikedMovies(List<String> likedMovies) {
        this.likedMovies = likedMovies;
    }

    // Method to add a movie to the likedMovies list
    public void addLikedMovie(String movieId) {
        likedMovies.add(movieId);
    }

    @Override
    public String toString() {
   return "User ID: " + userId + ", Name: " + userName + ", Liked Movies: " + likedMovies.toString();


}
}
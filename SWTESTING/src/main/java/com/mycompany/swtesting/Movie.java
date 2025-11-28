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
class Movie {
    private String movieName;
    private String movieId;
    private List<String> genre = new ArrayList<>();

    // Getters
    public String getMovieName() {
        return movieName;
    }

    public String getMovieId() {
        return movieId;
    }

    public List<String> getGenre() {
        return genre;
    }
 // Setters
    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    // Set genre list at once
    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    // Add a single genre
    public void addGenre(String g) {
        genre.add(g);
    }

    // Add multiple genres
    public void addGenres(String genres) {
        genre.add(genres);
    }
    @Override
    public String toString() {
        return "Movie ID: " + movieId +
               ", Name: " + movieName +
               ", Genres: " + genre.toString();
    }
}


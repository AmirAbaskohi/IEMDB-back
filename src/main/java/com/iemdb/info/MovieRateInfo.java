package com.iemdb.info;

import com.iemdb.model.Movie;

public class MovieRateInfo {
    private int numberOfRates;
    private Double rating;

    public MovieRateInfo(Movie movie) {
        rating = movie.getRating();
//        numberOfRates = movie.getRates().size();
    }

    public int getNumberOfRates() {return numberOfRates;}
    public Double getRating() {return rating;}
}

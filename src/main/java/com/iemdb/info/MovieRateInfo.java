package com.iemdb.info;

import com.iemdb.model.Movie;

public class MovieRateInfo {
    private int numberOfRates;
    private Double rating;

    public MovieRateInfo(Movie movie, int _numberOfRates) {
        rating = movie.getRating();
        numberOfRates = _numberOfRates;
    }

    public int getNumberOfRates() {return numberOfRates;}
    public Double getRating() {return rating;}
}

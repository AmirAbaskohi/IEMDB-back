package com.iemdb.info;

import com.iemdb.model.Movie;

import java.util.ArrayList;

public class MovieInfo {
    private final int id;
    private final String name;
    private final String summary;
    private final String releaseDate;
    private final String director;
    private final String image;
    private final String coverImage;
    private final Double rating;
    private final ArrayList<String> genres;
    private final ArrayList<String> writers;
    private final int numberOfRates;
    private final double imdbRate;
    private final int ageLimit;
    private final int duration;
    private final boolean existsInWatchlist;


    public MovieInfo(Movie movie, boolean _existsInWatchlist, int _numberOfRates){
        id = movie.getId();
        name = movie.getName();
        summary = movie.getSummary();
        releaseDate = movie.getReleaseDate().toString();
        director = movie.getDirector();
        image = movie.getImageUrl();
        coverImage = movie.getCoverImageUrl();
        rating = movie.getRating();
        genres = movie.getGenres();
        writers = movie.getWriters();
        numberOfRates = _numberOfRates;
        imdbRate = movie.getImdbRate();
        ageLimit = movie.getAgeLimit();
        duration = movie.getDuration();
        existsInWatchlist = _existsInWatchlist;
    }

    public int getId(){return id;}
    public String getName(){return name;}
    public String getSummary(){return summary;}
    public String getReleaseDate(){return releaseDate;}
    public String getDirector(){return director;}
    public ArrayList<String> getWriters(){return writers;}
    public ArrayList<String> getGenres(){return genres;}
    public double getImdbRate(){return imdbRate;}
    public int getDuration(){return duration;}
    public int getAgeLimit(){return ageLimit;}
    public Double getRating(){return rating;}
    public String getImage(){return image;}
    public String getCoverImage(){return coverImage;}
    public int getNumberOfRates(){return numberOfRates;}
    public boolean isExistsInWatchlist(){return existsInWatchlist;}
}

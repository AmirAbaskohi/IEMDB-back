package com.iemdb.info;

import com.iemdb.model.Actor;
import com.iemdb.model.Comment;
import com.iemdb.model.Movie;
import com.iemdb.model.Rate;

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
    private final ArrayList<Actor> cast;
    private final ArrayList<String> writers;
    private final ArrayList<Rate> rates;
    private final double imdbRate;
    private final int ageLimit;
    private final int duration;
    private final ArrayList<Comment> comments;


    public MovieInfo(Movie movie){
        id = movie.getId();
        name = movie.getName();
        summary = movie.getSummary();
        releaseDate = movie.getReleaseDate().toString();
        director = movie.getDirector();
        image = movie.getImage();
        coverImage = movie.getCoverImage();
        rating = movie.getRating();
        genres = movie.getGenres();
        cast = movie.getCast();
        writers = movie.getWriters();
        rates = movie.getRates();
        imdbRate = movie.getImdbRate();
        ageLimit = movie.getAgeLimit();
        duration = movie.getDuration();
        comments = movie.getComments();
    }

    public int getId(){return id;}
    public String getName(){return name;}
    public String getSummary(){return summary;}
    public String getReleaseDate(){return releaseDate;}
    public String getDirector(){return director;}
    public ArrayList<String> getWriters(){return writers;}
    public ArrayList<String> getGenres(){return genres;}
    public ArrayList<Actor> getCast(){return cast;}
    public double getImdbRate(){return imdbRate;}
    public int getDuration(){return duration;}
    public int getAgeLimit(){return ageLimit;}
    public Double getRating(){return rating;}
    public ArrayList<Rate> getRates(){return rates;}
    public String getImage(){return image;}
    public String getCoverImage(){return coverImage;}
    public ArrayList<Comment> getComments(){return comments;}
}

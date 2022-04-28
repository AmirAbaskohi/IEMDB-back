package com.iemdb.info;

import com.iemdb.model.Movie;

public class AbstractMovieInfo {
    private final int id;
    private final String name;
    private final String image;
    private final double imdbRate;


    public AbstractMovieInfo(Movie movie){
        id = movie.getId();
        name = movie.getName();
        image = movie.getImage();
        imdbRate = movie.getImdbRate();
    }

    public int getId(){return id;}
    public String getName(){return name;}
    public double getImdbRate(){return imdbRate;}
    public String getImage(){return image;}
}

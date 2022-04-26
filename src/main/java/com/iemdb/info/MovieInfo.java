package com.iemdb.info;

import com.iemdb.model.Movie;

public class MovieInfo {
    private final int id;
    private final String name;
    private final String summary;
    private final String releaseDate;
    private final String director;
    private final String image;
    private final String coverImage;
    private final Double rating;

    public MovieInfo(Movie movie){
        id = movie.getId();
        name = movie.getName();
        summary = movie.getSummary();
        releaseDate = movie.getReleaseDate().toString();
        director = movie.getDirector();
        image = movie.getImage();
        coverImage = movie.getCoverImage();
        rating = movie.getRating();
    }

    public int getId(){return id;}
    public String getName(){return name;}
    public String getSummary(){return summary;}
    public String getReleaseDate(){return releaseDate;}
    public String getDirector(){return director;}
//    public ArrayList<String> getWriters(){return writers;}
//    public ArrayList<String> getGenres(){return genres;}
//    public ArrayList<String> getCast(){return cast;}
//    public ArrayList<Integer> getCastIds(){return castId;}
//    public double getImdbRate(){return imdbRate;}
//    public int getDuration(){return duration;}
//    public int getAgeLimit(){return ageLimit;}
    public Double getRating(){return rating;}
//    public ArrayList<Rate> getRates(){return rates;}
    public String getImage(){return image;}
    public String getCoverImage(){return coverImage;}
}

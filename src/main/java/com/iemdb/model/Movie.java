package com.iemdb.model;

import io.swagger.models.auth.In;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Movie {
    private int id;
    private String name;
    private String summary;
    private LocalDate releaseDate;
    private String director;
    private ArrayList<String> writers;
    private ArrayList<String> genres;
    private ArrayList<Actor> cast;
    private double imdbRate;
    private int duration;
    private int ageLimit;

    private Double rating;

    private ArrayList<Comment> comments;

    private String imageUrl;
    private String coverImageUrl;

    private static final DecimalFormat df = new DecimalFormat("0.0");

    public Movie(Map<String, Object> _movieInfo){
        writers = new ArrayList<>();
        genres = new ArrayList<>();
        cast = new ArrayList<>();
        comments = new ArrayList<>();
        id = (Integer) _movieInfo.get("id");
        name = (String) _movieInfo.get("name");
        summary = (String) _movieInfo.get("summary");
        director = (String) _movieInfo.get("director");
        imdbRate = (Double) _movieInfo.get("imdbRate");
        duration = (Integer) _movieInfo.get("duration");
        ageLimit = (Integer) _movieInfo.get("ageLimit");
        imageUrl = (String) _movieInfo.get("imageUrl");
        coverImageUrl = (String) _movieInfo.get("coverImageUrl");
        rating = (Double) _movieInfo.get("rating");

        try{
            releaseDate = ((Date)_movieInfo.get("releaseDate")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }catch (Exception e){
            System.out.println("Cannot Parse The Date!!!");
        }
    }

    public boolean hasPermissionToWatch(int userAge){
        return LocalDate.now().getYear() - userAge >= ageLimit;
    }

    public int getId(){return id;}
    public String getName(){return name;}
    public String getSummary(){return summary;}
    public LocalDate getReleaseDate(){return releaseDate;}
    public String getDirector(){return director;}
    public ArrayList<String> getWriters(){return writers;}
    public ArrayList<String> getGenres(){return genres;}
    public ArrayList<Actor> getCast(){return cast;}
    public double getImdbRate(){return imdbRate;}
    public int getDuration(){return duration;}
    public int getAgeLimit(){return ageLimit;}
    public Double getRating(){return rating;}
    public String getImageUrl(){return imageUrl;}
    public String getCoverImageUrl(){return coverImageUrl;}
    public ArrayList<Comment> getComments(){return comments;}

    public void setCast(ArrayList<Map<String, Object>> _cast) {
        for (Map<String, Object> actor : _cast) {
            LocalDate birthDate;
            try{
                birthDate = ((Date)actor.get("birthDate")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }catch (Exception e){
                System.out.println("Cannot Parse The Date!!! In set Actor.");
                birthDate = null;
            }

            cast.add(new Actor((Integer) actor.get("id"), (String) actor.get("name"), birthDate,
                    (String) actor.get("nationality"), (String) actor.get("imageUrl")));
        }
    }

    public void setComments(ArrayList<Map<String, Object>> _comments) {
        for (Map<String, Object> comment : _comments) {
            comments.add(new Comment((Integer) comment.get("id"),
                                    (String) comment.get("userEmail"),
                                    id,
                                    (String) comment.get("text")));
        }
    }

    public void setGenres(ArrayList<Map<String, Object>> _genres) {
        for (Map<String, Object> genre : _genres) {
            genres.add((String) genre.get("name"));
        }
    }

    public void setWriters(ArrayList<Map<String, Object>> _writers) {
        for (Map<String, Object> writer : _writers) {
            genres.add((String) writer.get("name"));
        }
    }

    public int compareByReleaseDate(Movie movie2){
        return this.releaseDate.compareTo(movie2.releaseDate);
    }
    public int compareByImdbRate(Movie movie2){
        return Double.compare(this.imdbRate, movie2.imdbRate);
    }
}

package com.iemdb.info;

import com.iemdb.model.Actor;
import com.iemdb.model.Movie;

import java.util.ArrayList;

public class ActorInfo {
    private final int id;
    private final String name;
    private final String birthDate;
    private final String nationality;
    private final String image;
    private ArrayList<MovieInfo> movies;

    public ActorInfo(Actor actor, ArrayList<Movie> actorMovies){
        id = actor.getId();
        name = actor.getName();
        birthDate = actor.getBirthDate();
        nationality = actor.getNationality();
        image = actor.getImage();

        movies = new ArrayList<MovieInfo>();
        for (Movie movie : actorMovies)
            movies.add(new MovieInfo(movie));
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBirthDate() { return birthDate; }
    public String getNationality() { return nationality; }
    public String getImage() { return image; }
    public ArrayList<MovieInfo> getMovies() { return movies; }
}

package com.iemdb.info;

import com.iemdb.model.Actor;
import com.iemdb.model.Movie;

import java.time.LocalDate;
import java.util.ArrayList;

public class ActorInfo {
    private final int id;
    private final String name;
    private final String birthDate;
    private final String nationality;
    private final String image;
    private int age;
    private final int numberOfMovies;

    public ActorInfo(Actor actor, ArrayList<Movie> actorMovies){
        id = actor.getId();
        name = actor.getName();
        birthDate = actor.getBirthDate().toString();
        nationality = actor.getNationality();
        image = actor.getImageUrl();
        try {
            age = LocalDate.now().getYear() - Integer.parseInt(birthDate.split(", ", 2)[1]);
        }
        catch (Exception ex) {
            age = -1;
        }
        numberOfMovies = actorMovies.size();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBirthDate() { return birthDate; }
    public String getNationality() { return nationality; }
    public String getImage() { return image; }
    public int getAge() {return age;}
    public int getNumberOfMovies() {return numberOfMovies;}
}

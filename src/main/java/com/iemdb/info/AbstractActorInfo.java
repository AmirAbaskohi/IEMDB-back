package com.iemdb.info;

import com.iemdb.model.Actor;
import com.iemdb.model.Movie;

import java.time.LocalDate;
import java.util.ArrayList;

public class AbstractActorInfo {
    private final int id;
    private final String name;
    private final String image;
    private int age;
    private final String birthDate;

    public AbstractActorInfo(Actor actor){
        id = actor.getId();
        name = actor.getName();
        image = actor.getImage();
        birthDate = actor.getBirthDate();
        try {
            age = LocalDate.now().getYear() - Integer.parseInt(birthDate.split(", ", 2)[1]);
        }
        catch (Exception ex) {
            age = -1;
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBirthDate() { return birthDate; }
    public String getImage() { return image; }
    public int getAge() {return age;}
}

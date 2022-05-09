package com.iemdb.info;

import com.iemdb.model.Actor;

import java.time.LocalDate;

public class AbstractActorInfo {
    private final int id;
    private final String name;
    private final String image;
    private int age;
    private String birthDate;

    public AbstractActorInfo(Actor actor){
        id = actor.getId();
        name = actor.getName();
        image = actor.getImageUrl();
        try {
            birthDate = actor.getBirthDate().toString();
            age = LocalDate.now().getYear() - actor.getBirthDate().getYear();
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

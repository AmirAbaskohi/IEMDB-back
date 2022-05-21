package com.iemdb.model;

import java.time.*;
import java.util.*;

public class Actor {
    private int id;
    private String name;
    private LocalDate birthDate;
    private String nationality;
    private String imageUrl;

    public Actor(int _id, String _name, LocalDate _birthDate, String _nationality, String _imageUrl){
        id = _id;
        name = _name;
        birthDate = _birthDate;
        nationality = _nationality;
        imageUrl = _imageUrl;
    }

    public Actor(Map<String, Object> data){
        id = (Integer)data.get("id");
        name = (String) data.get("name");
        try{
            birthDate = ((LocalDateTime)data.get("birthDate")).toLocalDate();
        }catch (Exception e){
            System.out.println("Cannot Parse The Date!!! In set Actor.");
            birthDate = null;
        }
        nationality = (String) data.get("nationality");
        imageUrl = (String) data.get("imageUrl");
    }

    public void update(Actor _actor){
        name = _actor.getName();
        birthDate = _actor.getBirthDate();
        nationality = _actor.getNationality();
        imageUrl = _actor.getImageUrl();
    }

    public int getId(){return id;}

    public String getName(){return name;}

    public LocalDate getBirthDate(){return birthDate;}

    public String getNationality(){return nationality;}

    public String getImageUrl(){return imageUrl;}
}

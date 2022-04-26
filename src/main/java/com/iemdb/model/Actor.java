package com.iemdb.model;

import org.json.JSONObject;

public class Actor {
    private int id;
    private String name;
    private String birthDate;
    private String nationality;
    private String image;

    public Actor(int _id, String _name, String _birthDate, String _nationality, String _image){
        id = _id;
        name = _name;
        birthDate = _birthDate;
        nationality = _nationality;
        image = _image;
    }

    public Actor(JSONObject jsonObject){
        id = jsonObject.getInt("id");
        name = jsonObject.getString("name");
        birthDate = jsonObject.getString("birthDate");
        nationality = jsonObject.getString("nationality");
        image = jsonObject.getString("image");
    }

    public void update(Actor _actor){
        name = _actor.getName();
        birthDate = _actor.getBirthDate();
        nationality = _actor.getNationality();
        image = _actor.getImage();
    }

    public int getId(){return id;}

    public String getName(){return name;}

    public String getBirthDate(){return birthDate;}

    public String getNationality(){return nationality;}

    public String getImage(){return image;}
}

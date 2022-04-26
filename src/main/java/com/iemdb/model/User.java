package com.iemdb.model;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;

public class User {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private LocalDate birthDate;

    ArrayList<Movie> watchList;

    public User(JSONObject jsonObject){
        watchList = new ArrayList<>();

        email = jsonObject.getString("email");
        password = jsonObject.getString("password");
        nickname = jsonObject.getString("nickname");
        name = jsonObject.getString("name");
        birthDate = LocalDate.parse(jsonObject.getString("birthDate"));
    }

    public void update(User _user){
        email = _user.getEmail();
        password = _user.getPassword();
        nickname = _user.getNickname();
        name = _user.getName();
        birthDate = _user.getBirthDate();
    }

    public boolean hasMovieInWatchList(int movieId){
        for(Movie movie : watchList){
            if(movie.getId() == movieId)
                return true;
        }
        return false;
    }

    public void addMovie(Movie newMovie){
        watchList.add(newMovie);
    }

    public void removeMovie(int movieId) {
        int selectedIndex = -1;
        for(int i = 0 ; i < watchList.size() ; i++){
            if(watchList.get(i).getId() == movieId) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex >= 0)
            watchList.remove(selectedIndex);
    }

    public String getEmail(){return email;}
    public String getPassword(){return password;}
    public String getNickname(){return nickname;}
    public String getName(){return name;}
    public LocalDate getBirthDate(){return birthDate;}

    public ArrayList<Movie> getWatchList() {return watchList;}
}

package com.iemdb.model;

import java.time.*;
import java.util.*;

public class User {
    private String email;
    private String password;
    private String nickName;
    private String name;
    private LocalDate birthDate;

    ArrayList<Movie> watchList;

    public User(Map<String, Object> data){
        watchList = new ArrayList<>();

        email = (String) data.get("email");
        password = (String) data.get("password");
        nickName = (String) data.get("nickName");
        name = (String) data.get("name");
        if(data.get("birthDate") != null){
            birthDate = ((LocalDateTime)data.get("birthDate")).toLocalDate();
        }

    }

    public void update(User _user){
        email = _user.getEmail();
        password = _user.getPassword();
        nickName = _user.getNickName();
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
    public String getNickName(){return nickName;}
    public String getName(){return name;}
    public LocalDate getBirthDate(){return birthDate;}

    public ArrayList<Movie> getWatchList() {return watchList;}
}

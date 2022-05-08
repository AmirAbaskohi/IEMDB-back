package com.iemdb.model;

import java.util.ArrayList;
import java.util.Map;

public class Rate {
    private String userEmail;
    private int movieId;
    private int score;

    public Rate(String _userEmail, int _movieId, int _score){
        userEmail = _userEmail;
        movieId = _movieId;
        score = _score;
    }

    public Rate(Map<String, Object> data){
        userEmail = (String) data.get("userEmail");
        movieId = (Integer)data.get("movieId");
        score = (Integer) data.get("score");

    }

    public void update(Rate rate){
        userEmail = rate.getUserEmail();
        movieId = rate.getMovieId();
        score = rate.getScore();
    }

    public String getUserEmail(){return userEmail;};
    public int getMovieId(){return movieId;};
    public int getScore(){return score;};
}

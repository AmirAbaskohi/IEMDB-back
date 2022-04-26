package com.iemdb.model;

public class Rate {
    private String userEmail;
    private int movieId;
    private int score;

    public Rate(String _userEmail, int _movieId, int _score){
        userEmail = _userEmail;
        movieId = _movieId;
        score = _score;
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

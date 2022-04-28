package com.iemdb.model;

import org.json.JSONObject;

public class Vote {
    private String userEmail;
    private int commentId;
    private int vote;

    public Vote(String _userEmail, int _commentId, int _vote){
        userEmail = _userEmail;
        commentId = _commentId;
        vote = _vote;
    }

    public void update(Vote _vote){
        userEmail = _vote.getUserEmail();
        commentId = _vote.getMovieId();
        vote = _vote.getVote();
    }

    public String getUserEmail(){return userEmail;};
    public int getMovieId(){return commentId;};
    public int getVote(){return vote;};
}

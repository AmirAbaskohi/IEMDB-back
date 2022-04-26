package com.iemdb.model;

import org.json.JSONObject;

public class Vote {
    private String userEmail;
    private int commentId;
    private int vote;

    public Vote(JSONObject jsonObject){
        userEmail = jsonObject.getString("userEmail");
        commentId = jsonObject.getInt("commentId");
        vote = jsonObject.getInt("vote");
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

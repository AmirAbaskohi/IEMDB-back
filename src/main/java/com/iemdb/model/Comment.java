package com.iemdb.model;


import org.json.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Comment {
    private int id;
    private String userEmail;
    private String userNickname;
    private int movieId;
    private String text;
    private LocalDateTime creationTime;

    ArrayList<Vote> votes;

    public Comment(JSONObject jsonObject, int _id){
        votes = new ArrayList<>();
        id = _id;
        userEmail = jsonObject.getString("userEmail");
        movieId = jsonObject.getInt("movieId");
        text = jsonObject.getString("text");
        creationTime = LocalDateTime.now();
    }

    public Comment(JSONObject jsonObject){
        votes = new ArrayList<>();
        id = 0;
        userEmail = jsonObject.getString("userEmail");
        movieId = jsonObject.getInt("movieId");
        text = jsonObject.getString("text");

        creationTime = LocalDateTime.now();
    }

    public void addVote(Vote vote){
        int voteIndex = findVote(vote.getUserEmail());
        if(voteIndex < 0 ){
            votes.add(vote);
        }
        else{
            votes.get(voteIndex).update(vote);
        }
    }

    public int findVote(String userEmail){
        for(int i=0; i < votes.size(); i++){
            if (votes.get(i).getUserEmail().equals(userEmail)){
                return i;
            }
        }
        return -1;
    }

    public JSONObject getVotes(){
        JSONObject response = new JSONObject();
        int numOfLikes = 0;
        int numOfNeutrals = 0;
        int numOfDislikes = 0;
        for (Vote vote: votes){
            if(vote.getVote() == 1)
                numOfLikes++;
            if(vote.getVote() == 0)
                numOfNeutrals++;
            if(vote.getVote() == -1)
                numOfDislikes++;
        }
        response.put("like", numOfLikes);
        response.put("neutral", numOfNeutrals);
        response.put("dislike", numOfDislikes);
        return response;
    }

    public void setId(int _id){id = _id;}

    public void setUserNickname(String _userNickname){userNickname = _userNickname;}

    public JSONObject getInfo(){
        JSONObject votes = getVotes();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("commentId", id);
        jsonObject.put("userEmail", userEmail);
        jsonObject.put("userNickname", userNickname);
        jsonObject.put("text", text);
        jsonObject.put("like", votes.getInt("like"));
        jsonObject.put("dislike", votes.getInt("dislike"));
        return jsonObject;
    }

    public int getId(){return id;}
    public String getUserEmail(){return userEmail;};
    public int getMovieId(){return movieId;};
    public String getText(){return text;};
}

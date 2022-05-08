package com.iemdb.model;


import com.iemdb.info.VoteInfo;
import org.json.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class Comment {
    private int id;
    private String userEmail;
    private String userNickname;
    private int movieId;
    private String text;

    ArrayList<Vote> votes;

    public Comment(int _id, String _userEmail, int _movieId, String _text){
        id = _id;
        votes = new ArrayList<>();
        userEmail = _userEmail;
        movieId = _movieId;
        text = _text;
    }

    public Comment(Map<String, Object> data){
        votes = new ArrayList<>();

        id = (Integer)data.get("id");
        userEmail = (String) data.get("userEmail");
        movieId = (Integer)data.get("movieId");
        text = (String) data.get("text");

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

    public VoteInfo getVotes(){
        int numOfLikes = 0;
        int numOfDislikes = 0;
        for (Vote vote: votes){
            if(vote.getVote() == 1)
                numOfLikes++;
            if(vote.getVote() == -1)
                numOfDislikes++;
        }
        return new VoteInfo(numOfLikes, numOfDislikes);
    }

    public void setId(int _id){id = _id;}
    public void setVotes(ArrayList<Vote> _votes){votes = _votes;}
    public void setUserNickname(String _userNickname){userNickname = _userNickname;}

    public int getId(){return id;}
    public String getUserEmail(){return userEmail;};
    public String getUserNickname(){return userNickname;}
    public int getMovieId(){return movieId;};
    public String getText(){return text;};
}

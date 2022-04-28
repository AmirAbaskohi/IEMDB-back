package com.iemdb.info;

public class VoteInfo {
    private int likes;
    private int dislikes;
    private int neutrals;

    public VoteInfo(int _likes, int _dislikes) {
        likes = _likes;
        dislikes = _dislikes;
    }

    public int getLikes() {return likes;}
    public int getDislikes() {return dislikes;}
    public int getNeutrals() {return neutrals;}
}

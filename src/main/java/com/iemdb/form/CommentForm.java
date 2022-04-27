package com.iemdb.form;

public class CommentForm {
    private int movieId;
    private String text;

    public CommentForm() {
        text = null;
        movieId = 0;
    }

    public CommentForm(int _movieId, String _text) {
        text = _text;
        movieId = _movieId;
    }

    public int getMovieId() {return movieId;}
    public String getText() {return text;}

    public void setMovieId(int _movieId) { movieId = _movieId; }
    public void setText(String _text) { text = _text; }
}

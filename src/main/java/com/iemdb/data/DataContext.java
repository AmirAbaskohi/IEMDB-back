package com.iemdb.data;

import com.iemdb.model.Actor;
import com.iemdb.model.Comment;
import com.iemdb.model.Movie;
import com.iemdb.model.User;

import java.util.ArrayList;

public class DataContext {

    private ArrayList<Actor> actors;
    private ArrayList<Movie> movies;
    private ArrayList<User> users;
    private ArrayList<Comment> comments;
    int numOfComments;

    public DataContext(){
        actors = new ArrayList<>();
        movies = new ArrayList<>();
        users = new ArrayList<>();
        comments = new ArrayList<>();
        numOfComments = 0;
    }

    public int findActor(int id){
        for(int i=0; i < actors.size(); i++){
            if (actors.get(i).getId() == id){
                return i;
            }
        }
        return -1;
    }

    public int findMovie(int id){
        for(int i=0; i < movies.size(); i++){
            if (movies.get(i).getId() == id){
                return i;
            }
        }
        return -1;
    }

    public int findUser(String email){
        for(int i=0; i < users.size(); i++){
            if (users.get(i).getEmail().equals(email)){
                return i;
            }
        }
        return -1;
    }

    public int findComment(int id){
        for(int i=0; i < comments.size(); i++){
            if (comments.get(i).getId() == id){
                return i;
            }
        }
        return -1;
    }

    public void addActor(Actor actor){actors.add(actor);}

    public void addMovie(Movie movie){movies.add(movie);}

    public void addUser(User user){users.add(user);}

    public void addComment(Comment comment){
        int movieIndex = findMovie(comment.getMovieId());
        comments.add(comment);
        comment.setId(comments.size());
//        movies.get(movieIndex).addComment(comment);
    }

    public ArrayList<Actor> getActors(){return actors;}
    public ArrayList<Movie> getMovies(){return movies;}
    public ArrayList<User> getUsers(){return users;}
    public ArrayList<Comment> getComments(){return comments;}
}

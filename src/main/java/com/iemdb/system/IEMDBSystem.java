package com.iemdb.system;

import com.iemdb.data.*;
import com.iemdb.exception.ForbiddenException;
import com.iemdb.exception.InvalidValueException;
import com.iemdb.exception.NotFoundException;
import com.iemdb.info.AbstractActorInfo;
import com.iemdb.info.AbstractMovieInfo;
import com.iemdb.info.AccountInfo;
import com.iemdb.info.ActorInfo;
import com.iemdb.model.*;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class IEMDBSystem {
    public static IEMDBSystem iemdbSystem;
    public static IEMDBSystem getInstance(){
        if(iemdbSystem == null){
            iemdbSystem = new IEMDBSystem();
        }
        return iemdbSystem;
    }

    private DataContext context;

    private MovieRepository movieRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private ActorRepository actorRepository;
    private WatchlistRepository watchlistRepository;

    private String currentUser = "";

    public IEMDBSystem(){
        context = new DataContext();
        movieRepository = new MovieRepository();
        commentRepository = new CommentRepository();
        userRepository = new UserRepository();
        actorRepository = new ActorRepository();
        watchlistRepository = new WatchlistRepository();
    }

    public IEMDBSystem(DataContext _context){
        context = _context;
    }

    public Comment addComment(String userEmail, String text, int movieId) throws NotFoundException{
        Movie movie = movieRepository.getMovie(movieId);
        User user = userRepository.getUserByEmail(userEmail);

        if(movie == null){
            throw new NotFoundException("MovieNotFound");
        }
        if(user == null){
            throw new NotFoundException("UserNotFound");
        }

        commentRepository.addComment(userEmail, text, movieId);

        ArrayList<Comment> comments = commentRepository.getComment(userEmail, movieId);

        return comments.get(comments.size()-1);
    }

    public Rate rateMovie(String userEmail, int movieId, int score) throws NotFoundException{
        User user = userRepository.getUserByEmail(userEmail);
        Movie movie = movieRepository.getMovie(movieId);

        if(user == null){
            throw new NotFoundException("UserNotFound");
        }
        if(movie == null){
            throw new NotFoundException("MovieNotFound");
        }
        if(score < 1 || score > 10){
            throw new RuntimeException("InvalidRateScore");
        }

        movieRepository.addRate(userEmail, movieId, score);

        return movieRepository.getRate(userEmail, movieId);
    }

    public Comment voteComment(String userEmail, int commentId, int vote) throws NotFoundException, InvalidValueException{
        User user = userRepository.getUserByEmail(userEmail);
        Comment comment = commentRepository.getCommentById(commentId);

        if(user == null){
            throw new NotFoundException("User not found.");
        }
        if(comment == null){
            throw new NotFoundException("Comment not found.");
        }
        if(vote != -1 && vote != 1){
            throw new InvalidValueException("Invalid vote value.");
        }

        commentRepository.addVote(userEmail, commentId, vote);
        return comment;
    }

    public Movie addToWatchList(String userEmail, int movieId) throws NotFoundException, ForbiddenException, InvalidValueException {
        int userIndex = context.findUser(userEmail);
        int movieIndex = context.findMovie(movieId);
        if(userIndex < 0){
            throw new NotFoundException("User not found.");
        }
        if(movieIndex < 0){
            throw new NotFoundException("Movie not found.");
        }
        User user = context.getUsers().get(userIndex);
        Movie movie = context.getMovies().get(movieIndex);

        if(user.hasMovieInWatchList(movie.getId())){
            throw new InvalidValueException("Movie already exists.");
        }

        if(!movie.hasPermissionToWatch(user.getBirthDate().getYear())){
            throw new ForbiddenException("Age is not enough.");
        }

        user.addMovie(movie);
        return movie;
    }

    public Movie removeFromWatchList(String userEmail, int movieId) throws NotFoundException {
        int userIndex = context.findUser(userEmail);
        if(userIndex < 0){
            throw new NotFoundException("User not found.");
        }
        User user = context.getUsers().get(userIndex);

        if(!user.hasMovieInWatchList(movieId)){
            throw new NotFoundException("Movie does not exist in the watchlist.");
        }

        user.removeMovie(movieId);
        return getMovieById(movieId);
    }

    public ArrayList<Movie> getMovies(Integer queryType, String query, String sort) {
        return movieRepository.getMovies(queryType, query, sort);
    }

    public Movie getMovieById(int id) throws NotFoundException{
        Movie movie = movieRepository.getMovie(id);
        if(movie == null){
            throw new NotFoundException("MovieNotFound");
        }
        return movie;
    }

//    public ArrayList<AbstractMovieInfo> getMoviesByActor(int actorId) throws NotFoundException{
//        int actorIndex = context.findActor(actorId);
//
//        if(actorIndex < 0){
//            throw new NotFoundException("Actor not found.");
//        }
//
//        ArrayList<AbstractMovieInfo> result = new ArrayList<>();
//        for (Movie movie: context.getMovies()){
//            if(movie.hasActor(actorId)){
//                result.add(new AbstractMovieInfo(movie));
//            }
//        }
//        return result;
//    }
//
    public ActorInfo getActor(int actorId) throws NotFoundException{
        Actor actor = actorRepository.getActor(actorId);

        if(actor == null){
            throw new NotFoundException("Actor not found.");
        }

        ArrayList<Movie> actorMovies = actorRepository.getActorMovies(actorId);

        return new ActorInfo(actor, actorMovies);
    }

    public ArrayList<AbstractActorInfo> getMovieActors(int movieId) throws NotFoundException{
        Movie movie = movieRepository.getMovie(movieId);
        if(movie == null){
            throw new NotFoundException("Movie not found.");
        }

        movie.setCast(movieRepository.getActors(movieId));

        ArrayList<AbstractActorInfo> movieActors = new ArrayList<>();
        for (Actor movieActor : movie.getCast()){
            movieActors.add(new AbstractActorInfo(movieActor));
        }
        return movieActors;
    }

    public ArrayList<AbstractMovieInfo> getMoviesByActor(int actorId) throws NotFoundException{
        Actor actor = actorRepository.getActor(actorId);

        if(actor == null){
            throw new NotFoundException("Actor not found.");
        }

        ArrayList<AbstractMovieInfo> result = new ArrayList<>();
        for (Movie movie: actorRepository.getActorMovies(actorId)){
            result.add(new AbstractMovieInfo(movie));
        }
        return result;
    }

    public ArrayList<Comment> getMovieComments(int movieId) throws NotFoundException{
        Movie movie = movieRepository.getMovie(movieId);
        if(movie == null){
            throw new NotFoundException("Movie not found.");
        }
        return commentRepository.getMovieComments(movieId);
    }

    public User getUser(String userEmail) throws NotFoundException{
        int userIndex = context.findUser(userEmail);
        if(userIndex < 0 ){
            throw new NotFoundException("User Not Found.");
        }
        return context.getUsers().get(userIndex);
    }

    public ArrayList<Movie> getWatchList(String userEmail) throws NotFoundException{
        User user = userRepository.getUserByEmail(userEmail);

        if(user == null){
            throw new NotFoundException("User not found.");
        }

        return watchlistRepository.getWatchlist(userEmail);
    }

    public Double calculateScore(ArrayList<Movie> userWatchList, Movie movie){
        double score = 0;
        int genre_similarity = 0;

        for (String genre: movie.getGenres()){
            for (Movie watchlistMovie: userWatchList){
                if(watchlistMovie.getGenres().contains(genre)){
                    genre_similarity += 1;
                }
            }
        }
        score += 3 * genre_similarity;
        score += movie.getImdbRate();
        if(movie.getRating() != null){
            score +=  movie.getRating();
        }
        return score;
    }

    public ArrayList<Movie> getRecommendationList(String userEmail) throws NotFoundException{
        int userIndex = context.findUser(userEmail);

        if(userIndex < 0){
            throw new NotFoundException("User not found.");
        }

        ArrayList<Movie> userWatchList = context.getUsers().get(userIndex).getWatchList();
        ArrayList<Movie> recommendationList =new ArrayList<>(context.getMovies());
        recommendationList.sort(Comparator.comparing(o -> calculateScore(userWatchList, o)));
        Collections.reverse(recommendationList);

        int numOfRecommendations = 0;
        ArrayList<Movie> result = new ArrayList<>();

        for (Movie movie: recommendationList){
            if(numOfRecommendations > 2) break;
            if(userWatchList.contains(movie)) continue;
            result.add(movie);
            numOfRecommendations += 1;
        }
        return result;
    }

    public ArrayList<Rate> getMovieRates(int movieId) {
        return movieRepository.getRates(movieId);
    }

    public String getCurrentUser(){return currentUser;}

    public AccountInfo login(String email, String password) throws NotFoundException{
        User foundedUser = userRepository.getUserByEmail(email);

        if (foundedUser == null) {
            throw new NotFoundException("UserNotFound");
        }
        if (!foundedUser.getPassword().equals(password)) {
            throw new RuntimeException("UserNameOrPasswordWrong");
        }
        currentUser = email;
        return new AccountInfo(email);
    }

    public void logout(){currentUser = "";}
}

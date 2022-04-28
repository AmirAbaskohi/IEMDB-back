package com.iemdb.system;

import com.iemdb.data.DataContext;
import com.iemdb.exception.ForbiddenException;
import com.iemdb.exception.InvalidValueException;
import com.iemdb.exception.NotFoundException;
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
            iemdbSystem.readDataFromServer();
        }
        return iemdbSystem;
    }

    private DataContext context;

    private String currentUser = "";

    public IEMDBSystem(){
        context = new DataContext();
    }

    public IEMDBSystem(DataContext _context){
        context = _context;
    }

    public void addActor(JSONObject jsonObject){
        Actor newActor = new Actor(jsonObject);
        int existId = context.findActor(newActor.getId());
        if (existId == -1){
           context.addActor(newActor);
        }
        else {
            context.getActors().get(existId).update(newActor);
        }
    }

    public void addMovie(JSONObject jsonObject) throws NotFoundException{
        Movie newMovie = new Movie(jsonObject, context.getActors());

        for(Object actorId: jsonObject.getJSONArray("cast")){
            int actorIndex = context.findActor((Integer) actorId);
            if(actorIndex < 0){
                throw new NotFoundException("Actor Not Found");
            }
            for (Actor actor : context.getActors()) {
                if (actor.getId() == actorIndex) {
                    newMovie.addCast(actor);
                    break;
                }
            }
        }
        int existId = context.findMovie(newMovie.getId());
        if (existId == -1){
            context.addMovie(newMovie);
        }
        else {
            context.getMovies().get(existId).update(newMovie);
        }
    }

    public void addUser(JSONObject jsonObject){
        User newUser = new User(jsonObject);
        int existId = context.findUser(newUser.getEmail());
        if (existId == -1){
            context.addUser(newUser);
        }
        else {
            context.getUsers().get(existId).update(newUser);
        }
    }

    public Comment addComment(String userEmail, String text, int movieId) throws NotFoundException{
        int movieIndex = context.findMovie(movieId);
        int userIndex = context.findUser(userEmail);
        if(movieIndex < 0){
            throw new NotFoundException("MovieNotFound");
        }

        Comment newComment = new Comment(context.getComments().size() + 1, userEmail, movieId, text);
        context.addComment(newComment);
        newComment.setUserNickname(context.getUsers().get(userIndex).getNickname());

        return newComment;
    }

    public Rate rateMovie(String userEmail, int movieId, int score) throws NotFoundException{
        int userIndex = context.findUser(userEmail);
        int movieIndex = context.findMovie(movieId);

        if(userIndex < 0){
            throw new NotFoundException("UserNotFound");
        }
        if(movieIndex < 0){
            throw new NotFoundException("MovieNotFound");
        }
        if(score < 1 || score > 10){
            throw new RuntimeException("InvalidRateScore");
        }
        Rate newRate = new Rate(userEmail, movieId, score);

        context.getMovies().get(movieIndex).addRate(newRate);

        return newRate;
    }

    public Comment voteComment(String userEmail, int commentId, int vote) throws NotFoundException, InvalidValueException{
        int userIndex = context.findUser(userEmail);
        int commentIndex = context.findComment(commentId);

        if(userIndex < 0){
            throw new NotFoundException("User not found.");
        }
        if(commentIndex < 0){
            throw new NotFoundException("Comment not found.");
        }
        if(vote != -1 && vote != 1){
            throw new InvalidValueException("Invalid vote value.");
        }

        Vote newVote = new Vote(userEmail, commentId, vote);
        context.getComments().get(commentIndex).addVote(newVote);
        return context.getComments().get(commentIndex);
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

    public ArrayList<Movie> getMoviesList(){
        return context.getMovies();
    }

    public Movie getMovieById(int id) throws NotFoundException{
        int movieIndex = context.findMovie(id);
        if(movieIndex < 0){
            throw new NotFoundException("MovieNotFound");
        }
        return context.getMovies().get(movieIndex);
    }

    public ArrayList<Movie> getMoviesByGenre(ArrayList<Movie> movies, String genre){
        ArrayList<Movie> result = new ArrayList<>();
        for (Movie movie: movies){
            if(movie.hasGenre(genre)){
                result.add(movie);
            }
        }
        return result;
    }

    public ArrayList<Movie> getMoviesByActor(int actorId) throws NotFoundException{
        int actorIndex = context.findActor(actorId);

        if(actorIndex < 0){
            throw new NotFoundException("Actor not found.");
        }

        ArrayList<Movie> result = new ArrayList<>();
        for (Movie movie: context.getMovies()){
            if(movie.hasActor(actorId)){
                result.add(movie);
            }
        }
        return result;
    }

    public ArrayList<Movie> getMoviesByDate(ArrayList<Movie> movies, int startYear, int endYear){
        ArrayList<Movie> result = new ArrayList<>();
        for (Movie movie: movies){
            if(movie.getReleaseDate().getYear() >= startYear && movie.getReleaseDate().getYear() <= endYear){
                result.add(movie);
            }
        }
        return result;
    }

    public ArrayList<Movie> getMoviesBySearchName(ArrayList<Movie> movies, String searchKey){
        ArrayList<Movie> result = new ArrayList<>();
        for (Movie movie: movies){
            if(StringUtils.contains(movie.getName().toLowerCase(), searchKey.toLowerCase())){
                result.add(movie);
            }
        }
        return result;
    }

    public ActorInfo getActor(int actorId) throws NotFoundException{
        int actorIndex = context.findActor(actorId);

        if(actorIndex < 0){
            throw new NotFoundException("Actor not found.");
        }
        Actor actor = context.getActors().get(actorIndex);

        ArrayList<Movie> actorMovies = new ArrayList<>();
        for(Movie movie: context.getMovies()){
            if (movie.hasActor(actorId)){
                actorMovies.add(movie);
            }
        }

        return new ActorInfo(actor, actorMovies);
    }

    public ArrayList<ActorInfo> getMovieActors(int movieId) throws NotFoundException{
        int movieIndex = context.findMovie(movieId);
        if(movieId < 0){
            throw new NotFoundException("Movie not found.");
        }

        ArrayList<ActorInfo> movieActors = new ArrayList<>();
        Movie movie = context.getMovies().get(movieIndex);
        for (Actor movieActor : movie.getCast()){
            movieActors.add(getActor(movieActor.getId()));
        }
        return movieActors;
    }

    public ArrayList<Comment> getMovieComments(int movieId) throws NotFoundException{
        int movieIndex = context.findMovie(movieId);
        if(movieId < 0){
            throw new NotFoundException("Movie not found.");
        }
        Movie movie = context.getMovies().get(movieIndex);
        return movie.getComments();
    }

    public User getUser(String userEmail) throws NotFoundException{
        int userIndex = context.findUser(userEmail);
        if(userIndex < 0 ){
            throw new NotFoundException("User Not Found.");
        }
        return context.getUsers().get(userIndex);
    }

    public ArrayList<Movie> getWatchList(String userEmail) throws NotFoundException{
        int userIndex = context.findUser(userEmail);

        if(userIndex < 0){
            throw new NotFoundException("User not found.");
        }

        return context.getUsers().get(userIndex).getWatchList();
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

    public ArrayList<Movie> sortMoviesByReleaseDate(ArrayList<Movie> movies){
        ArrayList<Movie> sortedMovies = new ArrayList<>(movies);
        sortedMovies.sort(Movie::compareByReleaseDate);
        Collections.reverse(sortedMovies);
        return sortedMovies;
    }

    public ArrayList<Movie> sortMoviesByImdbRate(ArrayList<Movie> movies){
        ArrayList<Movie> sortedMovies = new ArrayList<>(movies);
        sortedMovies.sort(Movie::compareByImdbRate);
        Collections.reverse(sortedMovies);
        return sortedMovies;
    }


    public void readDataFromServer(){
        readActorsFromServer();
        readMoviesFromServer();
        readUsersFromServer();
        readCommentsFromServer();
    }

    public void readActorsFromServer(){
        ArrayList<String> response = getResponseFromUrl("http://138.197.181.131:5000/api/v2/actors");
        JSONArray jsonArray = new JSONArray(response.get(0));
        for (Object actorData : jsonArray){
            addActor((JSONObject) actorData);
        }
    }

    public void readMoviesFromServer(){
        ArrayList<String> response = getResponseFromUrl("http://138.197.181.131:5000/api/v2/movies");
        JSONArray jsonArray = new JSONArray(response.get(0));
        for (Object movieData : jsonArray){
            try {
                addMovie((JSONObject) movieData);
            }
            catch (Exception ex) {}
        }
    }

    public void readUsersFromServer(){
        ArrayList<String> response = getResponseFromUrl("http://138.197.181.131:5000/api/users");
        JSONArray jsonArray = new JSONArray(response.get(0));
        for (Object userData : jsonArray){
            addUser((JSONObject) userData);
        }
    }

    public void readCommentsFromServer(){
        ArrayList<String> response = getResponseFromUrl("http://138.197.181.131:5000/api/comments");
        JSONArray jsonArray = new JSONArray(response.get(0));
        for (Object commentData : jsonArray){
            try {
                JSONObject comment = (JSONObject)commentData;
                addComment(comment.getString("userEmail"), comment.getString("text"), comment.getInt("movieId"));
            }
            catch (Exception ex) {}
        }
    }

    public ArrayList<String> getResponseFromUrl(String _url){
        ArrayList<String> response = new ArrayList<>();
        try{
            URL url = new URL(_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.add(inputLine);
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public String getCurrentUser(){return currentUser;}

    public AccountInfo login(String email, String password) throws NotFoundException{
        ArrayList<User> users = context.getUsers();
        User foundedUser = null;
        for (User user: users) {
            if (user.getEmail().equals(email)) {
                foundedUser = user;
                break;
            }
        }
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

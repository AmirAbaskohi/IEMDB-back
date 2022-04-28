package com.iemdb.system;

import com.iemdb.data.DataContext;
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

    public JSONObject voteComment(JSONObject jsonObject){
        JSONObject response = new JSONObject();


        int userIndex = context.findUser(jsonObject.getString("userEmail"));
        int commentIndex = context.findComment(jsonObject.getInt("commentId"));

        if(userIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "UserNotFound");
            return response;
        }
        if(commentIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "CommentNotFound");
            return response;
        }
        if(!(jsonObject.get("vote") instanceof Integer) || !(jsonObject.getInt("vote") == 1 |
                jsonObject.getInt("vote") == -1)){
            response.put("success", false);
            response.put("com/iemdb", "InvalidVoteValue");
            return response;
        }

        Vote newVote = new Vote(jsonObject);
        context.getComments().get(commentIndex).addVote(newVote);
        response.put("success", true);
        response.put("com/iemdb", "comment voted successfully");
        return response;
    }

    public JSONObject addToWatchList(JSONObject jsonObject){
        JSONObject response = new JSONObject();

        int userIndex = context.findUser(jsonObject.getString("userEmail"));
        int movieIndex = context.findMovie(jsonObject.getInt("movieId"));
        if(userIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "UserNotFound");
            return response;
        }
        if(movieIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "MovieNotFound");
            return response;
        }
        User user = context.getUsers().get(userIndex);
        Movie movie = context.getMovies().get(movieIndex);

        if(user.hasMovieInWatchList(movie.getId())){
            response.put("success", false);
            response.put("com/iemdb", "MovieAlreadyExists");
            return response;
        }

        if(!movie.hasPermissionToWatch(user.getBirthDate().getYear())){
            response.put("success", false);
            response.put("com/iemdb", "AgeLimitError");
            return response;
        }

        user.addMovie(movie);

        response.put("success", true);
        response.put("com/iemdb", "movie added to watchlist successfully");
        return response;
    }

    public JSONObject removeFromWatchList(JSONObject jsonObject){
        JSONObject response = new JSONObject();

        int userIndex = context.findUser(jsonObject.getString("userEmail"));
        if(userIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "UserNotFound");
            return response;
        }
        User user = context.getUsers().get(userIndex);

        if(!user.hasMovieInWatchList(jsonObject.getInt("movieId"))){
            response.put("success", false);
            response.put("com/iemdb", "MovieNotFound");
            return response;
        }

        user.removeMovie(jsonObject.getInt("movieId"));

        response.put("success", true);
        response.put("com/iemdb", "movie removed from watchlist successfully");
        return response;
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

//    public JSONObject getWatchList(JSONObject jsonObject){
//        JSONObject response = new JSONObject();
//        JSONObject watchList = new JSONObject();
//
//        int userIndex = context.findUser(jsonObject.getString("userEmail"));
//
//        if(userIndex < 0){
//            response.put("success", false);
//            response.put("com/iemdb", "UserNotFound");
//            return response;
//        }
//
//        ArrayList<Movie> userWatchList = context.getUsers().get(userIndex).getWatchList();
//        ArrayList<JSONObject> info = new ArrayList<>();
//        for (Movie movie: userWatchList){
//            info.add(movie.getInfoFull());
//        }
//        watchList.put("WatchList", info);
//        response.put("success", true);
//        response.put("com/iemdb", watchList);
//        return response;
//    }

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

//    public JSONObject getRecommendationList(JSONObject jsonObject){
//        JSONObject response = new JSONObject();
//        JSONObject tempObject = new JSONObject();
//
//        int userIndex = context.findUser(jsonObject.getString("userEmail"));
//
//        if(userIndex < 0){
//            response.put("success", false);
//            response.put("com/iemdb", "UserNotFound");
//            return response;
//        }
//
//        ArrayList<Movie> userWatchList = context.getUsers().get(userIndex).getWatchList();
//        ArrayList<Movie> recommendationList =new ArrayList<>(context.getMovies());
//        recommendationList.sort(Comparator.comparing(o -> calculateScore(userWatchList, o)));
//        Collections.reverse(recommendationList);
//
//        int numOfRecommendations = 0;
//        ArrayList<JSONObject> info = new ArrayList<>();
//
//        for (Movie movie: recommendationList){
//            if(numOfRecommendations > 2) break;
//
//            if(userWatchList.contains(movie)) continue;
//
//            info.add(movie.getInfoFull());
//            numOfRecommendations += 1;
//        }
//        tempObject.put("RecommendationList", info);
//        response.put("success", true);
//        response.put("com/iemdb", tempObject);
//        return response;
//    }

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

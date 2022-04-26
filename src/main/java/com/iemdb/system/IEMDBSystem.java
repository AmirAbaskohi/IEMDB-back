package com.iemdb.system;

import com.iemdb.data.DataContext;
import com.iemdb.info.AccountInfo;
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

    public void addMovie(JSONObject jsonObject){
        Movie newMovie = new Movie(jsonObject);

        for(Object actorId: jsonObject.getJSONArray("cast")){
            int actorIndex = context.findActor((Integer) actorId);
            if(actorIndex < 0){
                throw new RuntimeException("Actor Not Found");
            }
            newMovie.addCast(context.getActors().get(actorIndex).getName());
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

    public void addComment(JSONObject jsonObject){
        Comment newComment = new Comment(jsonObject);

        int userIndex = context.findUser(jsonObject.getString("userEmail"));
        int movieIndex = context.findMovie(jsonObject.getInt("movieId"));

        if(userIndex < 0){
            throw new RuntimeException("UserNotFound");
        }
        if(movieIndex < 0){
            throw new RuntimeException("MovieNotFound");
        }

        context.addComment(newComment);
        newComment.setUserNickname(context.getUsers().get(userIndex).getNickname());
    }

    public Rate rateMovie(String userEmail, int movieId, int score){
        int userIndex = context.findUser(userEmail);
        int movieIndex = context.findMovie(movieId);

        if(userIndex < 0){
            throw new RuntimeException("UserNotFound");
        }
        if(movieIndex < 0){
            throw new RuntimeException("MovieNotFound");
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

    public Movie getMovieById(int id){
        int movieIndex = context.findMovie(id);
        if(movieIndex < 0){
            throw new RuntimeException("MovieNotFound");
        }
        return context.getMovies().get(movieIndex);
    }

    public JSONObject getMoviesByGenre(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject moviesList = new JSONObject();

        String genre = jsonObject.getString("genre");

        ArrayList<JSONObject> info = new ArrayList<>();
        for (Movie movie: context.getMovies()){
            if(movie.hasGenre(genre)){
                info.add(movie.getInfoFull());
            }
        }
        moviesList.put("MoviesList", info);
        response.put("success", true);
        response.put("com/iemdb", moviesList);
        return response;
    }

    public JSONObject getMoviesByActor(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject moviesList = new JSONObject();

        int actorId = jsonObject.getInt("actorId");

        ArrayList<JSONObject> info = new ArrayList<>();
        for (Movie movie: context.getMovies()){
            if(movie.hasActor(actorId)){
                info.add(movie.getInfoFull());
            }
        }
        moviesList.put("MoviesList", info);
        response.put("success", true);
        response.put("com/iemdb", moviesList);
        return response;
    }

    public JSONObject getMoviesByDate(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject moviesList = new JSONObject();

        int startYear = jsonObject.getInt("startYear");
        int endYear = jsonObject.getInt("endYear");

        ArrayList<JSONObject> info = new ArrayList<>();
        for (Movie movie: context.getMovies()){
            if(movie.getReleaseDate().getYear() >= startYear && movie.getReleaseDate().getYear() <= endYear){
                info.add(movie.getInfoFull());
            }
        }
        moviesList.put("MoviesList", info);
        response.put("success", true);
        response.put("com/iemdb", moviesList);
        return response;
    }

    public JSONObject getMoviesBySearchName(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject moviesList = new JSONObject();

        String searchKey = jsonObject.getString("searchKey");

        ArrayList<JSONObject> info = new ArrayList<>();
        for (Movie movie: context.getMovies()){
            if(StringUtils.contains(movie.getName().toLowerCase(), searchKey.toLowerCase())){
                info.add(movie.getInfoFull());
            }
        }
        moviesList.put("MoviesList", info);
        response.put("success", true);
        response.put("com/iemdb", moviesList);
        return response;
    }

    public JSONObject getActor(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject actorJsonObject = new JSONObject();

        int actorId = jsonObject.getInt("actorId");
        int actorIndex = context.findActor(actorId);

        if(actorIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "ActorNotFound");
            return response;
        }
        Actor actor = context.getActors().get(actorIndex);

        int tma = 0;
        for(Movie movie: context.getMovies()){
            if (movie.hasActor(actorId)){
                tma += 1;
            }
        }

        actorJsonObject.put("id", actorId);
        actorJsonObject.put("name", actor.getName());
        actorJsonObject.put("birthDate", actor.getBirthDate());
//        actorJsonObject.put("age", Period.between(actor.getBirthDate(), LocalDate.now()).getYears());
        actorJsonObject.put("nationality", actor.getNationality());
        actorJsonObject.put("tma", tma);

        response.put("success", true);
        response.put("com/iemdb", actorJsonObject);
        return response;
    }

    public JSONObject getMovieActors(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject actorJsonObject = new JSONObject();

        int movieIndex = context.findMovie(jsonObject.getInt("movieId"));
        if(movieIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "MovieNotFound");
            return response;
        }

        ArrayList<JSONObject> movieActors = new ArrayList<>();
        Movie movie = context.getMovies().get(movieIndex);
        for (int actorId : movie.getCastIds()){
            JSONObject request = new JSONObject();
            request.put("actorId", actorId);
            movieActors.add(getActor(request).getJSONObject("com/iemdb"));
        }

        response.put("success", true);
        response.put("com/iemdb", movieActors);
        return response;
    }

    public JSONObject getUser(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject userJsonObject = new JSONObject();

        String userEmail = jsonObject.getString("userEmail");
        int userIndex = context.findUser(userEmail);
        if(userIndex < 0 ){
            response.put("success", false);
            response.put("com/iemdb", "UserNotFound");
            return response;
        }
        User user = context.getUsers().get(userIndex);

        userJsonObject.put("name", user.getName());
        userJsonObject.put("nickname", user.getNickname());
        userJsonObject.put("userEmail", user.getEmail());

        response.put("success", true);
        response.put("com/iemdb", userJsonObject);
        return response;
    }

    public JSONObject getWatchList(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject watchList = new JSONObject();

        int userIndex = context.findUser(jsonObject.getString("userEmail"));

        if(userIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "UserNotFound");
            return response;
        }

        ArrayList<Movie> userWatchList = context.getUsers().get(userIndex).getWatchList();
        ArrayList<JSONObject> info = new ArrayList<>();
        for (Movie movie: userWatchList){
            info.add(movie.getInfoFull());
        }
        watchList.put("WatchList", info);
        response.put("success", true);
        response.put("com/iemdb", watchList);
        return response;
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

    public JSONObject getRecommendationList(JSONObject jsonObject){
        JSONObject response = new JSONObject();
        JSONObject tempObject = new JSONObject();

        int userIndex = context.findUser(jsonObject.getString("userEmail"));

        if(userIndex < 0){
            response.put("success", false);
            response.put("com/iemdb", "UserNotFound");
            return response;
        }

        ArrayList<Movie> userWatchList = context.getUsers().get(userIndex).getWatchList();
        ArrayList<Movie> recommendationList =new ArrayList<>(context.getMovies());
        recommendationList.sort(Comparator.comparing(o -> calculateScore(userWatchList, o)));
        Collections.reverse(recommendationList);

        int numOfRecommendations = 0;
        ArrayList<JSONObject> info = new ArrayList<>();

        for (Movie movie: recommendationList){
            if(numOfRecommendations > 2) break;

            if(userWatchList.contains(movie)) continue;

            info.add(movie.getInfoFull());
            numOfRecommendations += 1;
        }
        tempObject.put("RecommendationList", info);
        response.put("success", true);
        response.put("com/iemdb", tempObject);
        return response;
    }

    public JSONObject getSortedMoviesByReleaseDate(JSONArray movies){
        ArrayList<Movie> sortedMovies = sortMoviesByReleaseDate(movies);
        Collections.reverse(sortedMovies);

        JSONObject response = new JSONObject();
        JSONObject moviesList = new JSONObject();

        ArrayList<JSONObject> info = new ArrayList<>();
        for (Movie movie: sortedMovies){
            info.add(movie.getInfoFull());
        }
        moviesList.put("MoviesList", info);
        response.put("success", true);
        response.put("com/iemdb", moviesList);
        return response;
    }

    public JSONObject getSortedMoviesByImdbRate(JSONArray movies){
        ArrayList<Movie> sortedMovies = sortMoviesByImdbRate(movies);
        Collections.reverse(sortedMovies);

        JSONObject response = new JSONObject();
        JSONObject moviesList = new JSONObject();

        ArrayList<JSONObject> info = new ArrayList<>();
        for (Movie movie: sortedMovies){
            info.add(movie.getInfoFull());
        }
        moviesList.put("MoviesList", info);
        response.put("success", true);
        response.put("com/iemdb", moviesList);
        return response;
    }

    public ArrayList<Movie> sortMoviesByReleaseDate(JSONArray movies){
        ArrayList<Movie> sortedMovies = JSONArrayMovieToArrayList(movies);
        sortedMovies.sort(Movie::compareByReleaseDate);
        return sortedMovies;
    }

    public ArrayList<Movie> sortMoviesByImdbRate(JSONArray movies){
        ArrayList<Movie> sortedMovies = JSONArrayMovieToArrayList(movies);
        sortedMovies.sort(Movie::compareByImdbRate);
        return sortedMovies;
    }

    public ArrayList<Movie> JSONArrayMovieToArrayList(JSONArray jsonArray){
        ArrayList<Movie> response = new ArrayList<>();
        for(Object object: jsonArray){
            JSONObject movieJson = (JSONObject) object;
            int movieIndex = context.findMovie(movieJson.getInt("movieId"));
            Movie movie = context.getMovies().get(movieIndex);
            response.add(movie);
        }
        return response;
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
            addMovie((JSONObject) movieData);
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
            addComment((JSONObject) commentData);
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

    public AccountInfo login(String email, String password) {
        ArrayList<User> users = context.getUsers();
        User foundedUser = null;
        for (User user: users) {
            if (user.getEmail().equals(email)) {
                foundedUser = user;
                break;
            }
        }
        if (foundedUser == null) {
            throw new RuntimeException("UserNotFound");
        }
        if (!foundedUser.getPassword().equals(password)) {
            throw new RuntimeException("UserNameOrPasswordWrong");
        }
        currentUser = email;
        return new AccountInfo(email);
    }

    public void logout(){currentUser = "";}
}

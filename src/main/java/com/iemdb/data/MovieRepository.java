package com.iemdb.data;

import com.iemdb.model.Actor;
import com.iemdb.model.Comment;
import com.iemdb.model.Movie;
import com.iemdb.model.Rate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MovieRepository {
    IemdbRepository iemdbRepository;

    public MovieRepository() {
        iemdbRepository = new IemdbRepository();
    }

    private ArrayList<Movie> getMoviesByName(String name, String sort) {
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = String.format("SELECT * FROM movie m WHERE m.name LIKE '%%%s%%'", name);
        if (sort != null) {
            String orderColumn = sort.equals("date") ? "m.releaseDate" : "m.imdbRate";
            dbQuery += " ORDER BY " + orderColumn + " DESC";
        }
        dbQuery += ";";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        for (Map<String, Object> row : queryResult) {
            Movie newMovie = new Movie(row);
            result.add(newMovie);
        }
        return result;
    }

    private ArrayList<Movie> getMoviesByGenre(String genre, String sort) {
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = String.format("SELECT m.* FROM movie m, genre_movie gm, genre g " +
                "WHERE gm.movieId = m.Id AND gm.genreId = g.Id AND g.name LIKE '%%%s%%'", genre);
        if (sort != null) {
            String orderColumn = sort.equals("date") ? "m.releaseDate" : "m.imdbRate";
            dbQuery += " ORDER BY " + orderColumn + " DESC";
        }
        dbQuery += ";";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        for (Map<String, Object> row : queryResult) {
            Movie newMovie = new Movie(row);
            result.add(newMovie);
        }
        return result;
    }

    private ArrayList<Movie> getMoviesByDate(String date, String sort) {
        int start = Integer.parseInt(date.split("-", 2)[0]);
        int end = Integer.parseInt(date.split("-", 2)[1]);
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = String.format("SELECT * FROM movie m " +
                "WHERE YEAR(m.releaseDate) >= %d AND YEAR(m.releaseDate) <= %d", start, end);
        if (sort != null) {
            String orderColumn = sort.equals("date") ? "m.releaseDate" : "m.imdbRate";
            dbQuery += " ORDER BY " + orderColumn + " DESC";
        }
        dbQuery += ";";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        for (Map<String, Object> row : queryResult) {
            Movie newMovie = new Movie(row);
            result.add(newMovie);
        }
        return result;
    }

    public ArrayList<Movie> getMovies(Integer queryType, String query, String sort) {
        if (queryType != null) {
            if (queryType == 1)
                return getMoviesByGenre(query, sort);
            else if (queryType == 2)
                return getMoviesByName(query, sort);
            else
                return getMoviesByDate(query, sort);
        }
        else {
            ArrayList<Movie> result = new ArrayList<>();
            String dbQuery = "SELECT * FROM movie";
            if (sort != null) {
                String orderColumn = sort.equals("date") ? "releaseDate" : "imdbRate";
                dbQuery += " ORDER BY " + orderColumn + " DESC";
            }
            dbQuery += ";";
            ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
            for (Map<String, Object> row : queryResult) {
                Movie newMovie = new Movie(row);
                result.add(newMovie);
            }
            return result;
        }
    }

    public Movie getMovie(int movieId) {;
        String dbQuery = String.format("SELECT * FROM movie WHERE id = %d;", movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        if (queryResult.size() == 0)
            return null;
        Movie wantedMovie = new Movie(queryResult.get(0));

        dbQuery = String.format("SELECT g.name FROM genre_movie gm, genre g " +
                "WHERE gm.movieId = %d AND gm.genreId = g.id", movieId);
        ArrayList<Map<String, Object>> movieGenres = iemdbRepository.sendQuery(dbQuery);
        wantedMovie.setGenres(movieGenres);

        dbQuery = String.format("SELECT w.name FROM writer_movie wm, writer w " +
                "WHERE wm.movieId = %d AND wm.writerId = w.id", movieId);
        ArrayList<Map<String, Object>> movieWriters = iemdbRepository.sendQuery(dbQuery);
        wantedMovie.setWriters(movieWriters);

        return wantedMovie;
    }

    public ArrayList<Actor> getActors(int movieId) {
        ArrayList<Actor> result = new ArrayList<>();
        String dbQuery = String.format("SELECT a.* FROM actor a, actor_movie am " +
                "WHERE am.movieId = %d AND a.id = am.actorId;", movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        for (Map<String, Object> row : queryResult) {
            LocalDate birthDate;
            try{
                birthDate = ((Date)row.get("birthDate")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }catch (Exception e){
                System.out.println("Cannot Parse The Date!!! In set Actor.");
                birthDate = null;
            }
            Actor actor = new Actor((Integer) row.get("id"), (String) row.get("name"),
                    birthDate, (String) row.get("nationality"), (String) row.get("imageUrl"));
            result.add(actor);
        }
        return result;
    }

    public void addRate(String userEmail, int movieId, int score){
        String dbQuery = String.format("select * from rate r " +
                "where r.movieId=%d and r.userEmail='%s'", movieId, userEmail);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        String query;
        if(queryResult.size() > 0){
            query = String.format("update rate set score=%d where movieId=%d and userEmail='%s'",
                    score, movieId, userEmail);
        }else{
            query = String.format("insert into rate values('%s', %d, %d)",
                    userEmail, movieId, score);
        }
        iemdbRepository.updateQuery(query);
    }

    public Rate getRate(String userEmail, int movieId){
        String dbQuery = String.format("select * from rate r " +
                "where r.userEmail='%s' and r.movieId=%d", userEmail, movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        if(queryResult.size() > 0){
            Rate newRate = new Rate(queryResult.get(0));
            return newRate;
        }else{
            System.out.println("Rate does not exist");
        }
        return null;
    }
}

package com.iemdb.data;

import com.iemdb.model.*;

import java.util.*;

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

        wantedMovie.setGenres(getGenres(movieId));
        wantedMovie.setWriters(getWriters(movieId));

        return wantedMovie;
    }

    public ArrayList<Actor> getActors(int movieId) {
        ArrayList<Actor> result = new ArrayList<>();
        String dbQuery = String.format("SELECT a.* FROM actor a, actor_movie am " +
                "WHERE am.movieId = %d AND a.id = am.actorId;", movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        for (Map<String, Object> row : queryResult) {
            Actor newActor = new Actor(row);
            result.add(newActor);
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

        dbQuery = String.format("UPDATE movie\n" +
                "SET rating = (SELECT SUM(r.score)/COUNT(*) FROM rate r WHERE r.movieId = %d)\n" +
                "WHERE id = %d;", movieId, movieId);
        iemdbRepository.updateQuery(dbQuery);
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

    public ArrayList<Rate> getRates(int movieId){
        String dbQuery = String.format("select * from rate r " +
                "where r.movieId=%d", movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        ArrayList<Rate> movieRates = new ArrayList<>();
        for (Map<String, Object> row : queryResult) {
            movieRates.add(new Rate(row));
        }
        return movieRates;
    }

    public ArrayList<String> getGenres(int movieId){
        String dbQuery = String.format("SELECT g.name FROM genre_movie gm, genre g " +
                "WHERE gm.movieId = %d AND gm.genreId = g.id", movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        ArrayList<String> movieGenres = new ArrayList<>();
        for (Map<String, Object> row : queryResult) {
            movieGenres.add((String) row.get("name"));
        }
        return movieGenres;
    }

    public ArrayList<String> getWriters(int movieId){
        String dbQuery = String.format("SELECT w.name FROM writer_movie wm, writer w " +
                "WHERE wm.movieId = %d AND wm.writerId = w.id", movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        ArrayList<String> movieWriters = new ArrayList<>();
        for (Map<String, Object> row : queryResult) {
            movieWriters.add((String) row.get("name"));
        }
        return movieWriters;
    }
}

package com.iemdb.data;

import com.iemdb.model.*;

import java.util.*;

public class MovieRepository {
    IemdbRepository iemdbRepository;

    public MovieRepository() {
        iemdbRepository = new IemdbRepository();
    }

    private ArrayList<Movie> getMoviesByName(String name, String sort) {
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = "SELECT * FROM movie m WHERE m.name LIKE ?";
        params.add("%" + name + "%");
        if (sort != null) {
            String orderColumn = sort.equals("date") ? "m.releaseDate" : "m.imdbRate";
            dbQuery += " ORDER BY " + orderColumn + " DESC";
        }
        dbQuery += ";";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        for (Map<String, Object> row : queryResult) {
            Movie newMovie = new Movie(row);
            result.add(newMovie);
        }
        return result;
    }

    private ArrayList<Movie> getMoviesByGenre(String genre, String sort) {
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = "SELECT m.* FROM movie m, genre_movie gm, genre g " +
                "WHERE gm.movieId = m.Id AND gm.genreId = g.Id AND g.name LIKE ?";
        params.add("%" + genre + "%");
        if (sort != null) {
            String orderColumn = sort.equals("date") ? "m.releaseDate" : "m.imdbRate";
            dbQuery += " ORDER BY " + orderColumn + " DESC";
        }
        dbQuery += ";";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        for (Map<String, Object> row : queryResult) {
            Movie newMovie = new Movie(row);
            result.add(newMovie);
        }
        return result;
    }

    private ArrayList<Movie> getMoviesByDate(String date, String sort) {
        int start = Integer.parseInt(date.split("-", 2)[0]);
        int end = Integer.parseInt(date.split("-", 2)[1]);
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = "SELECT * FROM movie m WHERE YEAR(m.releaseDate) >= ? AND YEAR(m.releaseDate) <= ?";
        params.add(start);
        params.add(end);
        if (sort != null) {
            String orderColumn = sort.equals("date") ? "m.releaseDate" : "m.imdbRate";
            dbQuery += " ORDER BY " + orderColumn + " DESC";
        }
        dbQuery += ";";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
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
            ArrayList<Object> params = new ArrayList<>();
            ArrayList<Movie> result = new ArrayList<>();
            String dbQuery = "SELECT * FROM movie m";
            if (sort != null) {
                String orderColumn = sort.equals("date") ? "m.releaseDate" : "m.imdbRate";
                dbQuery += " ORDER BY " + orderColumn + " DESC";
            }
            dbQuery += ";";
            ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
            for (Map<String, Object> row : queryResult) {
                Movie newMovie = new Movie(row);
                result.add(newMovie);
            }
            return result;
        }
    }

    public Movie getMovie(int movieId) {
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "SELECT * FROM movie WHERE id = ?;";
        params.add(movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        if (queryResult.size() == 0)
            return null;
        Movie wantedMovie = new Movie(queryResult.get(0));

        wantedMovie.setGenres(getGenres(movieId));
        wantedMovie.setWriters(getWriters(movieId));

        return wantedMovie;
    }

    public ArrayList<Actor> getActors(int movieId) {
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Actor> result = new ArrayList<>();
        String dbQuery = "SELECT a.* FROM actor a, actor_movie am WHERE am.movieId = ? AND a.id = am.actorId;";
        params.add(movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        for (Map<String, Object> row : queryResult) {
            Actor newActor = new Actor(row);
            result.add(newActor);
        }
        return result;
    }

    public void addRate(String userEmail, int movieId, int score){
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "select * from rate r where r.movieId=? and r.userEmail=?";
        params.add(movieId);
        params.add(userEmail);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        String query;
        if(queryResult.size() > 0){
            query = "update rate set score=? where movieId=? and userEmail=?";
            iemdbRepository.updateQuery(query, new ArrayList<>(List.of(score, movieId, userEmail)));
        }else{
            query = "insert into rate values(?, ?, ?)";
            iemdbRepository.updateQuery(query, new ArrayList<>(List.of(userEmail, movieId, score)));
        }

        dbQuery = "UPDATE movie\n" +
                "SET rating = (SELECT SUM(r.score)/COUNT(*) FROM rate r WHERE r.movieId = ?)\n" +
                "WHERE id = ?;";
        iemdbRepository.updateQuery(dbQuery, new ArrayList<>(List.of(movieId, movieId)));
    }

    public Rate getRate(String userEmail, int movieId){
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "select * from rate r where r.userEmail=? and r.movieId=?";
        params.add(userEmail);
        params.add(movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        if(queryResult.size() > 0){
            Rate newRate = new Rate(queryResult.get(0));
            return newRate;
        }else{
            System.out.println("Rate does not exist");
        }
        return null;
    }

    public ArrayList<Rate> getRates(int movieId){
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "select * from rate r where r.movieId=?";
        params.add(movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        ArrayList<Rate> movieRates = new ArrayList<>();
        for (Map<String, Object> row : queryResult) {
            movieRates.add(new Rate(row));
        }
        return movieRates;
    }

    public ArrayList<String> getGenres(int movieId){
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "SELECT g.name FROM genre_movie gm, genre g WHERE gm.movieId = ? AND gm.genreId = g.id";
        params.add(movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        ArrayList<String> movieGenres = new ArrayList<>();
        for (Map<String, Object> row : queryResult) {
            movieGenres.add((String) row.get("name"));
        }
        return movieGenres;
    }

    public ArrayList<String> getWriters(int movieId){
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "SELECT w.name FROM writer_movie wm, writer w WHERE wm.movieId = ? AND wm.writerId = w.id";
        params.add(movieId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        ArrayList<String> movieWriters = new ArrayList<>();
        for (Map<String, Object> row : queryResult) {
            movieWriters.add((String) row.get("name"));
        }
        return movieWriters;
    }
}

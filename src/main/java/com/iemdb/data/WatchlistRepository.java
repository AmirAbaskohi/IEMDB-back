package com.iemdb.data;

import com.iemdb.model.Movie;

import java.util.ArrayList;
import java.util.Map;

public class WatchlistRepository {
    IemdbRepository iemdbRepository;
    MovieRepository movieRepository;

    public WatchlistRepository(){
        iemdbRepository = new IemdbRepository();
        movieRepository = new MovieRepository();
    }

    public void addToWatchlist(int movieId, String userEmail) {
        String dbQuery = "SELECT * FROM watchlist ";
        dbQuery += "WHERE movieId = " + movieId + " AND userEmail = '" + userEmail + "';";
        if (iemdbRepository.sendQuery(dbQuery).size() == 0) {
            dbQuery = "INSERT INTO watchlist VALUES (" + movieId + ",'" + userEmail + "');";
            iemdbRepository.updateQuery(dbQuery);
        }
    }

    public void removeFromWatchlist(int movieId, String userEmail) {
        String dbQuery = "SELECT * FROM watchlist ";
        dbQuery += "WHERE movieId = " + movieId + " AND userEmail = '" + userEmail + "';";
        if (iemdbRepository.sendQuery(dbQuery).size() != 0) {
            dbQuery = "DELETE FROM table_name WHERE movieId = " + " AND userEmail = '" + userEmail + "'ل;";
            iemdbRepository.updateQuery(dbQuery);
        }
    }

    public ArrayList<Movie> getWatchlist(String userEmail) {
        String dbQuery = "SELECT * FROM watchlist wl, movie m";
        dbQuery += "WHERE wl.userEmail = '" + userEmail + "' AND wl.movieId = m.id;";

        ArrayList<Map<String, Object>> watchlistMovies = iemdbRepository.sendQuery(dbQuery);
        ArrayList<Movie> result = new ArrayList<>();

        for (Map<String, Object> row : watchlistMovies) {
            Movie newMovie = new Movie(row);

            newMovie.setGenres(movieRepository.getGenres(newMovie.getId()));
            newMovie.setWriters(movieRepository.getWriters(newMovie.getId()));

            result.add(newMovie);
        }
        return result;
    }
}

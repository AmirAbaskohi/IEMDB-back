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

    public boolean existsInWatchlist(int movieId, String userEmail) {
        String dbQuery = "SELECT * FROM watchlist ";
        dbQuery += "WHERE movieId = " + movieId + " AND userEmail = '" + userEmail + "';";
        return iemdbRepository.sendQuery(dbQuery).size() != 0;
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
        String dbQuery = String.format("SELECT * FROM watchlist " +
                "WHERE movieId = %d AND userEmail = '%s';", movieId, userEmail);
        if (iemdbRepository.sendQuery(dbQuery).size() != 0) {
            dbQuery = String.format("DELETE FROM watchlist " +
                    "WHERE movieId = %d And userEmail = '%s';",movieId,userEmail);
            iemdbRepository.updateQuery(dbQuery);
        }
    }

    public ArrayList<Movie> getWatchlist(String userEmail) {
        String dbQuery = String.format("SELECT * FROM watchlist wl, movie m " +
                "WHERE wl.userEmail = '%s' AND wl.movieId = m.id;", userEmail);

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

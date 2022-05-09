package com.iemdb.data;

import com.iemdb.model.Movie;

import java.util.ArrayList;
import java.util.Map;

public class WatchlistRepository {
    IemdbRepository iemdbRepository;

    public WatchlistRepository(){
        iemdbRepository = new IemdbRepository();
    }

    public void addToWatchlist(int movieId, String userEmail) {
        String dbQuery = "SELECT * FROM watchlist ";
        dbQuery += "WHERE movieId = " + movieId + " AND userEmail = '" + userEmail + "';";
        if (iemdbRepository.sendQuery(dbQuery).size() != 0) {
            dbQuery = "INSERT INTO watchlist VALUES (" + movieId + ",'" + userEmail + "');";
            iemdbRepository.updateQuery(dbQuery);
        }
    }

//    public void removeFromWatchlist() {
//
//    }

    public ArrayList<Movie> getWatchlist(String userEmail) {
        String dbQuery = "SELECT * FROM watchlist wl, movie m";
        dbQuery += "WHERE wl.userEmail = '" + userEmail + "' AND wl.movieId = m.id;";

        ArrayList<Map<String, Object>> watchlistMovies = iemdbRepository.sendQuery(dbQuery);
        ArrayList<Movie> result = new ArrayList<>();

        for (Map<String, Object> row : watchlistMovies) {
            Movie newMovie = new Movie(row);
            dbQuery = String.format("SELECT g.name FROM genre_movie gm, genre g " +
                    "WHERE gm.movieId = %d AND gm.genreId = g.id", newMovie.getId());
            ArrayList<Map<String, Object>> movieGenres = iemdbRepository.sendQuery(dbQuery);
            newMovie.setGenres(movieGenres);

            dbQuery = String.format("SELECT w.name FROM writer_movie wm, writer w " +
                    "WHERE wm.movieId = %d AND wm.writerId = w.id", newMovie.getId());
            ArrayList<Map<String, Object>> movieWriters = iemdbRepository.sendQuery(dbQuery);
            newMovie.setWriters(movieWriters);
            result.add(newMovie);
        }
        return result;
    }
}

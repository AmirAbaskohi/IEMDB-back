package com.iemdb.data;

import com.iemdb.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WatchlistRepository {
    IemdbRepository iemdbRepository;
    MovieRepository movieRepository;

    public WatchlistRepository(){
        iemdbRepository = new IemdbRepository();
        movieRepository = new MovieRepository();
    }

    public boolean existsInWatchlist(int movieId, String userEmail) {
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "SELECT * FROM watchlist WHERE movieId = ? AND userEmail = ?;";
        params.add(movieId);
        params.add(userEmail);
        return iemdbRepository.sendQuery(dbQuery, params).size() != 0;
    }

    public void addToWatchlist(int movieId, String userEmail) {
        if (!existsInWatchlist(movieId, userEmail)) {
            String dbQuery = "INSERT INTO watchlist VALUES (? , ?);";
            iemdbRepository.updateQuery(dbQuery, new ArrayList<>(List.of(movieId, userEmail)));
        }
    }

    public void removeFromWatchlist(int movieId, String userEmail) {
        if (existsInWatchlist(movieId, userEmail)) {
            String dbQuery = "DELETE FROM watchlist WHERE movieId = ? And userEmail = ?;";
            iemdbRepository.updateQuery(dbQuery, new ArrayList<>(List.of(movieId, userEmail)));
        }
    }

    public ArrayList<Movie> getWatchlist(String userEmail) {
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "SELECT * FROM watchlist wl, movie m WHERE wl.userEmail = ? AND wl.movieId = m.id;";
        params.add(userEmail);
        ArrayList<Map<String, Object>> watchlistMovies = iemdbRepository.sendQuery(dbQuery, params);
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

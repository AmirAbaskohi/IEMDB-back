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

    public ArrayList<Movie> getWatchlist(String userEmail) {
        String dbQuery = "SELECT * FROM watchlist wl, movie m";
        dbQuery += "WHERE wl.userEmail = '" + userEmail + "' AND wl.movieId = m.id;";

        ArrayList<Map<String, Object>> watchlistMovies = iemdbRepository.sendQuery(dbQuery);
        ArrayList<Movie> result = new ArrayList<>();

        for (Map<String, Object> row : watchlistMovies) {
            Movie newMovie = new Movie(row);

            newMovie.setGenres(movieRepository.getGenres(newMovie.getId()));

            dbQuery = String.format("SELECT w.name FROM writer_movie wm, writer w " +
                    "WHERE wm.movieId = %d AND wm.writerId = w.id", newMovie.getId());
            ArrayList<Map<String, Object>> movieWriters = iemdbRepository.sendQuery(dbQuery);
            newMovie.setWriters(movieWriters);
            result.add(newMovie);
        }
        return result;
    }
}

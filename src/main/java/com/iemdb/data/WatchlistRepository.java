package com.iemdb.data;

import com.iemdb.model.Movie;

import java.util.ArrayList;
import java.util.Map;

public class WatchlistRepository {
    IemdbRepository iemdbRepository;

    public WatchlistRepository(){
        iemdbRepository = new IemdbRepository();
    }

    public ArrayList<Movie> getWatchlist(String userEmail) {
        String dbQuery = "SELECT * FROM watchlist wl, movie m";
        dbQuery += "WHERE wl.userEmail = '" + userEmail + "' AND wl.movieId = m.id;";

        ArrayList<Map<String, Object>> watchlistMovies = iemdbRepository.sendQuery(dbQuery);
        ArrayList<Movie> result = new ArrayList<>();

        for (Map<String, Object> row : watchlistMovies) {
            result.add(new Movie(row));
        }
        return result;
    }
}

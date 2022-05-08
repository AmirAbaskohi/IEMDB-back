package com.iemdb.data;

import com.iemdb.model.Actor;
import com.iemdb.model.Movie;

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
        String dbQuery = "SELECT * FROM movie m ";
        dbQuery += "WHERE m.name LIKE '%" + name + "%'";
        if (sort != null) {
            String orderColumn = sort == "date" ? "m.releaseDate" : "m.imdbRate";
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
        String dbQuery = "SELECT m.* FROM movie m, genre_movie gm, genre g ";
        dbQuery += "WHERE gm.movieId = m.Id AND gm.genreId = g.Id AND g.name LIKE '%" + genre + "%'";
        if (sort != null) {
            String orderColumn = sort == "date" ? "m.releaseDate" : "m.imdbRate";
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
        String dbQuery = "SELECT * FROM movie m ";
        dbQuery += "WHERE YEAR(m.releaseDate) >= " + start + " AND YEAR(m.releaseDate) >= " + end;
        if (sort != null) {
            String orderColumn = sort == "date" ? "m.releaseDate" : "m.imdbRate";
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
                String orderColumn = sort == "date" ? "releaseDate" : "imdbRate";
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

    public Movie getMovie(int id) {;
        String dbQuery = "SELECT * FROM movie WHERE id = " + id + ";";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        if (queryResult.size() == 0)
            return null;
        Movie wantedMovie = new Movie(queryResult.get(0));

        dbQuery = "SELECT g.name FROM genre_movie gm, genre g ";
        dbQuery += "WHERE gm.movieId = " + id + " AND gm.genreId = g.id";
        ArrayList<Map<String, Object>> movieGenres = iemdbRepository.sendQuery(dbQuery);
        wantedMovie.setGenres(movieGenres);

        dbQuery = "SELECT w.name FROM writer_movie wm, writer w ";
        dbQuery += "WHERE wm.movieId = " + id + " AND wm.writerId = w.id";
        ArrayList<Map<String, Object>> movieWriters = iemdbRepository.sendQuery(dbQuery);
        wantedMovie.setWriters(movieWriters);

        return wantedMovie;
    }

    public ArrayList<Actor> getActors(int movieId) {
        ArrayList<Actor> result = new ArrayList<>();
        String dbQuery = "SELECT a.* FROM actor a, actor_movie am ";
        dbQuery += "WHERE am.movieId = " + movieId + " AND a.id = am.actorId;";
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
}

package com.iemdb.data;

import com.iemdb.model.Actor;
import com.iemdb.model.Movie;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ActorRepository {
    IemdbRepository iemdbRepository;

    public ActorRepository() {
        iemdbRepository = new IemdbRepository();
    }

    public Actor getActor(int id) {
        String dbQuery = String.format("SELECT * FROM actor WHERE Id = %d;", id);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        if (queryResult.size() > 0){
            return new Actor(queryResult.get(0));
        }
        return null;
    }

    public ArrayList<Movie> getActorMovies(int actorId) {
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = String.format("SELECT m.* FROM movie m, actor_movie am " +
                "WHERE am.actorId = %d AND m.id = am.movieId;",actorId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        for (Map<String, Object> row : queryResult) {
            Movie newMovie = new Movie(row);
            result.add(newMovie);
        }
        return result;
    }
}

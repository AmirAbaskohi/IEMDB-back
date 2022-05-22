package com.iemdb.data;

import com.iemdb.model.*;

import java.util.*;

public class ActorRepository {
    IemdbRepository iemdbRepository;

    public ActorRepository() {
        iemdbRepository = new IemdbRepository();
    }

    public Actor getActor(int actorId) {
        ArrayList<Object> params = new ArrayList<>();
        String dbQuery = "SELECT * FROM actor WHERE Id = ?;";
        params.add(actorId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        if (queryResult.size() > 0){
            return new Actor(queryResult.get(0));
        }
        return null;
    }

    public ArrayList<Movie> getActorMovies(int actorId) {
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = "SELECT m.* FROM movie m, actor_movie am WHERE am.actorId = ? AND m.id = am.movieId;";
        params.add(actorId);
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery, params);
        for (Map<String, Object> row : queryResult) {
            Movie newMovie = new Movie(row);
            result.add(newMovie);
        }
        return result;
    }
}

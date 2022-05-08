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
        String dbQuery = "SELECT * FROM actor WHERE Id = " + id + ";";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        if (queryResult.size() == 0)
            return null;
        Map<String, Object> wantedRow = queryResult.get(0);
        LocalDate birthDate;
        try{
            birthDate = ((Date)wantedRow.get("birthDate")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }catch (Exception e){
            System.out.println("Cannot Parse The Date!!! In set Actor.");
            birthDate = null;
        }
        return new Actor((Integer) wantedRow.get("id"), (String) wantedRow.get("name"),
                birthDate, (String) wantedRow.get("nationality"), (String) wantedRow.get("imageUrl"));
    }

    public ArrayList<Movie> getActorMovies(int id) {
        ArrayList<Movie> result = new ArrayList<>();
        String dbQuery = "SELECT m.* FROM movie m, actor_movie am ";
        dbQuery += "WHERE am.actorId = " + id + " AND m.id = am.movieId;";
        ArrayList<Map<String, Object>> queryResult = iemdbRepository.sendQuery(dbQuery);
        for (Map<String, Object> row : queryResult) {
            Movie newMovie = new Movie(row);
            result.add(newMovie);
        }
        return result;
    }
}

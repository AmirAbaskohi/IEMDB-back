package com.iemdb.service;

import com.iemdb.exception.NotFoundException;
import com.iemdb.info.*;
import com.iemdb.model.Comment;
import com.iemdb.model.Movie;
import com.iemdb.model.Rate;
import com.iemdb.system.IEMDBSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/movies")
public class MovieService {
    IEMDBSystem iemdbSystem = IEMDBSystem.getInstance();

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getMovies(@RequestParam(value = "queryType", required=false) Integer queryType,
                                                    @RequestParam(value = "query", required=false) String query,
                                                    @RequestParam(value = "sort", required=false) String sort) {
        if (iemdbSystem.getCurrentUser() == null ||
                iemdbSystem.getCurrentUser().isBlank() ||
                iemdbSystem.getCurrentUser().isEmpty()) {
            ResponseInfo response = new ResponseInfo(null, false, "Unauthorized.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        ArrayList<AbstractMovieInfo> abstractMoviesInfo = new ArrayList<>();
        ArrayList<Movie> movies = iemdbSystem.getMoviesList();
        ResponseInfo response = new ResponseInfo();

        if (query != null && query.isEmpty() && query.isBlank()) {
            response.setSuccess(false);
            response.setMessage("Invalid parameters.");
            response.addError("Query can not be empty.");
        }
        if ((queryType != null && query == null) || (queryType == null && query != null)) {
            response.setSuccess(false);
            response.setMessage("Invalid parameters.");
            response.addError("Query and queryType should be provided together.");
        }
        if (queryType != null && queryType != 1 && queryType != 2 && queryType != 3) {
            response.setSuccess(false);
            response.setMessage("Invalid parameters.");
            response.addError("Invalid query type.");
        }
        if (sort != null && (!sort.equals("date")) && (!sort.equals("imdb"))) {
            response.setSuccess(false);
            response.setMessage("Invalid parameters.");
            response.addError("Invalid sort parameter.");
        }
        if (queryType != null && queryType == 3 && query != null) {
            try {
                String[] splitQuery = query.split("-", 2);
                int start = Integer.parseInt(splitQuery[0]);
                int end = Integer.parseInt(splitQuery[1]);
            }
            catch (Exception ex) {
                response.setSuccess(false);
                response.setMessage("Invalid parameters.");
                response.addError("Invalid query for search by date.");
            }
        }

        if (!response.getSuccess())
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        if (query != null && queryType != null)
            movies = queryType == 1 ? iemdbSystem.getMoviesByGenre(movies, query) :
                     queryType == 2 ? iemdbSystem.getMoviesBySearchName(movies, query) :
                                      iemdbSystem.getMoviesByDate(movies, Integer.parseInt(query.split("-", 2)[0]),
                                                                          Integer.parseInt(query.split("-", 2)[1]));

        if (sort != null)
            movies = sort.equals("date") ? iemdbSystem.sortMoviesByReleaseDate(movies) :
                                      iemdbSystem.sortMoviesByImdbRate(movies) ;

        for(Movie movie : movies){
            abstractMoviesInfo.add(new AbstractMovieInfo(movie));
        }

        response = new ResponseInfo(abstractMoviesInfo, true, "Movies returned successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{movieId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getMovie(@PathVariable(value = "movieId") int movieId) {
        if (iemdbSystem.getCurrentUser() == null ||
                iemdbSystem.getCurrentUser().isBlank() ||
                iemdbSystem.getCurrentUser().isEmpty()) {
            ResponseInfo response = new ResponseInfo(null, false, "Unauthorized.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        try {
            MovieInfo movieInfo = new MovieInfo(iemdbSystem.getMovieById(movieId));
            ResponseInfo response = new ResponseInfo(movieInfo, true, "Movie returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (NotFoundException e){
            ResponseInfo response = new ResponseInfo(null, true, "Movie not found.");
            response.addError("Movie not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/{movieId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> rateMovie(@PathVariable(value = "movieId") int movieId,
                                                @RequestParam(value = "score") int score) {
        if (iemdbSystem.getCurrentUser() == null ||
                iemdbSystem.getCurrentUser().isBlank() ||
                iemdbSystem.getCurrentUser().isEmpty()) {
            ResponseInfo response = new ResponseInfo(null, false, "Unauthorized.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        try {
            Rate rate = iemdbSystem.rateMovie(iemdbSystem.getCurrentUser(), movieId, score);
            ResponseInfo response = new ResponseInfo(new MovieRateInfo(iemdbSystem.getMovieById(movieId)),true, "Movie rated successfully.");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, "Movie not found.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        catch (RuntimeException ex){
            ResponseInfo response = new ResponseInfo(null, false, "Movie rating failed.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{movieId}/actors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getMovieActors(@PathVariable(value = "movieId") int movieId) {
        if (iemdbSystem.getCurrentUser() == null ||
                iemdbSystem.getCurrentUser().isBlank() ||
                iemdbSystem.getCurrentUser().isEmpty()) {
            ResponseInfo response = new ResponseInfo(null, false, "Unauthorized.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        try {
            ArrayList<AbstractActorInfo> movieActors = iemdbSystem.getMovieActors(movieId);
            ResponseInfo response = new ResponseInfo(movieActors,true, "Movie actors returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (NotFoundException e){
            ResponseInfo response = new ResponseInfo(null, false, "Movie not found.");
            response.addError(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{movieId}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getMovieComments(@PathVariable(value = "movieId") int movieId) {
        if (iemdbSystem.getCurrentUser() == null ||
                iemdbSystem.getCurrentUser().isBlank() ||
                iemdbSystem.getCurrentUser().isEmpty()) {
            ResponseInfo response = new ResponseInfo(null, false, "Unauthorized.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        try {
            ArrayList<Comment> movieComments = iemdbSystem.getMovieComments(movieId);
            ResponseInfo response = new ResponseInfo(movieComments,true, "Movie comments returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (NotFoundException e){
            ResponseInfo response = new ResponseInfo(null, false, "Movie not found.");
            response.addError(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}

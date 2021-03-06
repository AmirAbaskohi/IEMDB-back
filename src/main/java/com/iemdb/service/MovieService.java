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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<AbstractMovieInfo> abstractMoviesInfo = new ArrayList<>();
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

        ArrayList<Movie> movies = iemdbSystem.getMovies(queryType, query, sort);

        for(Movie movie : movies){
            abstractMoviesInfo.add(new AbstractMovieInfo(movie));
        }

        response = new ResponseInfo(abstractMoviesInfo, true, "Movies returned successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{movieId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getMovie(@PathVariable(value = "movieId") int movieId,
                                                 @RequestAttribute(value = "userEmail") String userEmail) {
        try {
            Movie selectedMovie = iemdbSystem.getMovieById(movieId);
            ArrayList<Movie> watchlist = iemdbSystem.getWatchList(userEmail);
            boolean existsInWatchlist = false;
            for (Movie movie : watchlist)
                if (movie.getId() == selectedMovie.getId()) {
                    existsInWatchlist = true;
                    break;
                }
            MovieInfo movieInfo = new MovieInfo(selectedMovie, existsInWatchlist, iemdbSystem.getMovieRates(movieId).size());
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
                                                  @RequestParam(value = "score") int score,
                                                  @RequestAttribute(value = "userEmail") String userEmail) {

        try {
            iemdbSystem.rateMovie(userEmail, movieId, score);
            MovieRateInfo movieRateInfo = new MovieRateInfo(iemdbSystem.getMovieById(movieId),
                    iemdbSystem.getMovieRates(movieId).size());
            ResponseInfo response = new ResponseInfo(movieRateInfo,
                    true, "Movie rated successfully.");
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

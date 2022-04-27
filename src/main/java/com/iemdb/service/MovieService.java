package com.iemdb.service;

import com.iemdb.info.MovieInfo;
import com.iemdb.info.ResponseInfo;
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
    public ResponseEntity<ResponseInfo> getFiltered(@RequestParam(value = "queryType", required=false) Integer queryType,
                                                    @RequestParam(value = "query", required=false) String query,
                                                    @RequestParam(value = "sort", required=false) String sort) {
        ArrayList<MovieInfo> moviesInfo = new ArrayList<>();
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
                String[] splittedQuery = query.split("-", 2);
                int start = Integer.parseInt(splittedQuery[0]);
                int end = Integer.parseInt(splittedQuery[1]);
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
            movies = sort == "date" ? iemdbSystem.sortMoviesByReleaseDate(movies) :
                                      iemdbSystem.sortMoviesByImdbRate(movies) ;

        for(Movie movie : movies){
            MovieInfo movieInfo = new MovieInfo(movie);
            moviesInfo.add(movieInfo);
        }

        response = new ResponseInfo(moviesInfo, true, "Movies returned successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{movieId}/rate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> rateMovie(@PathVariable(value = "movieId") int movieId,
                                                @RequestParam(value = "score") int score) {
        try {
            Rate rate = iemdbSystem.rateMovie(iemdbSystem.getCurrentUser(), movieId, score);
            ResponseInfo response = new ResponseInfo(rate,true, "Movie rated successfully.");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }catch (Exception e){
            ResponseInfo response = new ResponseInfo(null, false, "Movie rating failed.");
            response.addError(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}

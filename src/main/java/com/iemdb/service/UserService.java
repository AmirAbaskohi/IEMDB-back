package com.iemdb.service;

import com.iemdb.exception.ForbiddenException;
import com.iemdb.exception.InvalidValueException;
import com.iemdb.exception.NotFoundException;
import com.iemdb.info.AbstractMovieInfo;
import com.iemdb.info.AccountInfo;
import com.iemdb.info.MovieInfo;
import com.iemdb.info.ResponseInfo;
import com.iemdb.model.Movie;
import com.iemdb.model.User;
import com.iemdb.system.IEMDBSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/user")
public class UserService {
    IEMDBSystem iemdbSystem = IEMDBSystem.getInstance();

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getUser(@RequestAttribute(value = "userEmail") String userEmail) {
        try {
            User user = iemdbSystem.getUser(userEmail);
            ResponseInfo response = new ResponseInfo(user, true, "User returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, "User not found.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/watchlist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getWatchlist(@RequestAttribute(value = "userEmail") String userEmail) {
        try {
            ArrayList<Movie> movies = iemdbSystem.getWatchList(userEmail);
            ArrayList<MovieInfo> moviesInfo = new ArrayList<>();
            for (Movie movie : movies)
                moviesInfo.add(new MovieInfo(movie, true, 0));
            ResponseInfo response = new ResponseInfo(moviesInfo, true, "Watchlist returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, "User not found.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/watchlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> addToWatchlist(@RequestParam(value = "movieId") int movieId,
                                                       @RequestAttribute(value = "userEmail") String userEmail) {
        try {
            Movie movie = iemdbSystem.addToWatchList(userEmail, movieId);
            ResponseInfo response = new ResponseInfo(new MovieInfo(movie, true, 0), true, "Movie added to watchlist successfully.");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, ex.getMessage());
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        catch (ForbiddenException ex) {
            ResponseInfo response = new ResponseInfo(null, false, ex.getMessage());
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        catch (InvalidValueException ex) {
            ResponseInfo response = new ResponseInfo(null, false, ex.getMessage());
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/watchlist", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> removeFromWatchlist(@RequestParam(value = "movieId") int movieId,
                                                            @RequestAttribute(value = "userEmail") String userEmail) {
        try {
            Movie movie = iemdbSystem.removeFromWatchList(userEmail, movieId);
            ResponseInfo response = new ResponseInfo(new MovieInfo(movie, false, 0), true, "Movie removed from watchlist successfully.");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, ex.getMessage());
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/recommendationList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getRecommendationList(@RequestAttribute(value = "userEmail") String userEmail) {
        try {
            ArrayList<Movie> movies = iemdbSystem.getRecommendationList(userEmail);
            ArrayList<AbstractMovieInfo> abstractMoviesInfo = new ArrayList<>();
            for (Movie movie : movies)
                abstractMoviesInfo.add(new AbstractMovieInfo(movie));
            ResponseInfo response = new ResponseInfo(abstractMoviesInfo, true, "Recommendation list returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, "User not found.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}

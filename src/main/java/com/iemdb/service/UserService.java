package com.iemdb.service;

import com.iemdb.exception.ForbiddenException;
import com.iemdb.exception.InvalidValueException;
import com.iemdb.exception.NotFoundException;
import com.iemdb.info.AccountInfo;
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
    public ResponseEntity<ResponseInfo> getUser() {
        try {
            User user = iemdbSystem.getUser(iemdbSystem.getCurrentUser());
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
    public ResponseEntity<ResponseInfo> getWatchlist() {
        try {
            ArrayList<Movie> movies = iemdbSystem.getWatchList(iemdbSystem.getCurrentUser());
            ResponseInfo response = new ResponseInfo(movies, true, "Watchlist returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, "User not found.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/watchlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> addToWatchlist(@RequestParam(value = "movieId") int movieId) {
        try {
            Movie movie = iemdbSystem.addToWatchList(iemdbSystem.getCurrentUser(), movieId);
            ResponseInfo response = new ResponseInfo(movie, true, "Movie added to watchlist successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
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
    public ResponseEntity<ResponseInfo> removeFromWatchlist(@RequestParam(value = "movieId") int movieId) {
        try {
            Movie movie = iemdbSystem.removeFromWatchList(iemdbSystem.getCurrentUser(), movieId);
            ResponseInfo response = new ResponseInfo(movie, true, "Movie removed from watchlist successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, ex.getMessage());
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/recommendationList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getRecommendationList() {
        try {
            ArrayList<Movie> movies = iemdbSystem.getRecommendationList(iemdbSystem.getCurrentUser());
            ResponseInfo response = new ResponseInfo(movies, true, "Recommendation list returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, "User not found.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}

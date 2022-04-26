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
    public ResponseEntity<ResponseInfo> getMovies() {
        ArrayList<MovieInfo> movieInfos = new ArrayList<>();
        for(Movie movie : iemdbSystem.getMoviesList()){
            MovieInfo movieInfo = new MovieInfo(movie);
            movieInfos.add(movieInfo);
        }

        ResponseInfo response = new ResponseInfo(movieInfos, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{movieId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getMovie(@PathVariable(value = "movieId") int movieId) {
        try {
            MovieInfo movieInfo = new MovieInfo(iemdbSystem.getMovieById(movieId));
            ResponseInfo response = new ResponseInfo(movieInfo, true, "Movie returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            ResponseInfo response = new ResponseInfo(null, true, "Movie not found.");
            response.addError("Movie not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
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

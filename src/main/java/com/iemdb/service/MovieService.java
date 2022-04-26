package com.iemdb.service;

import com.iemdb.info.MovieInfo;
import com.iemdb.info.RatingInfo;
import com.iemdb.info.ResponseInfo;
import com.iemdb.model.Movie;
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

        ResponseInfo response = new ResponseInfo(movieInfos);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{movieId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieInfo> getMovie(@PathVariable(value = "movieId") int movieId) {
        try {
            MovieInfo movieInfo = new MovieInfo(iemdbSystem.getMovieById(movieId));
            return new ResponseEntity<>(movieInfo, HttpStatus.ACCEPTED);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{movieId}/rate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RatingInfo> rateMovie(@PathVariable(value = "movieId") int movieId,
                                                @RequestParam(value = "score") int score) {
        try {
            iemdbSystem.rateMovie(iemdbSystem.getCurrentUser(), movieId, score);
            RatingInfo ratingInfo = new RatingInfo(true, "Movie rated successfully");
            return new ResponseEntity<>(ratingInfo, HttpStatus.ACCEPTED);
        }catch (Exception e){
            RatingInfo ratingInfo = new RatingInfo(false, e.getMessage());
            return new ResponseEntity<>(ratingInfo, HttpStatus.BAD_REQUEST);
        }
    }
}

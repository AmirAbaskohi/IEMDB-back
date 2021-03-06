package com.iemdb.service;

import com.iemdb.exception.NotFoundException;
import com.iemdb.info.AbstractMovieInfo;
import com.iemdb.info.ActorInfo;
import com.iemdb.info.ResponseInfo;
import com.iemdb.system.IEMDBSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/actors")
public class ActorService {
    IEMDBSystem iemdbSystem = IEMDBSystem.getInstance();

    @RequestMapping(value = "/{actorId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getActor(@PathVariable(value = "actorId") int actorId) {
        try {
            ActorInfo actorInfo = iemdbSystem.getActorById(actorId);
            ResponseInfo response = new ResponseInfo(actorInfo, true, "Actor returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, "Actor not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{actorId}/movies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getActorMovies(@PathVariable(value = "actorId") int actorId) {
        try {
            ArrayList<AbstractMovieInfo> actorMovies = iemdbSystem.getMoviesByActor(actorId);
            ResponseInfo response = new ResponseInfo(actorMovies, true, "Actor movies returned successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            ResponseInfo response = new ResponseInfo(null, false, "Actor not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}

package com.iemdb.service;

import com.iemdb.info.ActorInfo;
import com.iemdb.info.MovieInfo;
import com.iemdb.info.ResponseInfo;
import com.iemdb.model.Movie;
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
@RequestMapping(value = "/actor")
public class ActorService {
    IEMDBSystem iemdbSystem = IEMDBSystem.getInstance();

    @RequestMapping(value = "/{actorId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getActor(@PathVariable(value = "actorId") int actorId) {
        ActorInfo actorInfo = iemdbSystem.getActor(actorId);
        ResponseInfo response = new ResponseInfo(actorInfo, true, "Actor returned successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

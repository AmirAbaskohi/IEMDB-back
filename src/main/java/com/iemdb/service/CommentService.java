package com.iemdb.service;

import com.iemdb.exception.InvalidValueException;
import com.iemdb.exception.NotFoundException;
import com.iemdb.form.CommentForm;
import com.iemdb.info.ResponseInfo;
import com.iemdb.model.Comment;
import com.iemdb.system.IEMDBSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/comment")
public class CommentService {
    IEMDBSystem iemdbSystem = IEMDBSystem.getInstance();

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> addComment(@RequestBody CommentForm commentForm) {
        ResponseInfo response = new ResponseInfo();
        try {
            Comment newComment = iemdbSystem.addComment(iemdbSystem.getCurrentUser(), commentForm.getText(), commentForm.getMovieId());
            response.setMessage("Comment added successfully.");
            response.setSuccess(true);
            response.setValue(newComment);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            response.addError(ex.getMessage());
            response.setMessage(ex.getMessage());
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{commentId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> voteComment(@PathVariable(value = "commentId") int commentId,
                                                   @PathVariable(value = "vote") int vote) {
        ResponseInfo response = new ResponseInfo();
        try {
            Comment comment = iemdbSystem.voteComment(iemdbSystem.getCurrentUser(), commentId, vote);
            response.setMessage("Comment voted successfully.");
            response.setSuccess(true);
            response.setValue(comment);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException ex) {
            response.addError(ex.getMessage());
            response.setMessage(ex.getMessage());
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        catch (InvalidValueException ex) {
            response.addError(ex.getMessage());
            response.setMessage(ex.getMessage());
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}

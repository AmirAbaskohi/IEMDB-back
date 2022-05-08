package com.iemdb.data;

import com.iemdb.model.*;

import java.util.*;

public class CommentRepository {
    IemdbRepository iemdbRepository;

    public CommentRepository(){
        iemdbRepository = new IemdbRepository();
    }

    public Comment getCommentById(int commentId){
        String query = String.format("select * from comment c where c.id=%d", commentId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query);
        if(response.size() > 0){
            Comment newComment = new Comment(response.get(0));
            newComment.setVotes(getCommentVotes(newComment.getId()));
            return newComment;
        }else{
            System.out.println("Comment does not exist");
        }
        return null;
    }

    public ArrayList<Comment> getMovieComments(int movieId){
        ArrayList<Comment> comments = new ArrayList<>();
        String query = String.format("select * from comment c where c.movieId=%d", movieId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query);
        for(Map<String, Object> entry: response){
            Comment newComment = new Comment(entry);
            newComment.setVotes(getCommentVotes(newComment.getId()));
            comments.add(newComment);
        }
        return comments;
    }

    public ArrayList<Vote> getCommentVotes(int commentId){
        ArrayList<Vote> votes = new ArrayList<>();
        String query = String.format("select * from vote v where v.commentId=%d", commentId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query);
        for(Map<String, Object> entry: response){
            Vote newVote = new Vote(entry);
            votes.add(newVote);
        }
        return votes;
    }

    public void addVote(String userEmail, int commentId, int vote){
        String searchQuery = String.format("select * from vote v where v.commentId=%d and v.userEmail='%s'",
                commentId, userEmail);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(searchQuery);
        String query;
        if(response.size() > 0){
            query = String.format("update vote set vote=%d where commentId=%d and userEmail='%s'",
                    vote, commentId, userEmail);
        }else{
            query = String.format("insert into vote values(%d, '%s', %d)",
                    commentId, userEmail, vote);
        }
        iemdbRepository.updateQuery(query);
    }
}

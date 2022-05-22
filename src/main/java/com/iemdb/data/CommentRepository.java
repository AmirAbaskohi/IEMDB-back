package com.iemdb.data;

import com.iemdb.model.*;

import java.util.*;

public class CommentRepository {
    IemdbRepository iemdbRepository;

    public CommentRepository(){
        iemdbRepository = new IemdbRepository();
    }

    public ArrayList<Comment> getComment(String userEmail, int movieId){
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Comment> comments = new ArrayList<>();
        String query = "select * from comment c where c.userEmail=? and c.movieId=? order by c.id";
        params.add(userEmail);
        params.add(movieId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query, params);
        for(Map<String, Object> entry: response){
            Comment newComment = new Comment(entry);
            newComment.setVotes(getCommentVotes(newComment.getId()));
            newComment.setUserNickname(getCommentUserNickname(newComment.getUserEmail()));
            comments.add(newComment);
        }
        return comments;
    }

    public Comment getCommentById(int commentId){
        ArrayList<Object> params = new ArrayList<>();
        String query = "select * from comment c where c.id=?";
        params.add(commentId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query, params);
        if(response.size() > 0){
            Comment newComment = new Comment(response.get(0));
            newComment.setVotes(getCommentVotes(newComment.getId()));
            newComment.setUserNickname(getCommentUserNickname(newComment.getUserEmail()));
            return newComment;
        }else{
            System.out.println("Comment does not exist");
        }
        return null;
    }

    public ArrayList<Comment> getMovieComments(int movieId){
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Comment> comments = new ArrayList<>();
        String query = "select * from comment c where c.movieId=?";
        params.add(movieId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query, params);
        for(Map<String, Object> entry: response){
            Comment newComment = new Comment(entry);
            newComment.setVotes(getCommentVotes(newComment.getId()));
            newComment.setUserNickname(getCommentUserNickname(newComment.getUserEmail()));
            comments.add(newComment);
        }
        return comments;
    }

    public ArrayList<Vote> getCommentVotes(int commentId){
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Vote> votes = new ArrayList<>();
        String query = "select * from vote v where v.commentId=?";
        params.add(commentId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query, params);
        for(Map<String, Object> entry: response){
            Vote newVote = new Vote(entry);
            votes.add(newVote);
        }
        return votes;
    }

    public String getCommentUserNickname(String userEmail){
        ArrayList<Object> params = new ArrayList<>();
        String query = "select * from user u where u.email=?";
        params.add(userEmail);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query, params);
        if(response.size() > 0){
            User newUser = new User(response.get(0));
            return newUser.getNickName();
        }else{
            System.out.println("Comment does not exist");
        }
        return null;
    }

    public void addComment(String userEmail, String text, int movieId){
        String query = "insert into comment values(null, ?, ?, ?)";
        iemdbRepository.updateQuery(query, new ArrayList<>(List.of(userEmail, text, movieId)));
    }

    public void addVote(String userEmail, int commentId, int vote){
        ArrayList<Object> params = new ArrayList<>();
        String searchQuery = "select * from vote v where v.commentId=? and v.userEmail=?";
        params.add(commentId);
        params.add(userEmail);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(searchQuery, params);
        String query;
        if(response.size() > 0){
            query = "update vote set vote=? where commentId=? and userEmail=?";
            iemdbRepository.updateQuery(query, new ArrayList<>(List.of(vote, commentId, userEmail)));
        }else{
            query = "insert into vote values(?, ?, ?)";
            iemdbRepository.updateQuery(query, new ArrayList<>(List.of(commentId, userEmail, vote)));
        }
    }
}

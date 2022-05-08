package com.iemdb.data;

import com.iemdb.model.*;

import java.util.*;

public class CommentRepository {
    IemdbRepository iemdbRepository;

    public CommentRepository(){
        iemdbRepository = new IemdbRepository();
    }

    public ArrayList<Comment> getComment(String userEmail, int movieId){
        ArrayList<Comment> comments = new ArrayList<>();
        String query = String.format("select * from comment c where c.userEmail='%s' and c.movieId=%d order by c.id",
                userEmail, movieId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query);
        for(Map<String, Object> entry: response){
            Comment newComment = new Comment(entry);
            newComment.setVotes(getCommentVotes(newComment.getId()));
            newComment.setUserNickname(getCommentUserNickname(newComment.getUserEmail()));
            comments.add(newComment);
        }
        return comments;
    }

    public Comment getCommentById(int commentId){
        String query = String.format("select * from comment c where c.id=%d", commentId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query);
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
        ArrayList<Comment> comments = new ArrayList<>();
        String query = String.format("select * from comment c where c.movieId=%d", movieId);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query);
        for(Map<String, Object> entry: response){
            Comment newComment = new Comment(entry);
            newComment.setVotes(getCommentVotes(newComment.getId()));
            newComment.setUserNickname(getCommentUserNickname(newComment.getUserEmail()));
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

    public String getCommentUserNickname(String userEmail){
        String query = String.format("select * from user u where u.email='%s'", userEmail);
        ArrayList<Map<String, Object>> response = iemdbRepository.sendQuery(query);
        if(response.size() > 0){
            User newUser = new User(response.get(0));
            return newUser.getNickName();
        }else{
            System.out.println("Comment does not exist");
        }
        return null;
    }

    public void addComment(String userEmail, String text, int movieId){
        String query = String.format("insert into comment values(null, '%s', '%s', %d)",
                userEmail, text, movieId);
        iemdbRepository.updateQuery(query);
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

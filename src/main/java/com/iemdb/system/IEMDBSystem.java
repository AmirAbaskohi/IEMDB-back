package com.iemdb.system;

import com.iemdb.data.*;
import com.iemdb.exception.*;
import com.iemdb.info.*;
import com.iemdb.model.*;
import com.iemdb.utils.Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import java.util.*;


public class IEMDBSystem {
    public static IEMDBSystem iemdbSystem;
    public static IEMDBSystem getInstance(){
        if(iemdbSystem == null){
            iemdbSystem = new IEMDBSystem();
        }
        return iemdbSystem;
    }

    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ActorRepository actorRepository;
    private final WatchlistRepository watchlistRepository;

    private String currentUser = "";
    String key = "iemdb1401";
    SignatureAlgorithm alg;
    Key signKey;

    public IEMDBSystem(){
        movieRepository = new MovieRepository();
        commentRepository = new CommentRepository();
        userRepository = new UserRepository();
        actorRepository = new ActorRepository();
        watchlistRepository = new WatchlistRepository();

        alg = SignatureAlgorithm.HS256;
        signKey = new SecretKeySpec(Util.getSHA(key), alg.getJcaName());
    }

    public Comment addComment(String userEmail, String text, int movieId) throws NotFoundException{
        Movie movie = movieRepository.getMovie(movieId);
        User user = userRepository.getUserByEmail(userEmail);

        if(movie == null){
            throw new NotFoundException("MovieNotFound");
        }
        if(user == null){
            throw new NotFoundException("UserNotFound");
        }

        commentRepository.addComment(userEmail, text, movieId);

        ArrayList<Comment> comments = commentRepository.getComment(userEmail, movieId);

        return comments.get(comments.size()-1);
    }

    public Rate rateMovie(String userEmail, int movieId, int score) throws NotFoundException{
        User user = userRepository.getUserByEmail(userEmail);
        Movie movie = movieRepository.getMovie(movieId);

        if(user == null){
            throw new NotFoundException("UserNotFound");
        }
        if(movie == null){
            throw new NotFoundException("MovieNotFound");
        }
        if(score < 1 || score > 10){
            throw new RuntimeException("InvalidRateScore");
        }

        movieRepository.addRate(userEmail, movieId, score);

        return movieRepository.getRate(userEmail, movieId);
    }

    public Comment voteComment(String userEmail, int commentId, int vote) throws NotFoundException, InvalidValueException{
        User user = userRepository.getUserByEmail(userEmail);
        Comment comment = commentRepository.getCommentById(commentId);

        if(user == null){
            throw new NotFoundException("User not found.");
        }
        if(comment == null){
            throw new NotFoundException("Comment not found.");
        }
        if(vote != -1 && vote != 1){
            throw new InvalidValueException("Invalid vote value.");
        }

        commentRepository.addVote(userEmail, commentId, vote);
        return commentRepository.getCommentById(commentId);
    }

    public Movie addToWatchList(String userEmail, int movieId) throws NotFoundException, ForbiddenException, InvalidValueException {
        User user = userRepository.getUserByEmail(userEmail);
        Movie movie = movieRepository.getMovie(movieId);
        if(user == null){
            throw new NotFoundException("User not found.");
        }
        if(movie == null){
            throw new NotFoundException("Movie not found.");
        }

        if(watchlistRepository.existsInWatchlist(movieId, userEmail)){
            throw new InvalidValueException("Movie already exists.");
        }

        if(!movie.hasPermissionToWatch(user.getBirthDate().getYear())){
            throw new ForbiddenException("Age is not enough.");
        }

        watchlistRepository.addToWatchlist(movieId, userEmail);
        return movie;
    }

    public Movie removeFromWatchList(String userEmail, int movieId) throws NotFoundException {
        User user = userRepository.getUserByEmail(userEmail);
        Movie movie = movieRepository.getMovie(movieId);
        if(user == null){
            throw new NotFoundException("User not found.");
        }
        if(movie == null){
            throw new NotFoundException("Movie not found.");
        }

        if(!watchlistRepository.existsInWatchlist(movieId, userEmail)){
            throw new NotFoundException("Movie does not exist in the watchlist.");
        }

        watchlistRepository.removeFromWatchlist(movieId, userEmail);
        return movie;
    }

    public ArrayList<Movie> getMovies(Integer queryType, String query, String sort) {
        return movieRepository.getMovies(queryType, query, sort);
    }

    public Movie getMovieById(int id) throws NotFoundException{
        Movie movie = movieRepository.getMovie(id);
        if(movie == null){
            throw new NotFoundException("MovieNotFound");
        }
        return movie;
    }

    public ActorInfo getActorById(int actorId) throws NotFoundException{
        Actor actor = actorRepository.getActor(actorId);

        if(actor == null){
            throw new NotFoundException("Actor not found.");
        }

        ArrayList<Movie> actorMovies = actorRepository.getActorMovies(actorId);

        return new ActorInfo(actor, actorMovies);
    }

    public ArrayList<AbstractActorInfo> getMovieActors(int movieId) throws NotFoundException{
        Movie movie = movieRepository.getMovie(movieId);
        if(movie == null){
            throw new NotFoundException("Movie not found.");
        }

        movie.setCast(movieRepository.getActors(movieId));

        ArrayList<AbstractActorInfo> movieActors = new ArrayList<>();
        for (Actor movieActor : movie.getCast()){
            movieActors.add(new AbstractActorInfo(movieActor));
        }
        return movieActors;
    }

    public ArrayList<AbstractMovieInfo> getMoviesByActor(int actorId) throws NotFoundException{
        Actor actor = actorRepository.getActor(actorId);

        if(actor == null){
            throw new NotFoundException("Actor not found.");
        }

        ArrayList<AbstractMovieInfo> result = new ArrayList<>();
        for (Movie movie: actorRepository.getActorMovies(actorId)){
            result.add(new AbstractMovieInfo(movie));
        }
        return result;
    }

    public ArrayList<Comment> getMovieComments(int movieId) throws NotFoundException{
        Movie movie = movieRepository.getMovie(movieId);
        if(movie == null){
            throw new NotFoundException("Movie not found.");
        }
        return commentRepository.getMovieComments(movieId);
    }

    public User getUser(String userEmail) throws NotFoundException{
        User user = userRepository.getUserByEmail(userEmail);
        if(user == null){
            throw new NotFoundException("User Not Found.");
        }
        return user;
    }

    public ArrayList<Movie> getWatchList(String userEmail) throws NotFoundException{
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = userRepository.getUserByEmail(userEmail);
        if(user == null){
            throw new NotFoundException("User not found.");
        }
        return watchlistRepository.getWatchlist(userEmail);
    }

    public Double calculateScore(ArrayList<Movie> userWatchList, Movie movie){
        double score = 0;
        int genre_similarity = 0;

        for (String genre: movie.getGenres()){
            for (Movie watchlistMovie: userWatchList){
                if(watchlistMovie.getGenres().contains(genre)){
                    genre_similarity += 1;
                }
            }
        }
        score += 3 * genre_similarity;
        score += movie.getImdbRate();
        if(movie.getRating() != null){
            score +=  movie.getRating();
        }
        return score;
    }

    public ArrayList<Movie> getRecommendationList(String userEmail) throws NotFoundException{
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = userRepository.getUserByEmail(userEmail);

        if(user == null){
            throw new NotFoundException("User not found.");
        }

        ArrayList<Movie> userWatchList = watchlistRepository.getWatchlist(userEmail);
        ArrayList<Movie> recommendationList =new ArrayList<>(movieRepository.getMovies(null, null, null));
        for (Movie movie : recommendationList){
            movie.setGenres(movieRepository.getGenres(movie.getId()));
        }
        recommendationList.sort(Comparator.comparing(o -> calculateScore(userWatchList, o)));
        Collections.reverse(recommendationList);

        int numOfRecommendations = 0;
        ArrayList<Movie> result = new ArrayList<>();

        boolean movieExist = false;
        for (Movie movie: recommendationList){
            if(numOfRecommendations > 2)
                break;
            for(Movie m: userWatchList){
                if(m.getId() == movie.getId()){
                    movieExist = true;
                    break;
                }
            }
            if(movieExist){
                movieExist = false;
                continue;
            }
            result.add(movie);
            numOfRecommendations += 1;
        }
        return result;
    }

    public ArrayList<Rate> getMovieRates(int movieId) {
        return movieRepository.getRates(movieId);
    }

    public String createJWT(String userEmail){

        Instant now = Instant.now();

        JwtBuilder jwtBuilder = Jwts.builder()
                .claim("userEmail", userEmail)
                .setId(UUID.randomUUID().toString())
                .setIssuer("IEMDB")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(24L, ChronoUnit.HOURS)))
                .signWith(signKey, alg);

        return jwtBuilder.compact();
    }

    public Claims decodeJWT(String token){
        try {
            return Jwts.parser().setSigningKey(signKey).parseClaimsJws(token).getBody();
        }catch (Exception e){
            return null;
        }
    }

    public boolean validateJwt(Claims claims){
        if(!claims.getIssuer().equals("IEMDB")) return false;
        if(claims.getExpiration().before(Date.from(Instant.now()))) return false;
        return true;
    }

    public AccountInfo login(String userEmail, String password) throws NotFoundException{
        User foundedUser = userRepository.getUserByEmail(userEmail);
        String passHash = Util.toHexString(Util.getSHA(password));

        if (foundedUser == null) {
            throw new NotFoundException("UserNotFound");
        }
        if (!foundedUser.getPassword().equals(passHash)) {
            throw new RuntimeException("UserNameOrPasswordWrong");

        }
        currentUser = userEmail;
        return new AccountInfo(userEmail, createJWT(userEmail));
    }

    public AccountInfo signUp(String name, String nickName, String userEmail, String password, String birthDate)
    throws AlreadyExistsException{
        String passHash = Util.toHexString(Util.getSHA(password));
        User foundedUser = userRepository.getUserByEmail(userEmail);
        if (foundedUser != null) {
            throw new AlreadyExistsException("UserAlreadyExists");
        }
        currentUser = userEmail;
        userRepository.addUser(name, nickName, userEmail, passHash, birthDate);
        return new AccountInfo(userEmail, createJWT(userEmail));
    }

    public AccountInfo handleGithubUser(String name, String nickName, String userEmail, String birthDate) {
        User foundedUser = userRepository.getUserByEmail(userEmail);
        if (foundedUser == null) {
            currentUser = userEmail;
            userRepository.addUser(name, nickName, userEmail, null, birthDate);
        }
        else {
            currentUser = userEmail;
        }
        return new AccountInfo(userEmail, createJWT(userEmail));
    }

    public void logout(){currentUser = "";}
}

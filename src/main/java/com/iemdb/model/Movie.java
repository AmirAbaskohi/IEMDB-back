package com.iemdb.model;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Movie {
    private int id;
    private String name;
    private String summary;
    private LocalDate releaseDate;
    private String director;
    private ArrayList<String> writers;
    private ArrayList<String> genres;
    private ArrayList<Actor> cast;
    private double imdbRate;
    private int duration;
    private int ageLimit;

    private Double rating;

    private ArrayList<Comment> comments;
    private ArrayList<Rate> rates;

    private String imageUrl;
    private String coverImageUrl;

    private static final DecimalFormat df = new DecimalFormat("0.0");


    public Movie(JSONObject jsonObject, ArrayList<Actor> allActors){
        writers = new ArrayList<>();
        genres = new ArrayList<>();
        cast = new ArrayList<>();
        comments = new ArrayList<>();
        rates = new ArrayList<>();
        id = jsonObject.getInt("id");
        name = jsonObject.getString("name");
        summary = jsonObject.getString("summary");

        try{
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
            Date d = fmt.parse(jsonObject.getString("releaseDate"));
            releaseDate =  d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }catch (Exception e){
            System.out.println("Cannot Parse The Date!!!");
        }

        director = jsonObject.getString("director");
        imdbRate = jsonObject.getDouble("imdbRate");
        duration = jsonObject.getInt("duration");
        ageLimit = jsonObject.getInt("ageLimit");
        imageUrl = jsonObject.getString("image");
        coverImageUrl = jsonObject.getString("coverImage");

        for(Object writer: jsonObject.getJSONArray("writers")){
            writers.add((String) writer);
        }
        for(Object genre: jsonObject.getJSONArray("genres")){
            genres.add((String) genre);
        }
        ArrayList<Integer> castIds = new ArrayList<Integer>();
        for(Object actor: jsonObject.getJSONArray("cast")){
            castIds.add((Integer)actor);
        }
        for (Actor actor : allActors) {
            if (castIds.contains(actor.getId()))
                cast.add(actor);
        }
    }

    public void update(Movie _movie){
        id = _movie.getId();
        name = _movie.getName();
        summary = _movie.getSummary();
        releaseDate = _movie.getReleaseDate();
        director = _movie.getDirector();
        imdbRate = _movie.getImdbRate();
        duration = _movie.getDuration();
        ageLimit = _movie.getAgeLimit();
        writers = _movie.getWriters();
        genres = _movie.getGenres();
        cast = _movie.getCast();
    }

    public void addCast(Actor actor){
        cast.add(actor);
    }

    public void addComment(Comment comment){
        comments.add(comment);
    }

    public void addRate(Rate rate){
        int rateIndex = findRate(rate.getUserEmail());
        if(rateIndex < 0 ){
            rates.add(rate);
        }
        else{
            rates.get(rateIndex).update(rate);
        }
        updateRating();
    }

    public void updateRating(){
        double sum = 0;
        for(Rate rate: rates){
            sum += rate.getScore();
        }
        if(rates.size() == 0) rating = null;
        else rating = Double.valueOf(df.format(sum / rates.size()));
    }

    public int findRate(String userEmail){
        for(int i=0; i < rates.size(); i++){
            if (rates.get(i).getUserEmail().equals(userEmail)){
                return i;
            }
        }
        return -1;
    }

    public JSONObject getInfoAbstract(){
        JSONObject info = new JSONObject();
        info.put("movieId", id);
        info.put("name", name);
        info.put("director", director);
        info.put("genres", genres);
        info.put("rating", rating == null ? JSONObject.NULL : rating);
        return info;
    }

    public boolean hasGenre(String genre){
        return genres.contains(genre);
    }

    public boolean hasActor(int actorId){
        for (Actor actor : cast)
            if (actor.getId() == actorId)
                return true;
        return false;
    }

    public boolean hasPermissionToWatch(int userAge){
        return LocalDate.now().getYear() - userAge >= ageLimit;
    }

    public int getId(){return id;}
    public String getName(){return name;}
    public String getSummary(){return summary;}
    public LocalDate getReleaseDate(){return releaseDate;}
    public String getDirector(){return director;}
    public ArrayList<String> getWriters(){return writers;}
    public ArrayList<String> getGenres(){return genres;}
    public ArrayList<Actor> getCast(){return cast;}
    public double getImdbRate(){return imdbRate;}
    public int getDuration(){return duration;}
    public int getAgeLimit(){return ageLimit;}
    public Double getRating(){return rating;}
    public ArrayList<Rate> getRates(){return rates;}
    public String getImageUrl(){return imageUrl;}
    public String getCoverImageUrl(){return coverImageUrl;}
    public ArrayList<Comment> getComments(){return comments;}

    public int compareByReleaseDate(Movie movie2){
        return this.releaseDate.compareTo(movie2.releaseDate);
    }
    public int compareByImdbRate(Movie movie2){
        return Double.compare(this.imdbRate, movie2.imdbRate);
    }
}

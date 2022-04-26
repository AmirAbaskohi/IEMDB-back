package com.iemdb.model;

import com.iemdb.utils.Util;
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
    private ArrayList<Integer> castId;
    private ArrayList<String> cast;
    private double imdbRate;
    private int duration;
    private int ageLimit;

    private Double rating;

    private ArrayList<Comment> comments;
    private ArrayList<Rate> rates;

    private String image;
    private String coverImage;

    private static final DecimalFormat df = new DecimalFormat("0.0");


    public Movie(JSONObject jsonObject){
        writers = new ArrayList<>();
        genres = new ArrayList<>();
        castId = new ArrayList<>();
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
        image = jsonObject.getString("image");
        coverImage = jsonObject.getString("coverImage");

        for(Object writer: jsonObject.getJSONArray("writers")){
            writers.add((String) writer);
        }
        for(Object genre: jsonObject.getJSONArray("genres")){
            genres.add((String) genre);
        }
        for(Object actorId: jsonObject.getJSONArray("cast")){
            castId.add((Integer) actorId);
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

    public void addCast(String _name){
        cast.add(_name);
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

    public JSONObject getInfoFull(){
        ArrayList<JSONObject> commentsInfo = new ArrayList<>();
        for (Comment comment: comments){
            commentsInfo.add(comment.getInfo());
        }
        JSONObject info = new JSONObject();
        info.put("movieId", id);
        info.put("name", name);
        info.put("summary", summary);
        info.put("releaseDate", releaseDate);
        info.put("writers", Util.ArrayStringToString(writers));
        info.put("director", director);
        info.put("genres", Util.ArrayStringToString(genres));
        info.put("cast", Util.ArrayStringToString(cast));
        info.put("imdbRate", imdbRate);
        info.put("rating", rating == null ? JSONObject.NULL : rating);
        info.put("ageLimit", ageLimit);
        info.put("duration", duration);
        info.put("comments", commentsInfo);

        return info;
    }

    public boolean hasGenre(String genre){
        return genres.contains(genre);
    }

    public boolean hasActor(int actorId){
        return castId.contains(actorId);
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
    public ArrayList<String> getCast(){return cast;}
    public ArrayList<Integer> getCastIds(){return castId;}
    public double getImdbRate(){return imdbRate;}
    public int getDuration(){return duration;}
    public int getAgeLimit(){return ageLimit;}
    public Double getRating(){return rating;}
    public ArrayList<Rate> getRates(){return rates;}
    public String getImage(){return image;}
    public String getCoverImage(){return coverImage;}

    public int compareByReleaseDate(Movie movie2){
        return this.releaseDate.compareTo(movie2.releaseDate);
    }
    public int compareByImdbRate(Movie movie2){
        return Double.compare(this.imdbRate, movie2.imdbRate);
    }
}

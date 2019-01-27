package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Event implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String eventId;


    @ColumnInfo
    private String eventsDate;

    @ColumnInfo
    private String eventsTime;

    @ColumnInfo
    private double max_Price;

    @ColumnInfo
    private double min_Price;

    @ColumnInfo
    private int numComments;

    @ColumnInfo
    private int numLikes;

    @ColumnInfo
    private double lon;

    @ColumnInfo
    private double lat;

    @ColumnInfo
    private String genreId;

    @ColumnInfo
    private String eventUrl;

    @ColumnInfo
    private String genre;

    public void printObj() {
        System.out.println("*********************");
        System.out.println(name);
        System.out.println(eventId);
        System.out.println(lat);
        System.out.println(lon);
        System.out.println(genreId);
        System.out.println(eventUrl);
        System.out.println(min_Price);
        System.out.println(max_Price);
        System.out.println(numComments);
        System.out.println(numLikes);
        System.out.println(eventsDate);
        System.out.println(eventsTime);
        System.out.println(eventsDate);
        System.out.println(genre);
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getEventsTime() {
        return eventsTime;
    }

    public void setEventsTime(String eventsTime) {
        this.eventsTime = eventsTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public String getEventsDate() {
        return eventsDate;
    }

    public void setEventsDate(String eventsDate) {
        this.eventsDate = eventsDate;
    }

    public double getMax_Price() {
        return max_Price;
    }

    public void setMax_Price(double max_Price) {
        this.max_Price = max_Price;
    }

    public double getMin_Price() {
        return min_Price;
    }

    public void setMin_Price(double min_Price) {
        this.min_Price = min_Price;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComents) {
        this.numComments = numComents;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }
}

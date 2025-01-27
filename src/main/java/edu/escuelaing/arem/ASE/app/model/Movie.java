package edu.escuelaing.arem.ASE.app.model;

public class Movie {
    private static int counter = 1;
    private final int id;
    private String title;
    private String director;
    private String year;

    public Movie(String title, String director, String year) {
        this.id = counter++;
        this.title = title;
        this.director = director;
        this.year = year;
    }

    public void update(String title, String director, String year) {
        if (title != null) this.title = title;
        if (director != null) this.director = director;
        if (year != null) this.year = year;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public String getYear() { return year; }

    public void setTitle(String title) { this.title = title; }
    public void setDirector(String director) { this.director = director; }
    public void setYear(String year) { this.year = year; }
}
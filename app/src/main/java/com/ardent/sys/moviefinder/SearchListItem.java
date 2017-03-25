package com.ardent.sys.moviefinder;

/**
 * Created by sagar on 24-03-2017.
 */

public class SearchListItem {

    private String title;
    private String posterUrl;
    private String releaseYear;
    private String type;
    private String imdbId;
    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseDate) {
        this.releaseYear = releaseDate;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}

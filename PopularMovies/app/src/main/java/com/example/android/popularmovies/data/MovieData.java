package com.example.android.popularmovies.data;

import com.example.android.popularmovies.networkutils.UrlEndpoints;

/**
 * Created by PrajktiK on Oct/26/2017.
 */

public final class MovieData{

    private int mMovieId;
    private String mTitle;
    private String mReleaseDate;
    private int mDuration;
    private double mUserRating;
    private String mOverview;
    private String mThumbnailUrl;

    public MovieData(int id, String title, String posterPath){
        this.mMovieId = id;
        this.mTitle = title;
        this.mThumbnailUrl = createCompleteUrl(posterPath);
    }

    public MovieData(int id, String title, String releaseDate, int duration, double userRating, String overview, String thumbnailUrl){
        this.mMovieId = id;
        this.mTitle = title;
        this.mReleaseDate = releaseDate;
        this.mDuration = duration;
        this.mUserRating = userRating;
        this.mOverview = overview;
        this.mThumbnailUrl = createCompleteUrl(thumbnailUrl);
    }

    private String createCompleteUrl(String thumbnailUrl){
       return UrlEndpoints.IMAGE_BASE_URL +
               UrlEndpoints.IMAGE_SIZE +
               thumbnailUrl;
    }

    int getMovieId(){
        return mMovieId;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getReleaseDate(){
        return mReleaseDate;
    }

    public int getDuration(){
        return mDuration;
    }

    public double getUserRating(){
        return mUserRating;
    }

    public String getOverview(){
        return mOverview;
    }

    public String getThumbnailUrl(){
        return mThumbnailUrl;
    }

    @Override
    public String toString(){
        return "MovieData{" +
                "mMovieId=" + mMovieId +
                ", mTitle='" + mTitle + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mDuration=" + mDuration +
                ", mUserRating=" + mUserRating +
                ", mOverview='" + mOverview + '\'' +
                ", mThumbnailUrl='" + mThumbnailUrl + '\'' +
                "}\n";
    }
}

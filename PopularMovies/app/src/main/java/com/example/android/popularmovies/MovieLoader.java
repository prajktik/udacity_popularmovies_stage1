package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmovies.data.MovieData;
import com.example.android.popularmovies.networkutils.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by PrajktiK on Oct/26/2017.
 */

public final class MovieLoader extends AsyncTaskLoader<ArrayList<MovieData>>{

    private String mQuery;
    private int mQueryType;
    private int mMovieId;

    private int QUERY_TYPE_MOVIES = 0;
    private int QUERY_TYPE_MOVIE_DETAILS = 1;

    MovieLoader(Context context, String query){
        super(context);
        mQuery = query;
        mQueryType = QUERY_TYPE_MOVIES;
    }

    MovieLoader(Context context, int id){
        super(context);
        mMovieId = id;
        mQueryType = QUERY_TYPE_MOVIE_DETAILS;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();

    }

    @Override
    public ArrayList<MovieData> loadInBackground(){

        if(mQueryType == QUERY_TYPE_MOVIES){
            return NetworkUtils.loadMovieData(mQuery);
        }else{
            return NetworkUtils.loadMovieData(mMovieId);
        }
    }
}

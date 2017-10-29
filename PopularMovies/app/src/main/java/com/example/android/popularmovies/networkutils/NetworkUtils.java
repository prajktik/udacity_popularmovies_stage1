package com.example.android.popularmovies.networkutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.data.MovieData;
import com.example.android.popularmovies.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by PrajktiK on Oct/26/2017.
 */

public class NetworkUtils{

    private static final boolean DEBUG = LogUtil.DEBUG;
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 15000;
    private static final int RESPONSE_SUCCESS = 200;

    private static final String KEY_RESULTS_ARRAY = "results"; //Array
    private static final String KEY_MOVIE_ID = "id"; //int
    private static final String KEY_RATING = "vote_average"; //double
    private static final String KEY_ORIGINAL_TITLE = "original_title"; //String
    private static final String KEY_POSTER_PATH = "poster_path"; //String
    private static final String KEY_OVERVIEW = "overview"; //String
    private static final String KEY_RELEASE_DATE = "release_date"; //String
    private static final String KEY_RUNTIME = "runtime"; //integer


    public static ArrayList<MovieData> loadMovieData(String query){

        URL url = buildUrl(query);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            LogUtil.error(TAG, "Error closing input stream\n" + e);
        }

        return extractMovies(jsonResponse);

    }

    private static URL buildUrl(String sortQuery){

        Uri builtURI = Uri.parse(UrlEndpoints.MOVIES_BASE_URL).buildUpon()
                .appendPath(sortQuery)
                .appendQueryParameter(UrlEndpoints.API_KEY_PARAM, UrlEndpoints.API_KEY)
                .build();

        URL url = null;
        try{
            url = new URL(builtURI.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }

        LogUtil.verbose(TAG, "BuiltURL: \n" + url);
        return url;
    }

    public static ArrayList<MovieData> loadMovieData(int id){

        URL url = buildUrl(id);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            LogUtil.error(TAG, "Error closing input stream\n" + e);
        }

        return extractMovies(jsonResponse, id);
    }

    private static URL buildUrl(int id){

        Uri builtURI = Uri.parse(UrlEndpoints.MOVIES_BASE_URL).buildUpon()
                .appendPath(String.valueOf(id))
                .appendQueryParameter(UrlEndpoints.API_KEY_PARAM, UrlEndpoints.API_KEY)
                .build();

        URL url = null;
        try{
            url = new URL(builtURI.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }

        LogUtil.verbose(TAG, "BuiltURL: \n" + url);
        return url;
    }


    public static boolean checkNetworkState(Context context){

        boolean isConnected = false;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null){
            LogUtil.debug(TAG, "CONNECTIVITY_SERVICE not available. ");
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()){
            isConnected = true;
        }

        return isConnected;
    }

    private static String makeHttpRequest(URL url) throws IOException{

        String jsonResponse = "";

        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(CONNECT_TIMEOUT);
            urlConnection.setConnectTimeout(READ_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == RESPONSE_SUCCESS){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch(IOException e){
            Log.e(TAG, "Problem retrieving the movie data.", e);
        }finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset
                    .forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<MovieData> extractMovies(String jSonResponse){

        ArrayList<MovieData> movies = new ArrayList<>();

        try{

            JSONObject mainObj = new JSONObject(jSonResponse);
            if(!mainObj.has(KEY_RESULTS_ARRAY)){
                LogUtil.error(TAG, "Query returned no results!");
                return null;
            }
            JSONArray resultsArray = mainObj.getJSONArray(KEY_RESULTS_ARRAY);
            JSONObject result;
            int id = -1;
            String title = null;
            String posterPath = null;

            MovieData movie;

            for(int i = 0; i < resultsArray.length(); i++){

                result = resultsArray.getJSONObject(i);

                if(result.has(KEY_MOVIE_ID)){
                    id = result.getInt(KEY_MOVIE_ID);
                }

                if(result.has(KEY_ORIGINAL_TITLE)){
                    title = result.getString(KEY_ORIGINAL_TITLE);
                }

                if(result.has(KEY_POSTER_PATH)){
                    posterPath = result.getString(KEY_POSTER_PATH);
                }

                movie = new MovieData(id, title, posterPath);
                movies.add(movie);
            }

        }catch(JSONException e){
            LogUtil.error(TAG, "Problem parsing the earthquake JSON results\n" + e);
        }
        return movies;
    }

    private static ArrayList<MovieData> extractMovies(String jSonResponse, int id){

        ArrayList<MovieData> movies = new ArrayList<>();

        try{

            JSONObject result = new JSONObject(jSonResponse);
            int movieId = -1;
            double rating = 0;
            String title = null;
            String posterPath = null;
            String overview = null;
            String releaseDate = null;
            int duration = 0;

            MovieData data;
            if(result.has(KEY_MOVIE_ID)){
                movieId = result.getInt(KEY_MOVIE_ID);
            }

            if(result.has(KEY_ORIGINAL_TITLE)){
                title = result.getString(KEY_ORIGINAL_TITLE);
            }

            if(result.has(KEY_POSTER_PATH)){
                posterPath = result.getString(KEY_POSTER_PATH);
            }

            if(result.has(KEY_RATING)){
                rating = result.getDouble(KEY_RATING);
            }

            if(result.has(KEY_OVERVIEW)){
                overview = result.getString(KEY_OVERVIEW);
            }

            if(result.has(KEY_RELEASE_DATE)){
                releaseDate = result.getString(KEY_RELEASE_DATE);
            }

            if(result.has(KEY_RUNTIME)){
                duration = result.getInt(KEY_RUNTIME);
            }

            data = new MovieData(movieId, title, releaseDate, duration, rating, overview,
                    posterPath);
            movies.add(data);

        }catch(JSONException e){
            LogUtil.error(TAG, "Problem parsing the earthquake JSON results\n" + e);
        }
        return movies;
    }
}
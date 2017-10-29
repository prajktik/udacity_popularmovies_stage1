package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieAdapter;
import com.example.android.popularmovies.data.MovieData;
import com.example.android.popularmovies.networkutils.NetworkUtils;
import com.example.android.popularmovies.networkutils.UrlEndpoints;
import com.example.android.popularmovies.utils.AppConstants;
import com.example.android.popularmovies.utils.LogUtil;

import java.util.ArrayList;

public class MoviesMainActivity extends AppCompatActivity implements MovieAdapter
        .OnItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<MovieData>>{

    private static final boolean DEBUG = LogUtil.DEBUG;
    private static final String TAG = MoviesMainActivity.class.getSimpleName();

    private static final int NO_OF_COLUMNS = 2;

    private static int mSortOrder;
    private static final int SORT_BY_POPULARITY = 100;
    private static final int SORT_BY_RATINGS = 200;

    private static final String BUNDLE_KEY = "prev_key";

    private MovieAdapter mAdapter;
    private TextView mEmptyView;
    private ProgressBar mLoadingIndicator;

    private String mQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            mQuery = savedInstanceState.getString(BUNDLE_KEY);
        }

        boolean mIsSavedState;
        if(mQuery == null || mQuery.isEmpty()){
            mQuery = UrlEndpoints.SORT_BY_POPULARITY; //Set default sort order as popularity
            mIsSavedState = false;
        }else{
            mIsSavedState = true;
        }

        RecyclerView mGridView = findViewById(R.id.rv_grid_view);
        mEmptyView = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        ArrayList<MovieData> movies = new ArrayList<>();
        mAdapter = new MovieAdapter(MoviesMainActivity.this, movies, this);

        GridLayoutManager layoutManager = new GridLayoutManager(MoviesMainActivity.this,
                NO_OF_COLUMNS);
        mGridView.setLayoutManager(layoutManager);
        mGridView.setAdapter(mAdapter);
        mGridView.setHasFixedSize(true);

        loadMovies(!mIsSavedState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY, mQuery);
    }

    @Override
    public void onItemClick(int id){

        Intent intent = new Intent(MoviesMainActivity.this, MovieDetailsActivity.class);
        intent.putExtra(AppConstants.INTENT_ID_EXTRA, id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        switch(mSortOrder){
            case SORT_BY_POPULARITY:
                menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
                return true;
            case SORT_BY_RATINGS:
                menu.findItem(R.id.action_sort_by_rating).setChecked(true);
                return true;
            default:
                break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch(id){

            case R.id.action_sort_by_popularity:
                mSortOrder = SORT_BY_POPULARITY;
                mQuery = UrlEndpoints.SORT_BY_POPULARITY;
                invalidateOptionsMenu();
                loadMovies(true);
                return true;

            case R.id.action_sort_by_rating:
                mSortOrder = SORT_BY_RATINGS;
                mQuery = UrlEndpoints.SORT_BY_RATING;
                invalidateOptionsMenu();
                loadMovies(true);

                return true;

            case R.id.action_refresh:
                mAdapter.setMoviesData(null);
                loadMovies(true);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovies(boolean isNewSearch){

        if(NetworkUtils.checkNetworkState(MoviesMainActivity.this)){
            LoaderManager loaderManager = getLoaderManager();
            if(isNewSearch){
                loaderManager.restartLoader(AppConstants.MOVIE_LOADER_ID, null,
                        MoviesMainActivity.this);
            }else{
                loaderManager.initLoader(AppConstants.MOVIE_LOADER_ID, null, MoviesMainActivity
                        .this);
            }
        }else{
            mLoadingIndicator.setVisibility(View.GONE);
            mAdapter.setMoviesData(null);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.no_internet_connection);
        }
    }


    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int i, Bundle bundle){

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        return new MovieLoader(this, mQuery);

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieData>> loader, ArrayList<MovieData> movieData){

        mLoadingIndicator.setVisibility(View.GONE);
        if(!movieData.isEmpty()){
            mAdapter.setMoviesData(movieData);
        }else{
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.error_message);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieData>> loader){

        mAdapter.setMoviesData(null);
    }

}

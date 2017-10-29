package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieData;
import com.example.android.popularmovies.networkutils.NetworkUtils;
import com.example.android.popularmovies.utils.AppConstants;
import com.example.android.popularmovies.utils.LogUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<ArrayList<MovieData>>{


    private static final boolean DEBUG = LogUtil.DEBUG;
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private static final String BUNDLE_KEY = "prev_id";

    private TextView mEmptyView;
    private ProgressBar mLoadingIndicator;

    private RelativeLayout mLayout;
    private ImageView mMovieThumbnailView;
    private TextView mMovieTitleView;
    private TextView mMovieReleaseDateView;
    private TextView mMovieDurationView;
    private TextView mMovieRatingView;
    private TextView mMovieOverview;

    private int mMovieId;

    private int mImageHeight;
    private int mImageWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if(savedInstanceState != null){
            mMovieId = savedInstanceState.getInt(BUNDLE_KEY, 0);
        }

        boolean mIsSavedState = mMovieId > 0;

        mLayout = findViewById(R.id.details_container);
        mMovieThumbnailView = findViewById(R.id.iv_details_movie_thumbnail);
        mMovieTitleView = findViewById(R.id.tv_details_movie_title);
        mMovieReleaseDateView = findViewById(R.id.tv_details_movie_release_date);
        mMovieDurationView = findViewById(R.id.tv_details_movie_duration);
        mMovieRatingView = findViewById(R.id.tv_details_movie_rating);
        mMovieOverview = findViewById(R.id.tv_details_movie_overview);

        mEmptyView = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        float density = getResources().getDisplayMetrics().density;
        mImageHeight = (int) (getResources().getDimension(R.dimen.details_image_height)
                / density);
        mImageWidth = (int) (getResources().getDimension(R.dimen.details_image_width) /
                density);

        if(mIsSavedState){
            loadMovies(false);
        }else{
            Intent intent = getIntent();
            if(intent.hasExtra(AppConstants.INTENT_ID_EXTRA)){
                mMovieId = intent.getIntExtra(AppConstants.INTENT_ID_EXTRA, 0);
                if(mMovieId > 0){
                    loadMovies(true);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY, mMovieId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch(id){

            case R.id.action_refresh:
                loadMovies(true);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadMovies(boolean isNewSearch){

        if(NetworkUtils.checkNetworkState(MovieDetailsActivity.this)){
            LoaderManager loaderManager = getLoaderManager();
            if(isNewSearch){
                loaderManager.restartLoader(AppConstants.MOVIE_LOADER_ID, null,
                        MovieDetailsActivity.this);
            }else{
                loaderManager.initLoader(AppConstants.MOVIE_LOADER_ID, null,
                        MovieDetailsActivity.this);
            }
        }else{
            mLoadingIndicator.setVisibility(View.GONE);
            mLayout.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int i, Bundle bundle){
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        return new MovieLoader(MovieDetailsActivity.this, mMovieId);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieData>> loader, ArrayList<MovieData> movieData){

        mLoadingIndicator.setVisibility(View.GONE);

        if(movieData != null && movieData.size() > 0){
            MovieData data = movieData.get(0);

            String imageUrl = data.getThumbnailUrl();
            Picasso.with(MovieDetailsActivity.this).load(imageUrl).resize(mImageWidth,
                    mImageHeight)
                    .error(R.drawable.user_placeholder_error)
                    .placeholder(R.drawable.user_placeholder)
                    .into(mMovieThumbnailView);

            mMovieTitleView.setText(data.getTitle());
            mMovieReleaseDateView.setText(data.getReleaseDate());

            String durationText = getString(R.string.duration_text, data.getDuration());
            mMovieDurationView.setText(durationText);

            String ratingText = getString(R.string.user_ratings_text,data.getUserRating());
            mMovieRatingView.setText(ratingText);
            mMovieOverview.setText(data.getOverview());
        }else{
            mLayout.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.error_message);
        }


    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieData>> loader){
            resetViews();
    }

    private void resetViews(){
      mLayout.setVisibility(View.INVISIBLE);
    }
}

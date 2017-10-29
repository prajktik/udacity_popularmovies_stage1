package com.example.android.popularmovies.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.utils.LogUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PrajktiK on Oct/26/2017.
 */

public final class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private static final boolean DEBUG = LogUtil.DEBUG;
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final int mImageHeight;
    private final int mImageWidth;

    private ArrayList<MovieData> mMovieData;
    private final Context mContext;

    private final OnItemClickListener mOnClickListener;

    public void setMoviesData(ArrayList<MovieData> movieData){
        mMovieData.clear();
        if(movieData != null && movieData.size() > 0){
            mMovieData.addAll(movieData);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(int id);
    }

    public MovieAdapter(Context context, ArrayList<MovieData> moviesList, OnItemClickListener
            listener){

        mContext = context;
        mOnClickListener = listener;
        mMovieData = moviesList;
        float density = mContext.getResources().getDisplayMetrics().density;
        mImageHeight = (int) (mContext.getResources().getDimension(R.dimen.grid_image_height)
                / density);
        mImageWidth = (int) (mContext.getResources().getDimension(R.dimen.grid_image_width) /
                density);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent,
                false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position){

        holder.bind(position);

    }

    @Override
    public int getItemCount(){

        if(mMovieData == null){
            return 0;
        }else{
            return mMovieData.size();
        }
    }

    final class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mThumbnailView;

        MovieViewHolder(View itemView){
            super(itemView);
            mThumbnailView = itemView.findViewById(R.id.iv_movie_thumbnail);
            itemView.setOnClickListener(this);
        }

        void bind(int position){

            MovieData data = mMovieData.get(position);
            String url = data.getThumbnailUrl();
            Picasso.with(mContext).load(url).resize(mImageWidth, mImageHeight)
                    .error(R.drawable.user_placeholder_error)
                    .placeholder(R.drawable.user_placeholder)
                    .into(mThumbnailView);
        }

        @Override
        public void onClick(View view){
            MovieData data = mMovieData.get(getAdapterPosition());
            int id = data.getMovieId();
            mOnClickListener.onItemClick(id);

        }
    }
}

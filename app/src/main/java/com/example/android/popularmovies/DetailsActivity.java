package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.databinding.ActivityDetailsBinding;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.util.Api;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.text.DecimalFormat;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout = binding.collapsingToolbar;
        mCollapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.primary_dark));
        mCollapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white_text));
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white_text));

        Movie mMovie = null;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e(TAG, "No extras were passed.");
        } else {
            mMovie = getIntent().getParcelableExtra("movie");

            String posterSize;
            String backdropSize;

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                posterSize = Api.PosterSize.w342;
                backdropSize = Api.BackdropSize.w780;
            } else {
                posterSize = Api.PosterSize.w185;
                backdropSize = Api.BackdropSize.w300;
            }

            mCollapsingToolbarLayout.setTitle(mMovie.title);

            String posterUrl = Api.fullImageUrl(mMovie.posterPath, posterSize);
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.ivPoster);
            String backdropUrl = Api.fullImageUrl(mMovie.backdropPath, backdropSize);
            Glide.with(this)
                    .load(backdropUrl)
                    .placeholder(R.drawable.placeholder_backdrop)
                    .into(binding.ivBackdrop);

            binding.tvYear.setText(mMovie.year());
            binding.tvVotes.setText(
                    "Rating: "
                            + new DecimalFormat("#0.0").format(mMovie.voteAverage)
                            + " (" + mMovie.voteCount.toString() + " votes)"
            );
            binding.tvOverview.setText(mMovie.overview);

            Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, mMovie.id);
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            boolean isFavorite = false;
            if (cursor != null && cursor.moveToFirst()) {
                isFavorite = (cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITE)) == 1);
            }

            binding.toggleFavortie.setChecked(isFavorite);
            binding.toggleFavortie.setOnCheckedChangeListener(new FavoriteToggleListener(mMovie));

        }
    }

    private class FavoriteToggleListener implements CompoundButton.OnCheckedChangeListener {

        private Movie movie;

        public FavoriteToggleListener(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry._ID, movie.id);
                values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.title);
                values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
                values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
                values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.posterPath);
                values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.backdropPath);
                values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
                values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.voteCount);
                values.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
                getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            } else {
                getContentResolver().delete(
                        ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movie.id),
                        null,
                        null);
            }
            getContentResolver().notifyChange(MovieContract.MovieEntry.CONTENT_URI, null);
        }
    }
}

package com.alexvit.android.popularmovies.moviedetails;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alexvit.android.popularmovies.R;
import com.alexvit.android.popularmovies.base.BaseActivity;
import com.alexvit.android.popularmovies.data.models.Movie;
import com.alexvit.android.popularmovies.data.models.Review;
import com.alexvit.android.popularmovies.data.models.Video;
import com.alexvit.android.popularmovies.utils.Analytics;
import com.alexvit.android.popularmovies.utils.Movies;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alexvit.android.popularmovies.utils.Movies.yearOfMovie;

public class DetailsActivity extends BaseActivity<DetailsViewModel>
        implements DetailsNavigator {

    public static final String TAG_MOVIE_ID = "TAG_MOVIE_ID";

    private static final String TAG = DetailsActivity.class.getSimpleName();

    @BindView(R.id.app_bar)
    AppBarLayout incAppBar;
    @BindView(R.id.body)
    View incBody;

    @Inject
    DetailsViewModel viewModel;

    private AppBar mAppBar;
    private Body mBody;

    private MovieExtrasAdapter mExtrasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        mAppBar = new AppBar();
        ButterKnife.bind(mAppBar, incAppBar);
        mBody = new Body();
        ButterKnife.bind(mBody, incBody);


        long movieId = getMovieId();
        if (movieId == -1) {
            Log.e(TAG, "No movie ID was passed.");
            return;
        }

        mExtrasAdapter = new MovieExtrasAdapter(mBody.reviewList, mBody.videoList);

        getComponent().inject(this);

        viewModel.setNavigator(this);
        viewModel.onMovieId(movieId);
    }

    @Override
    protected DetailsViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onMovieLoaded(Movie movie) {
        initToolbar(movie.title);
        loadImages(movie);
        loadText(movie);
        setupFavorite(movie);

        Analytics.logMovieView(this, movie);
    }

    @Override
    public void onReviewsLoaded(List<Review> reviews) {
        mExtrasAdapter.setReviews(reviews);
    }

    @Override
    public void onVideosLoaded(List<Video> videos) {
        mExtrasAdapter.setVideos(videos);
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    private long getMovieId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return getIntent().getLongExtra(TAG_MOVIE_ID, -1);
        }
        return -1;
    }

    private void initToolbar(String title) {
        setSupportActionBar(mAppBar.toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppBar.collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.primary_dark));
        mAppBar.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white_text));
        mAppBar.collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white_text));
        mAppBar.collapsingToolbar.setTitle(title);
    }

    private void loadImages(Movie movie) {
        String posterSize = Movies.PosterSize.w185;
        String backdropSize;
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            backdropSize = Movies.BackdropSize.w780;
        } else {
            backdropSize = Movies.BackdropSize.w300;
        }

        String posterUrl = Movies.fullImageUrl(movie.posterPath, posterSize);
        Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder)
                .into(mBody.ivPoster);

        String backdropUrl = Movies.fullImageUrl(movie.backdropPath, backdropSize);
        Glide.with(this)
                .load(backdropUrl)
                .placeholder(R.drawable.placeholder_backdrop)
                .into(mAppBar.ivBackdrop);
    }

    private void loadText(Movie movie) {
        mBody.tvYear.setText(yearOfMovie(movie));
        mBody.tvVotes.setText(
                "Rating: "
                        + new DecimalFormat("#0.0").format(movie.voteAverage)
                        + " (" + movie.voteCount.toString() + " votes)"
        );
        mBody.tvOverview.setText(movie.overview);
    }

    private void setupFavorite(Movie movie) {
        mBody.toggleFavorite.setOnCheckedChangeListener(null);
        mBody.toggleFavorite.setChecked(
                (movie.favorite == null) ? false : movie.favorite
        );
        mBody.toggleFavorite.setOnCheckedChangeListener(new FavoriteToggleListener(movie));
    }

    private class FavoriteToggleListener implements CompoundButton.OnCheckedChangeListener {

        private final Movie movie;

        FavoriteToggleListener(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // TODO: Favorite movie
//                toast(DetailsActivity.this, "Favorite " + movie.title);
                viewModel.onFavorite(movie, true);
            } else {
                // TODO: Un-favorite movie
//                toast(DetailsActivity.this, "Un-favorite " + movie.title);
                viewModel.onFavorite(movie, false);
            }
            // TODO: Notify change, anyone?
        }
    }

    static class AppBar {
        @BindView(R.id.collapsing_toolbar)
        CollapsingToolbarLayout collapsingToolbar;
        @BindView(R.id.iv_backdrop)
        ImageView ivBackdrop;
        @BindView(R.id.toolbar)
        Toolbar toolbar;
    }

    static class Body {
        @BindView(R.id.iv_poster)
        ImageView ivPoster;
        @BindView(R.id.tv_year)
        TextView tvYear;
        @BindView(R.id.tv_votes)
        TextView tvVotes;
        @BindView(R.id.tv_overview)
        TextView tvOverview;
        @BindView(R.id.video_list)
        FlexboxLayout videoList;
        @BindView(R.id.toggle_favortie)
        ToggleButton toggleFavorite;
        @BindView(R.id.review_list)
        LinearLayout reviewList;
    }
}

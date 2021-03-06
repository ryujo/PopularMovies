package com.alexvit.android.popularmovies.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alexvit.android.popularmovies.R;
import com.alexvit.android.popularmovies.base.BaseActivity;
import com.alexvit.android.popularmovies.data.models.Movie;
import com.alexvit.android.popularmovies.moviedetails.DetailsActivity;
import com.alexvit.android.popularmovies.settings.SettingsActivity;
import com.alexvit.android.popularmovies.utils.Analytics;
import com.alexvit.android.popularmovies.utils.Prefs;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends BaseActivity<ListViewModel>
        implements ListNavigator,
        MovieGridAdapter.MovieClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @SuppressWarnings("unused")
    private static final String TAG = ListActivity.class.getSimpleName();

    @BindView(R.id.movie_grid)
    RecyclerView mMovieGridRecyclerView;

    private MovieGridAdapter mAdapter;
    private String mCategory;

    @Inject
    SharedPreferences mSharedPreferences;
    @Inject
    ListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getComponent().inject(this);

        viewModel.setNavigator(this);

        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mCategory = Prefs.getCategory(this);
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAdapter.getItemCount() == 0) {
            // Reload if there's nothing to show (orientation changed)
            // Always reload favorites, so if you open a favorite, unfavorite it and go back, it disappears
            viewModel.onCategoryChanged(mCategory);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected ListViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onMovieClicked(Movie movie) {
        launchDetails(movie.id);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_category_key))) {
            String newSortBy = Prefs.getCategory(this);
            if (!newSortBy.equals(mCategory)) {
                // Sorting changed, should reload data.
                mCategory = newSortBy;
                viewModel.onCategoryChanged(mCategory);
            }
        }
    }

    @Override
    public void onMoviesLoaded(List<Movie> movies) {

        CharSequence title = Prefs.getCategoryTitle(this, mCategory);
        setTitle(title);

        mAdapter.deleteMovies();
        mAdapter.setMovies(movies);

        Analytics.logCategoryView(this, mCategory);
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    private void initRecyclerView() {
        final int nColumns = getResources().getInteger(R.integer.grid_column_count);
        mMovieGridRecyclerView.setLayoutManager(new GridLayoutManager(this, nColumns));

        mAdapter = new MovieGridAdapter(this);
        mMovieGridRecyclerView.setAdapter(mAdapter);

        mMovieGridRecyclerView.setHasFixedSize(true);
    }

    private void launchDetails(long movieId) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.TAG_MOVIE_ID, movieId);
        startActivity(intent);
    }
}
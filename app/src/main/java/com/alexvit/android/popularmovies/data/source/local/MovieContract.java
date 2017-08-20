package com.alexvit.android.popularmovies.data.source.local;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Aleksandrs Vitjukovs on 7/15/2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.alexvit.android.popularmovies";
    public static final String PATH_MOVIES = "movies";
    private static final String SCHEME = "content://";
    private static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_IS_FAVORITE = "isFavorite";
        public static final String TABLE_NAME = "movies";

    }

}
package com.alexvit.android.popularmovies.moviedetails;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alexvit.android.popularmovies.R;
import com.alexvit.android.popularmovies.data.models.Review;
import com.alexvit.android.popularmovies.data.models.Video;
import com.alexvit.android.popularmovies.utils.Movies;

import java.util.List;

/**
 * Created by Aleksandrs Vitjukovs on 7/20/2017.
 */

class MovieExtrasAdapter {

    private final ViewGroup reviewParent;
    private final ViewGroup videoParent;

    MovieExtrasAdapter(ViewGroup reviewParent, ViewGroup videoParent) {
        this.reviewParent = reviewParent;
        this.videoParent = videoParent;
    }

    void setReviews(List<Review> reviews) {
        reviewParent.removeAllViews();
        if (reviews != null) {
            reviews.forEach(this::addReview);
        }
    }

    void setVideos(List<Video> videos) {
        videoParent.removeAllViews();
        if (videos != null) {
            videos.forEach(this::addVideo);
        }
    }

    private void addReview(Review review) {
        LayoutInflater inflater = LayoutInflater.from(reviewParent.getContext());
        View itemView = inflater.inflate(R.layout.review_item, reviewParent, false);
        ((TextView) itemView.findViewById(R.id.review_author)).setText(review.author);
        ((TextView) itemView.findViewById(R.id.review_content)).setText(review.content);
        reviewParent.addView(itemView);
    }

    private void addVideo(final Video video) {
        if (!Movies.isSupportedVideoSite(video.site)) return;
        final Uri videoUri = Movies.getVideoUri(video.site, video.key);

        LayoutInflater inflater = LayoutInflater.from(videoParent.getContext());
        View itemView = inflater.inflate(R.layout.video_item, videoParent, false);
        ((TextView) itemView.findViewById(R.id.video_name)).setText(video.name);

        Button button = (Button) itemView;
        button.setOnClickListener(view -> view.getContext().startActivity(
                new Intent(Intent.ACTION_VIEW, videoUri)));

        videoParent.addView(itemView);
    }
}

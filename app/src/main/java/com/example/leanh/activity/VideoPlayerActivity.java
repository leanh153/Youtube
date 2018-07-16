package com.example.leanh.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.leanh.model.Video;
import com.example.leanh.ultil.Constant;
import com.example.leanh.view.ToastCustom;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * this class to play video from youtube use youtube api v3
 */
public class VideoPlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    /*Youtube player view to play youtube video*/
    private YouTubePlayerView mPlayerView;

    /*display video title*/
    private TextView mTitle;

    /*display video Description*/
    private TextView mDescription;

    /*Video object*/
    private Video video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        initView();

    }

    /**
     * initialize view for this activity
     */
    private void initView() {
        mTitle = findViewById(R.id.play_title);
        mDescription = findViewById(R.id.play_description);
        video = (Video) getIntent().getSerializableExtra("video");
        mPlayerView = findViewById(R.id.youtube_player_view);
        // this developer key require to play video from youtube
        mPlayerView.initialize(Constant.DEVELOPER_KEY, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check orientation state and set view flag
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPlayerView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setDisplay();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            // if success play video with the video's id
            youTubePlayer.cueVideo(video.getVideoId());
            setDisplay();
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            // on false show dialog request.
            youTubeInitializationResult.getErrorDialog(this, Constant.RECOVERY_DIALOG_REQUEST).show();
        } else {
            ToastCustom.Toast(this, getLayoutInflater(), youTubeInitializationResult.toString());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPlayerView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setDisplay();
        }
    }

    /**
     * set video information
     */
    private void setDisplay() {
        mTitle.setText(video.getTitle());
        mDescription.setText(video.getDescription());
    }

}
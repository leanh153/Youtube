package com.example.leanh.activity;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.leanh.Sqlite.SQLiteHelper;
import com.example.leanh.connector.GoogleConnector;
import com.example.leanh.connector.YouTubeConnector;
import com.example.leanh.model.Video;
import com.example.leanh.view.ToastCustom;
import com.example.leanh.view.VideoAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

/**
 * this class is main activity show all video searched from youtube, max result is 10
 */
public class HomeActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, MaterialSearchView.OnQueryTextListener {

    /*this handle video change*/
    private Handler mHandler;

    /*this is toolBar to set mToolBar title*/
    private Toolbar mToolBar;

    /*this help get video history by mUserName and display on mToolBar welcome*/
    private String mUserName;

    /*get user's video watching history by mEmail*/
    private ListView mListView;
    private String mEmail;

    /*true get user's video watching history by mEmail, otherwise by user*/
    private boolean mSignInByGoogle;

    /*this is hold searching's mKeyWord*/
    private String mKeyWord;

    /*display video's information by VideoAdapter class*/
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mListView = findViewById(R.id.list_view);
        setToolbar();
        mHandler = new Handler();
        setKeyWord("funix");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MaterialSearchView searchView = findViewById(R.id.search_view);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.setHint(getString(R.string.hint_search));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        // onBackPressed search video by mKeyWord
        setKeyWord(getKeyWord());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_history:
                // get user's video watching history form database
                SQLiteHelper helper = SQLiteHelper.getInstance(this);
                List<Video> videoList;
                if (mSignInByGoogle) {
                    // get by mEmail if user sign in with google sign in button
                    videoList = helper.getVideosByUser(mEmail);
                } else {
                    // get by mUserName if user sign in with mUserName
                    videoList = helper.getVideosByUser(mUserName);
                }

                notifyChange(videoList);
                break;
            case R.id.action_log_out:
                // log out by mEmail GoogleSignInClient.logOut()
                if (mSignInByGoogle) {
                    GoogleSignInClient connector = new GoogleConnector(this).getGoogleSignInClient();
                    connector.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI();
                        }
                    });

                } else {
                    // start sign in activity
                    updateUI();
                }
                break;


        }
        return true;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Video video = (Video) parent.getItemAtPosition(position);
        SQLiteHelper helper = SQLiteHelper.getInstance(this);
        if (mSignInByGoogle) {
            helper.addVideo(video, mEmail);
        } else {
            helper.addVideo(video, mUserName);
        }
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra("video", video);
        startActivity(intent);
    }

    /*set mToolBar title*/
    private void setToolbar() {
        mUserName = getIntent().getStringExtra("userName");
        mToolBar.setTitle("HI " + mUserName.toUpperCase() + " WelCome to FuNIX");
        if (getIntent().getStringExtra("email") != null) {
            mEmail = getIntent().getStringExtra("email");
            mSignInByGoogle = true;
        } else {
            mSignInByGoogle = false;
        }
    }

    /*set new user interface*/
    private void updateUI() {
        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }

    /*search video from youtube by mKeyWord*/
    private void loadVideos(final String keyword) {
        new Thread() {
            public void run() {
                YouTubeConnector connector = new YouTubeConnector(HomeActivity.this);
                final List<Video> videoList = connector.search(keyword);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyChange(videoList);
                    }
                });
            }
        }.start();

    }

    /* update new videos to screen*/
    private void notifyChange(List<Video> videoList) {
        if (videoAdapter == null) {
            videoAdapter = new VideoAdapter(this, videoList);
        } else {
            videoAdapter.clear();
            videoAdapter.addAll(videoList);
        }


        videoAdapter.notifyDataSetChanged();
        mListView.setAdapter(videoAdapter);
        mListView.setScrollContainer(true);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(this);
    }

    /* get the search mKeyWord*/
    private String getKeyWord() {
        return mKeyWord;
    }

    private void setKeyWord(String mKeyWord) {
        this.mKeyWord = mKeyWord;
        if (isDeviceOnline()) {
            loadVideos(mKeyWord);
        } else {
            ToastCustom.Toast(this, getLayoutInflater(),
                    getString(R.string.error_internet));
        }
    }

    /*check device connection state*/
    private boolean isDeviceOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWorkInfor = manager.getActiveNetworkInfo();
        return (netWorkInfor != null && netWorkInfor.isConnected());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        setKeyWord(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}


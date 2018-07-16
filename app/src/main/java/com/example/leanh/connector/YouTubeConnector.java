package com.example.leanh.connector;

import android.content.Context;
import android.util.Log;

import com.example.leanh.activity.R;
import com.example.leanh.model.Video;
import com.example.leanh.ultil.Constant;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this class help connect to youtube and get one search list by keyword
 */
public class YouTubeConnector {

    private static final String TAG = YouTubeConnector.class.getSimpleName();

    private YouTube.Search.List mYouTubeList;

    public YouTubeConnector(Context context) {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {

            @Override
            public void initialize(HttpRequest request) {

            }
        }).setApplicationName(context.getString(R.string.app_name))
                .build();
        try {
            mYouTubeList = youtube.search().list("id, snippet");// set field require id, snipped only
            mYouTubeList.setKey(Constant.DEVELOPER_KEY);        // set Developer key
            mYouTubeList.setMaxResults((long) 10);      // set max result return 10
            mYouTubeList.setType("video");      // set field require video
            // set field we need to get video id, title, description, and thumbnail default size url
            mYouTubeList.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
        } catch (IOException e) {
            Log.e(TAG, "YouTubeConnector: Exception " + e);
        }


    }

    /**
     * get video list from mYouTubeList
     */
    public List<Video> search(String keywords) {
        List<Video> mListVideo = new ArrayList<>();// this hold all video information
        //set search keyword
        mYouTubeList.setQ(keywords);
        try {
            SearchListResponse response = mYouTubeList.execute();// run youtube require
            List<SearchResult> results = response.getItems();// get list search result
            // loop through all result and get set video information
            for (SearchResult result : results) {
                Video video = new Video();
                video.setTitle(result.getSnippet().getTitle());
                video.setDescription(result.getSnippet().getDescription());
                video.setThumbnail(result.getSnippet().getThumbnails().getDefault().getUrl());
                video.setVideoId(result.getId().getVideoId());
                mListVideo.add(video);
            }

        } catch (IOException e) {
            Log.e(TAG, "search: Exception " + e);
        }
        return mListVideo;
    }
}
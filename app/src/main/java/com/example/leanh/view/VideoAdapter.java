package com.example.leanh.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leanh.activity.R;
import com.example.leanh.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * this id Video adapter to display video list extends ArrayAdapter and set Video object to it
 */
public class VideoAdapter extends ArrayAdapter<Video> {
    private static final String TAG = VideoAdapter.class.getSimpleName();

    public VideoAdapter(@NonNull Context context, List<Video> videos) {
        super(context, 0, videos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get layout if not initialize
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.content_home, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.thumbnail);
        TextView title = convertView.findViewById(R.id.title);
        TextView description = convertView.findViewById(R.id.description);
        try {
            // getItem() is ArrayAdapter function to get the item by position
            Video video = getItem(position);
            // picasso api help load video thumbnail to the view
            Picasso.with(getContext().getApplicationContext()).load(video.getThumbnail()).into(imageView);
            title.setText(video.getTitle());
            description.setText(video.getDescription());
        } catch (Exception e) {
            Log.e(TAG, "getView exception " + e);
        }

        return convertView;
    }

}

package com.example.leanh.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leanh.activity.R;

/**
 * this is custom Toast for project
 */
public class ToastCustom {
    public static void Toast(Context context, LayoutInflater inflater, String toastString) {
        // get view layout
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView text = layout.findViewById(R.id.toastText);
        // set text is the toastString
        text.setText(toastString);
        Toast toast = new Toast(context);
        // gravity CENTER_VERTICAL and yOffset 1000
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 1000);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);// set layout view
        toast.show();
    }

}

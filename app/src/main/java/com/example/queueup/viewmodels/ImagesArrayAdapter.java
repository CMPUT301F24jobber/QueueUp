package com.example.queueup.viewmodels;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.queueup.R;
import com.example.queueup.models.User;

import java.util.ArrayList;

public class ImagesArrayAdapter extends ArrayAdapter<User> {
    public ImagesArrayAdapter(Context context, ArrayList<User> event) {
        super(context, 0, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.organizer_image_content,
                    parent, false);
        } else {
            view = convertView;
        }
        TextView fileName = view.findViewById(R.id.file_name);
        TextView fileSpace = view.findViewById(R.id.storage_space);
        TextView fileFormat = view.findViewById(R.id.file_format);
        ImageView fileImage = view.findViewById(R.id.file_image);
        fileName.setText("name");
        fileSpace.setText("storage");
        fileFormat.setText("format");
        fileImage.setImageResource(R.drawable.ic_nav_users);
        return view;
    }

}
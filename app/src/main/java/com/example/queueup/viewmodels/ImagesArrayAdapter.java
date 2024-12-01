package com.example.queueup.viewmodels;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.models.Image;
import com.example.queueup.models.User;
import com.example.queueup.views.admin.AdminImageFragment;

import java.util.ArrayList;


public class ImagesArrayAdapter extends ArrayAdapter<Image> {

    public ImagesArrayAdapter(Context context, ArrayList<Image> images) {
        super(context, 0, images);
    }

    /**
     * Returns the view for the ImagesArrayAdapter
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_image_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Image image = getItem(position);

        TextView fileName = view.findViewById(R.id.file_name);
        ImageView fileImage = view.findViewById(R.id.file_image);
        fileName.setText(image.getImageType());

        if (!image.getImageId().isEmpty()) {
            Glide.with(getContext()).load(image.getImageUrl()).circleCrop().into(fileImage);
        }

        return view;
    }

}

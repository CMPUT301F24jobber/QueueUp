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

/**
 * Adapter class to display a list of user-related images or file data, extending ArrayAdapter to provide
 * a custom view for each item. It populates a list item layout with placeholder information for
 * file name, storage space, file format, and an image.
 */
public class ImagesArrayAdapter extends ArrayAdapter<User> {

    /**
     * Constructs a new ImagesArrayAdapter.
     *
     * @param context the context in which the adapter is being used
     * @param event   the list of User objects to be displayed
     */
    public ImagesArrayAdapter(Context context, ArrayList<User> event) {
        super(context, 0, event);
    }

    /**
     * Inflates and populates the view for a User item at the specified position. Sets up placeholder
     * text for the file name, storage space, and format, as well as a static image.
     *
     * @param position    the position of the User item in the list
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent view group
     * @return the view representing the User item with image and placeholder text
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
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

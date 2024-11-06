package com.example.queueup.viewmodels;

import static android.app.PendingIntent.getActivity;

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
import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.example.queueup.views.admin.AdminUserFragment;

import java.util.ArrayList;

public class UsersArrayAdapter extends ArrayAdapter<User> {
    public UsersArrayAdapter(Context context, ArrayList<User> user) {
        super(context, 0, user);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_user_content,
                    parent, false);
        } else {
            view = convertView;
        }
        User user = getItem(position);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userPhone = view.findViewById(R.id.user_phone);
        TextView userEmail = view.findViewById(R.id.user_email);
        ImageView userImage = view.findViewById(R.id.user_image);
        userName.setText(user.getFirstName() + " " + user.getLastName());
        userPhone.setText(user.getPhoneNumber());
        userEmail.setText(user.getEmailAddress());
        userImage.setImageResource(R.drawable.ic_nav_users);
        view.setOnClickListener( (v) -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_activity_fragment, AdminUserFragment.class, null)
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }

}
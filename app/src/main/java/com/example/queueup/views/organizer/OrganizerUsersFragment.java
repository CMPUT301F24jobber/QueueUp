package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UsersArrayAdapter;

import java.util.ArrayList;

public class OrganizerUsersFragment extends Fragment {
    public OrganizerUsersFragment() {
        super(R.layout.organizer_users_fragment);
    }

    private ArrayList<User> dataList;
    private ListView userList;
    private UsersArrayAdapter usersAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        User exampleUser = new User("example", "example", "example", "example", "example", "example");
        dataList = new ArrayList<>();
        dataList.add(exampleUser);

        userList = getView().findViewById(R.id.organizer_user_list);
        usersAdapter = new UsersArrayAdapter(view.getContext(), dataList);
        userList.setAdapter(usersAdapter);
    }
}

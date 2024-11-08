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

/**
 * Fragment responsible for displaying a list of users with QR codes, likely for event check-ins or other organizer actions.
 * The fragment initializes a list of users and displays them in a ListView.
 */
public class OrganizerQRCodesFragment extends Fragment {
    public OrganizerQRCodesFragment() {
        super(R.layout.organizer_qrcodes_fragment);
    }

    private ArrayList<User> dataList;
    private ListView userList;
    private UsersArrayAdapter usersAdapter;

    /**
     * Called when the view is created. Initializes the list of users and binds them to the ListView via the adapter.
     * For now, a sample user is added to the list for demonstration purposes.
     *
     * @param view The view returned by onCreateView.
     * @param savedInstanceState The saved instance state if the fragment is being recreated.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        User exampleUser = new User("example", "example", "example", "example", "example", "example", false);
        dataList = new ArrayList<>();
        dataList.add(exampleUser);

        userList = getView().findViewById(R.id.organizer_qrcodes_list);
        usersAdapter = new UsersArrayAdapter(view.getContext(), dataList);
        userList.setAdapter(usersAdapter);
    }
}

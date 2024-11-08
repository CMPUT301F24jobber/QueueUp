package com.example.queueup.views.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UsersArrayAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * AdminUsersFragment is responsible for displaying the list of users in the admin section of the app.
 * It listens for real-time updates to the Firestore 'users' collection and allows the admin to view detailed information
 * about a user by clicking on their profile.
 */
public class AdminUsersFragment extends Fragment implements AdminClickUserFragment.RefreshUsersListener {

    public AdminUsersFragment() {
        super(R.layout.admin_users_fragment);
    }

    private ArrayList<User> dataList;
    private ListView userList;
    private UsersArrayAdapter usersAdapter;
    private FirebaseFirestore db;

    /**
     * Called when the fragment's view is created. This method initializes the UI elements, sets up an adapter
     * for displaying the list of users, and sets up an item click listener to navigate to the AdminUserFragment
     * for detailed information about the selected user.
     *
     * @param view The View returned by onCreateView().
     * @param savedInstanceState A Bundle containing the activity's previous state (if any).
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        dataList = new ArrayList<>();

        userList = view.findViewById(R.id.admin_user_list);
        usersAdapter = new UsersArrayAdapter(view.getContext(), dataList);
        userList.setAdapter(usersAdapter);


        userList.setOnItemClickListener((parent, view1, position, id) -> {
            User selectedUser = dataList.get(position);

            AdminClickUserFragment fragment = new AdminClickUserFragment();

            Bundle args = new Bundle();
            args.putParcelable("user", selectedUser);

            fragment.setArguments(args);

            fragment.show(getParentFragmentManager(), "AdminClickUserFragment");

        });


        listenToUsersCollection();
    }

    /**
     * Called when the fragment resumes, this method ensures that the fragment's user list is refreshed.
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshFragment();
    }

    /**
     * Method to listen for real-time changes in the Firestore 'users' collection.
     */
    private void listenToUsersCollection() {
        db.collection("users")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("AdminUsersFragment", "Listen failed.", error);
                        return;
                    }

                    if (snapshots != null) {
//                        dataList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            User user = doc.toObject(User.class);
                            dataList.add(user);
                        }
                        usersAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void refreshFragment() {
        usersAdapter.notifyDataSetChanged();
    }
}

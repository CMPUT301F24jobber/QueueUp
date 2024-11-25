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


public class AdminImagesFragment extends Fragment implements AdminClickUserFragment.RefreshUsersListener {

    public AdminImagesFragment() {
        super(R.layout.admin_users_fragment);
    }

    private ArrayList<User> dataList;
    private ListView userList;
    private UsersArrayAdapter usersAdapter;
    private FirebaseFirestore db;

    /**
     * Called when the fragment's view has been created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        dataList = new ArrayList<>();
        User e = new User("d", "d", "d", "d", "d", "d", true);

        dataList.add(e);
        userList = view.findViewById(R.id.admin_user_list);
        usersAdapter = new UsersArrayAdapter(view.getContext(), dataList);
        userList.setAdapter(usersAdapter);

        userList.setOnItemClickListener((parent, view1, position, id) -> {
            User selectedUser = dataList.get(position);

            AdminUserFragment fragment = new AdminUserFragment();

            Bundle args = new Bundle();
            args.putParcelable("user", selectedUser);

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.admin_activity_fragment, AdminUserFragment.class, args)
                    .addToBackStack("User")
                    .commit();
        });

        listenToUsersCollection();
    }

    /**
     * Method to listen for real-time changes in the Firestore 'users' collection.
     */
    private void listenToUsersCollection() {
        db.collection("users")
                .whereIn("role", new ArrayList<String>() {{ add("attendee"); add("organizer"); }})
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

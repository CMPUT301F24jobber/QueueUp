package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventArrayAdapter;

import java.time.LocalDate;
import java.util.ArrayList;

public class AdminHomeFragment extends Fragment {
    public AdminHomeFragment() {
        super(R.layout.admin_home_fragment);
    }
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Event e = new Event("id","name", "desc", "image", 1.2, 1.2, LocalDate.of(2020, 1, 8), LocalDate.of(2020, 2, 8));
        dataList = new ArrayList<Event>();
        dataList.add(e);

        eventList = getView().findViewById(R.id.admin_event_list);
        eventAdapter = new EventArrayAdapter(view.getContext(), dataList);
        eventList.setAdapter(eventAdapter);
    }

}

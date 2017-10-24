package com.maciejak.myplaces.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.adapters.MyPlacesListRecyclerViewAdapter;
import com.maciejak.myplaces.models.Place;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPlacesListFragment extends BaseFragment{

    List<Place> mPlaces;

    @BindView(R.id.my_places_list_recycler_view)
    RecyclerView mMyPlacesListRecyclerView;

    MyPlacesListRecyclerViewAdapter mMyPlacesListRecycelerViewAdapter;

    public MyPlacesListFragment() {
        // Required empty public constructor
    }

    public static MyPlacesListFragment newInstance() {
        return new MyPlacesListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_places_list, container, false);
        ButterKnife.bind(this, view);
        setupControls(view);
        return view;
    }

    private void setupControls(View view) {
        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.add_place_menu);
        configFloatingActionMenu(getContext(), floatingActionMenu);

        mPlaces = new ArrayList<>();
        mMyPlacesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void populateRecyclerView(List<Place> places){
        mMyPlacesListRecycelerViewAdapter = new MyPlacesListRecyclerViewAdapter(getActivity(), places);
        mMyPlacesListRecyclerView.setAdapter(mMyPlacesListRecycelerViewAdapter);
        mMyPlacesListRecycelerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPlaces = SQLite.select().from(Place.class).queryList();
        populateRecyclerView(mPlaces);
    }
}

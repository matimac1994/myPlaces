package com.maciejak.myplaces.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.listener.ArchiveListOnDataChangeListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repository.PlaceRepository;
import com.maciejak.myplaces.ui.adapter.ArchiveListRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArchiveListFragment extends Fragment implements ArchiveListOnDataChangeListener {

    @BindView(R.id.archive_list_recycler_view)
    RecyclerView mArchiveListRecyclerview;

    @BindView(R.id.archive_list_empty_view)
    LinearLayout mEmptyView;

    ArchiveListRecyclerViewAdapter mArchiveListRecyclerViewAdapter;
    PlaceRepository mPlaceRepository;
    List<Place> mPlaces;

    public ArchiveListFragment() {}

    public static ArchiveListFragment newInstance() {
        return new ArchiveListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive_list, container, false);
        ButterKnife.bind(this, view);
        setupControls();
        return view;
    }

    private void setupControls() {
        getActivity().setTitle(R.string.archive_of_places);
        mArchiveListRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mPlaceRepository = new PlaceRepository();
    }

    private void manageVisibility(List<Place> places){
        if (places.size() > 0){
            applyFilledView();
        }
        else {
            applyEmptyView();
        }
    }

    private void applyEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
        mArchiveListRecyclerview.setVisibility(View.GONE);
    }

    private void applyFilledView(){
        mEmptyView.setVisibility(View.GONE);
        mArchiveListRecyclerview.setVisibility(View.VISIBLE);
    }

    private void populateRecyclerView(List<Place> places){
        mArchiveListRecyclerViewAdapter = new ArchiveListRecyclerViewAdapter(places, getContext(), this);
        mArchiveListRecyclerview.setAdapter(mArchiveListRecyclerViewAdapter);
        mArchiveListRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        mPlaces = mPlaceRepository.getAllDeletedPlaces();
        manageVisibility(mPlaces);
        populateRecyclerView(mPlaces);
        super.onStart();
    }

    @Override
    public void onDataChanged() {
        manageVisibility(mPlaces);
    }
}

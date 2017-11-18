package com.maciejak.myplaces.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.listener.OnQueryTextChangeListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repository.PlaceRepository;
import com.maciejak.myplaces.ui.activity.ShowPlaceActivity;
import com.maciejak.myplaces.ui.adapter.SearchPlacesRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPlacesFragment extends BaseFragment implements View.OnClickListener, OnQueryTextChangeListener{

    @BindView(R.id.search_places_recycler_view)
    RecyclerView mSearchPlacesRecyclerView;

    SearchPlacesRecyclerViewAdapter mSearchPlacesRecyclerViewAdapter;

    List<Place> mPlaces;
    PlaceRepository mPlaceRepository;
    OnGetInstanceFragment mOnGetInstanceFragment;

    public SearchPlacesFragment() {}

    public static SearchPlacesFragment newInstance(){return new SearchPlacesFragment();}

    public interface OnGetInstanceFragment{
        void getFragmentInstance(Fragment fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_places, container, false);
        ButterKnife.bind(this, view);
        setupControls(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnGetInstanceFragment = (OnGetInstanceFragment)context;
            mOnGetInstanceFragment.getFragmentInstance(this);
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnGetInstanceFragment");
        }
    }

    public void setupControls(View view) {
        mPlaceRepository = new PlaceRepository();
        mPlaces = mPlaceRepository.getAllVisiblePlaces();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mSearchPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchPlacesRecyclerViewAdapter = new SearchPlacesRecyclerViewAdapter(getContext(), mPlaces, this);
        mSearchPlacesRecyclerView.setAdapter(mSearchPlacesRecyclerViewAdapter);
        mSearchPlacesRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mSearchPlacesRecyclerView.getChildAdapterPosition(v);
        Place place = mSearchPlacesRecyclerViewAdapter.getItem(itemPosition);
        int viewId = v.getId();
        switch (viewId){
            case R.id.row_search_places_card_view:
                Intent intent = new Intent(getContext(), ShowPlaceActivity.class);
                intent.putExtra(ShowPlaceActivity.PLACE_ID, place.getId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onQueryTextChange(String newText) {
        mSearchPlacesRecyclerViewAdapter.getFilter().filter(newText);
    }
}

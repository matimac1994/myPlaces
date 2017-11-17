package com.maciejak.myplaces.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.repository.PlaceRepository;
import com.maciejak.myplaces.ui.activity.ShowPlaceActivity;
import com.maciejak.myplaces.ui.adapter.MyPlacesListRecyclerViewAdapter;
import com.maciejak.myplaces.model.Place;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPlacesListFragment extends Fragment implements View.OnClickListener, MyPlacesListRecyclerViewAdapter.MyPlacesListOnDataChangeListener{

    List<Place> mPlaces;
    PlaceRepository mPlaceRepository;

    @BindView(R.id.my_places_list_recycler_view)
    RecyclerView mMyPlacesListRecyclerView;

    @BindView(R.id.my_places_list_empty_view)
    LinearLayout mEmptyView;

    MyPlacesListRecyclerViewAdapter mMyPlacesListRecyclerViewAdapter;

    public MyPlacesListFragment() {}

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
        getActivity().setTitle(R.string.list_of_places);

        mPlaceRepository = new PlaceRepository();
        mPlaces = new ArrayList<>();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mMyPlacesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final View layout = getActivity().findViewById(R.id.coordinatorLayout);
                final Place deletedPlace = mPlaces.get(viewHolder.getAdapterPosition());
                final int position = viewHolder.getAdapterPosition();
                Snackbar snackbar = Snackbar
                        .make(layout, getContext().getString(R.string.archived), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, view -> {
                            if (position == 0 || position == mMyPlacesListRecyclerViewAdapter.getItemCount())
                                mMyPlacesListRecyclerView.scrollToPosition(position);
                            mMyPlacesListRecyclerViewAdapter.restoreItem(deletedPlace, position);
                            mPlaceRepository.restorePlace(deletedPlace);
                        });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                mMyPlacesListRecyclerViewAdapter.removeItem(position);
                mPlaceRepository.deletePlaceSoft(deletedPlace);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mMyPlacesListRecyclerView);
    }

    private void populateRecyclerView(List<Place> places){
        mMyPlacesListRecyclerViewAdapter = new MyPlacesListRecyclerViewAdapter(getActivity(), places, this, this);
        mMyPlacesListRecyclerView.setAdapter(mMyPlacesListRecyclerViewAdapter);
        mMyPlacesListRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void applyEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
        mMyPlacesListRecyclerView.setVisibility(View.GONE);
    }

    private void applyFilledView(){
        mEmptyView.setVisibility(View.GONE);
        mMyPlacesListRecyclerView.setVisibility(View.VISIBLE);
    }

    private void manageVisibility(List<Place> places){
        if (places.isEmpty()){
            applyEmptyView();
        }
        else {
            applyFilledView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mPlaces = mPlaceRepository.getAllVisiblePlaces();
        manageVisibility(mPlaces);
        populateRecyclerView(mPlaces);
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mMyPlacesListRecyclerView.getChildAdapterPosition(v);
        Place place = mMyPlacesListRecyclerViewAdapter.getItem(itemPosition);
        int viewId = v.getId();
        switch (viewId){
            case R.id.card_view:
                Intent intent = new Intent(getContext(), ShowPlaceActivity.class);
                intent.putExtra(ShowPlaceActivity.PLACE_ID, place.getId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDataChanged() {
        manageVisibility(mPlaces);
    }
}

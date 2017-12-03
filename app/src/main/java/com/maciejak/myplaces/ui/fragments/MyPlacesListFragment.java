package com.maciejak.myplaces.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.BaseResponse;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.GetAllActivePlacesListener;
import com.maciejak.myplaces.listeners.ServerResponseListener;
import com.maciejak.myplaces.managers.PlaceListManager;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.ui.activities.ShowPlaceActivity;
import com.maciejak.myplaces.ui.adapters.MyPlacesListRecyclerViewAdapter;
import com.maciejak.myplaces.model.Place;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPlacesListFragment extends BaseFragment implements View.OnClickListener,
        MyPlacesListRecyclerViewAdapter.MyPlacesListOnDataChangeListener,
        GetAllActivePlacesListener,
        ServerResponseListener{

    List<PlaceListResponse> mPlaces = new ArrayList<>();
    PlaceRepository mPlaceRepository;
    PlaceListManager mPlaceListManager;

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
        mPlaceListManager = new PlaceListManager(mContext, this, this);
        mPlaceRepository = new PlaceRepository();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mMyPlacesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMyPlacesListRecyclerViewAdapter = new MyPlacesListRecyclerViewAdapter(getContext(), mPlaces, this, this);
        mMyPlacesListRecyclerView.setAdapter(mMyPlacesListRecyclerViewAdapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final View layout = getActivity().findViewById(R.id.coordinatorLayout);
                final PlaceListResponse deletedPlace = mPlaces.get(viewHolder.getAdapterPosition());
                final int position = viewHolder.getAdapterPosition();
                Snackbar snackbar = Snackbar
                        .make(layout, getContext().getString(R.string.archived), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, view -> {
                            if (position == 0 || position == mMyPlacesListRecyclerViewAdapter.getItemCount())
                                mMyPlacesListRecyclerView.scrollToPosition(position);
                            mMyPlacesListRecyclerViewAdapter.restoreItem(deletedPlace, position);
                        });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                mMyPlacesListRecyclerViewAdapter.removeItem(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mMyPlacesListRecyclerView);
    }

    private void applyEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
        mMyPlacesListRecyclerView.setVisibility(View.GONE);
    }

    private void applyFilledView(){
        mEmptyView.setVisibility(View.GONE);
        mMyPlacesListRecyclerView.setVisibility(View.VISIBLE);
    }

    private void manageVisibility(List<PlaceListResponse> places){
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
        mPlaceListManager.getPlaces();
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mMyPlacesListRecyclerView.getChildAdapterPosition(v);
        PlaceListResponse place = mMyPlacesListRecyclerViewAdapter.getItem(itemPosition);
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
    public void onDataChanged(List<PlaceListResponse> places) {
        manageVisibility(places);
    }

    @Override
    public void onGetAllActivePlaces(List<PlaceListResponse> places) {
        mPlaces = places;
        manageVisibility(mPlaces);
        mMyPlacesListRecyclerViewAdapter.updateList(mPlaces);
    }

    @Override
    public void onSuccessResponse(BaseResponse response) {

    }

    @Override
    public void onErrorResponse(ErrorResponse response) {

    }

    @Override
    public void onFailure(String message) {

    }
}

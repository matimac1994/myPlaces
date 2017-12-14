package com.maciejak.myplaces.ui.fragments;


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
import com.maciejak.myplaces.api.dto.response.TopPlaceResponseList;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.TopPlaces.TopPlacesFragmentManager;
import com.maciejak.myplaces.ui.activities.ShowTopPlaceActivity;
import com.maciejak.myplaces.ui.adapters.MyPlacesListRecyclerViewAdapter;
import com.maciejak.myplaces.ui.adapters.TopPlacesRecyclerViewAdapter;
import com.maciejak.myplaces.ui.dialogs.ErrorDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopPlacesFragment extends BaseFragment
        implements ServerErrorResponseListener,
        TopPlacesFragmentManager.TopPlacesFragmentManagerListener,
        View.OnClickListener{

    @BindView(R.id.top_places_recycler_view)
    RecyclerView mTopPlacesRecyclerView;

    private TopPlacesRecyclerViewAdapter mTopPlacesRecyclerViewAdapter;

    private TopPlacesFragmentManager mTopPlacesFragmentManager;
    private List<TopPlaceResponseList> mTopPlaces;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_places, container, false);
        ButterKnife.bind(this, view);
        setupControls(view);
        return view;
    }

    public TopPlacesFragment() {}

    public static TopPlacesFragment newInstance(){return new TopPlacesFragment();}

    private void setupControls(View view) {
        mTopPlaces = new ArrayList<>();
        mTopPlacesFragmentManager = new TopPlacesFragmentManager(mContext, this, this);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mTopPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTopPlacesRecyclerViewAdapter = new TopPlacesRecyclerViewAdapter(mTopPlaces, mContext, this);
        mTopPlacesRecyclerView.setAdapter(mTopPlacesRecyclerViewAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mTopPlacesFragmentManager.getTopPlaces();
    }

    @Override
    public void onErrorResponse(ErrorResponse response) {
        String message;
        if (response.getErrors() != null) {
            message = response.getErrors().get(0).getDefaultMessage();
        }else {
            message = response.getMessage();
        }
        ErrorDialog errorDialog = new ErrorDialog(mContext, message);
        errorDialog.show();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetTopPlaces(List<TopPlaceResponseList> topPlaces) {
        mTopPlacesRecyclerViewAdapter.updateList(topPlaces);
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mTopPlacesRecyclerView.getChildAdapterPosition(v);
        TopPlaceResponseList topPlace = mTopPlacesRecyclerViewAdapter.getItem(itemPosition);
        int viewId = v.getId();
        switch (viewId){
            case R.id.row_top_places_card_view:
                Intent intent = new Intent(getContext(), ShowTopPlaceActivity.class);
                intent.putExtra(ShowTopPlaceActivity.PLACE_ID, topPlace.getId());
                startActivity(intent);
                break;
        }
    }
}

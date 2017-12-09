package com.maciejak.myplaces.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.ArchiveManager;
import com.maciejak.myplaces.ui.activities.ShowPlaceActivity;
import com.maciejak.myplaces.ui.adapters.ArchiveListRecyclerViewAdapter;
import com.maciejak.myplaces.ui.dialogs.ErrorDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArchiveListFragment extends BaseFragment implements
        ServerErrorResponseListener,
        ArchiveManager.ArchiveManagerListener,
        ArchiveListRecyclerViewAdapter.ArchiveListAdapterListener,
        ActionMode.Callback{

    @BindView(R.id.archive_list_recycler_view)
    RecyclerView mArchiveListRecyclerView;

    @BindView(R.id.archive_list_empty_view)
    LinearLayout mEmptyView;

    ArchiveManager mArchiveManager;
    ArchiveListRecyclerViewAdapter mArchiveListRecyclerViewAdapter;
    List<PlaceListResponse> mPlaces = new ArrayList<>();

    ActionMode mActionMode;
    Context mContext;

    public ArchiveListFragment() {}

    public static ArchiveListFragment newInstance() {
        return new ArchiveListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        mArchiveManager = new ArchiveManager(mContext, this, this);
        mArchiveListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mArchiveListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mArchiveListRecyclerViewAdapter = new ArchiveListRecyclerViewAdapter(mPlaces, getContext(), this);
        mArchiveListRecyclerView.setAdapter(mArchiveListRecyclerViewAdapter);
    }

    private void manageVisibility(List<PlaceListResponse> places){
        if (places.size() > 0){
            applyFilledView();
        }
        else {
            applyEmptyView();
        }
    }

    private void applyEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
        mArchiveListRecyclerView.setVisibility(View.GONE);
    }

    private void applyFilledView(){
        mEmptyView.setVisibility(View.GONE);
        mArchiveListRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDataChanged(List<PlaceListResponse> places) {
        manageVisibility(places);
    }

    @Override
    public void onStart() {
        super.onStart();
        mArchiveManager.getPlaces();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.archive_list_action_menu, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.Gray));
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deletePlaces();
                return true;
            case R.id.action_restore:
                restorePlaces();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        mArchiveListRecyclerViewAdapter.clearSelections();
        mArchiveListRecyclerViewAdapter.clearIds();
        mActionMode = null;
    }

    private void restorePlaces(){
        mArchiveManager.restorePlaces(mArchiveListRecyclerViewAdapter.getPlaceIds());
    }

    private void deletePlaces(){
        mArchiveManager.deletePlaces(mArchiveListRecyclerViewAdapter.getPlaceIds());
    }


    @Override
    public void onImageClicked(int position) {
        startActionMode(position);
    }

    @Override
    public void onTitleOrDescriptionClicked(int position) {
        if (mArchiveListRecyclerViewAdapter.getSelectedItemCount() > 0){
            startActionMode(position);
        }
        else {
            PlaceListResponse place = mArchiveListRecyclerViewAdapter.getItem(position);
            Intent intent = new Intent(getContext(), ShowPlaceActivity.class);
            intent.putExtra(ShowPlaceActivity.PLACE_ID, place.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        startActionMode(position);
    }

    private void startActionMode(int position){
        if (mActionMode ==null){
            mActionMode = ((AppCompatActivity)mContext).startSupportActionMode(this);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mArchiveListRecyclerViewAdapter.toggleSelection(position);
        int count = mArchiveListRecyclerViewAdapter.getSelectedItemCount();

        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mActionMode != null)
            mActionMode.finish();
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
    public void onGetPlaces(List<PlaceListResponse> places) {
        mPlaces = places;
        manageVisibility(mPlaces);
        mArchiveListRecyclerViewAdapter.updateList(mPlaces);
    }

    @Override
    public void onDeletePlaces(Boolean isDeleted) {
        if (isDeleted){
            removePlacesFromAdapter();
            mArchiveListRecyclerViewAdapter.clearIds();
            mActionMode.finish();
            Toast.makeText(mContext, R.string.deleted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRestorePlaces(Boolean isRestored) {
        if (isRestored){
            removePlacesFromAdapter();
            mArchiveListRecyclerViewAdapter.clearIds();
            mActionMode.finish();
            Toast.makeText(mContext, R.string.restored, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(String message) {
        ErrorDialog errorDialog = new ErrorDialog(mContext, message);
        errorDialog.show();
    }

    private void removePlacesFromAdapter(){
        List<Integer> selectedItemPositions =
                mArchiveListRecyclerViewAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mArchiveListRecyclerViewAdapter.removePlaceFromList(selectedItemPositions.get(i));
        }
    }
}

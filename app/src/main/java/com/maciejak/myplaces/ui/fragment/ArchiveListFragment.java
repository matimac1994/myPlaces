package com.maciejak.myplaces.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.maciejak.myplaces.listener.OnCloseFloatingActionMenu;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repository.PlaceRepository;
import com.maciejak.myplaces.ui.activity.ShowPlaceActivity;
import com.maciejak.myplaces.ui.adapter.ArchiveListRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArchiveListFragment extends BaseFragment
        implements ArchiveListRecyclerViewAdapter.ArchiveListAdapterListener,
        ActionMode.Callback{

    @BindView(R.id.archive_list_recycler_view)
    RecyclerView mArchiveListRecyclerview;

    @BindView(R.id.archive_list_empty_view)
    LinearLayout mEmptyView;

    ArchiveListRecyclerViewAdapter mArchiveListRecyclerViewAdapter;
    PlaceRepository mPlaceRepository;
    List<Place> mPlaces;

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
        mPlaces = mPlaceRepository.getAllDeletedPlaces();
        mArchiveListRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mArchiveListRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mArchiveListRecyclerViewAdapter = new ArchiveListRecyclerViewAdapter(mPlaces, getContext(), this);
        mArchiveListRecyclerview.setAdapter(mArchiveListRecyclerViewAdapter);
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
        mArchiveListRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        mPlaces = mPlaceRepository.getAllDeletedPlaces();
        manageVisibility(mPlaces);
        super.onStart();
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
                mode.finish();
                return true;
            case R.id.action_restore:
                restorePlaces();
                mode.finish();
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
        mActionMode = null;
    }

    private void restorePlaces(){
        List<Integer> selectedItemPositions =
                mArchiveListRecyclerViewAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mArchiveListRecyclerViewAdapter.restorePlace(selectedItemPositions.get(i));
        }
        manageVisibility(mPlaces);
        Toast.makeText(mContext, R.string.restored, Toast.LENGTH_SHORT).show();
    }

    private void deletePlaces(){
        List<Integer> selectedItemPositions =
                mArchiveListRecyclerViewAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mArchiveListRecyclerViewAdapter.removePlace(selectedItemPositions.get(i));
        }
        manageVisibility(mPlaces);
        Toast.makeText(mContext, R.string.deleted, Toast.LENGTH_SHORT).show();
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
            Place place = mArchiveListRecyclerViewAdapter.getItem(position);
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
}

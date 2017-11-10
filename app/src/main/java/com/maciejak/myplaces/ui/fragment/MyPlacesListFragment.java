package com.maciejak.myplaces.ui.fragment;

import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.repository.PlaceRepository;
import com.maciejak.myplaces.service.DeletePlacesService;
import com.maciejak.myplaces.ui.activity.ShowPlaceActivity;
import com.maciejak.myplaces.ui.adapter.MyPlacesListRecyclerViewAdapter;
import com.maciejak.myplaces.model.Place;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPlacesListFragment extends BaseFragment implements View.OnClickListener{

    List<Place> mPlaces;

    @BindView(R.id.my_places_list_recycler_view)
    RecyclerView mMyPlacesListRecyclerView;

    @BindView(R.id.my_places_list_empty_view)
    LinearLayout mEmptyView;

    MyPlacesListRecyclerViewAdapter mMyPlacesListRecyclerViewAdapter;

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
        getActivity().setTitle(R.string.list_of_favourites);

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
                mMyPlacesListRecyclerViewAdapter.onItemRemove(viewHolder, mMyPlacesListRecyclerView);

//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage(getString(R.string.are_you_sure_to_delete));
//
//                builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> {
//                    Place place = mMyPlacesListRecyclerViewAdapter.getItem(position);
//                    place.delete();
//                    mPlaces.remove(position);
//                    Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
//                    mMyPlacesListRecyclerViewAdapter.notifyItemRemoved(position);
//                    mMyPlacesListRecyclerView.scrollToPosition(position-1);
//                    manageVisibility(mPlaces);
//                }).setNegativeButton(getString(R.string.back), (dialog, which) -> mMyPlacesListRecyclerViewAdapter.notifyItemChanged(position));
//
//                AlertDialog dialog = builder.show();
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Red));
//                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Green));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mMyPlacesListRecyclerView);
    }

    private void populateRecyclerView(List<Place> places){
        mMyPlacesListRecyclerViewAdapter = new MyPlacesListRecyclerViewAdapter(getActivity(), places, this);
        mMyPlacesListRecyclerView.setAdapter(mMyPlacesListRecyclerViewAdapter);
        mMyPlacesListRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void manageVisibility(List<Place> places){
        if (places.isEmpty()){
            mEmptyView.setVisibility(View.VISIBLE);
            mMyPlacesListRecyclerView.setVisibility(View.GONE);
        }
        else {
            mEmptyView.setVisibility(View.GONE);
            mMyPlacesListRecyclerView.setVisibility(View.VISIBLE);
            populateRecyclerView(places);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PlaceRepository placeRepository = new PlaceRepository();
        mPlaces = placeRepository.getAllPlaces();
        manageVisibility(mPlaces);
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
    public void onPause() {
        ArrayList<String> placeIdsToDelete = new ArrayList<>();
        if (mMyPlacesListRecyclerViewAdapter != null){
            if (mMyPlacesListRecyclerViewAdapter.placesToDelete != null){
                for (Place place : mMyPlacesListRecyclerViewAdapter.placesToDelete){
                    placeIdsToDelete.add(String.valueOf(place.getId()));
                }
                Intent intent = new Intent(getContext(), DeletePlacesService.class);
                intent.putStringArrayListExtra(DeletePlacesService.PLACES_TO_DELETE_IDS, placeIdsToDelete);
                getActivity().startService(intent);
            }
        }

        super.onPause();

    }

    @Override
    protected void actionAfterAddPlaceDone(Intent data) {
        super.actionAfterAddPlaceDone(data);
        if (mPlaces!=null){
            populateRecyclerView(mPlaces);
            mMyPlacesListRecyclerViewAdapter.notifyItemInserted(mMyPlacesListRecyclerViewAdapter.getItemCount());
            mMyPlacesListRecyclerView.smoothScrollToPosition(mMyPlacesListRecyclerViewAdapter.getItemCount());
        }
    }
}

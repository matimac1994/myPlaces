package com.maciejak.myplaces.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionMenu;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.model.PlacePhoto;
import com.maciejak.myplaces.repository.PlacePhotoRepository;
import com.maciejak.myplaces.ui.activity.ShowPlaceActivity;
import com.maciejak.myplaces.ui.adapter.MyPlacesListRecyclerViewAdapter;
import com.maciejak.myplaces.model.Place;
import com.raizlabs.android.dbflow.sql.language.SQLite;

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
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mMyPlacesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getString(R.string.are_you_sure_to_delete));

                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Place place = mMyPlacesListRecycelerViewAdapter.getItem(position);
                        place.delete();
                        mPlaces.remove(position);
                        mMyPlacesListRecycelerViewAdapter.notifyItemRemoved(position);
                        manageVisibility(mPlaces);
                    }
                }).setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMyPlacesListRecycelerViewAdapter.notifyItemChanged(position);
                    }
                });

                AlertDialog dialog = builder.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Red));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Green));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mMyPlacesListRecyclerView);
    }

    private void populateRecyclerView(List<Place> places){
        mMyPlacesListRecycelerViewAdapter = new MyPlacesListRecyclerViewAdapter(getActivity(), places, this);
        mMyPlacesListRecyclerView.setAdapter(mMyPlacesListRecycelerViewAdapter);
        mMyPlacesListRecycelerViewAdapter.notifyDataSetChanged();
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
        mPlaces = SQLite.select().from(Place.class).queryList();
        manageVisibility(mPlaces);
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mMyPlacesListRecyclerView.getChildAdapterPosition(v);
        Place place = mMyPlacesListRecycelerViewAdapter.getItem(itemPosition);
        int viewId = v.getId();
        switch (viewId){
            case R.id.card_view:
                Intent intent = new Intent(getContext(), ShowPlaceActivity.class);
                intent.putExtra(ShowPlaceActivity.PLACE_ID, place.getId());
                startActivity(intent);
                break;
        }
    }
}

package com.maciejak.myplaces.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.models.Place;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mati on 21.10.2017.
 */

public class MyPlacesListRecyclerViewAdapter extends RecyclerView.Adapter<MyPlacesListRecyclerViewAdapter.ViewHolder> {

    private List<Place> mPlaces;
    private Context mContext;
    LayoutInflater mInflater;

    public MyPlacesListRecyclerViewAdapter(Context context, List<Place> places) {
        this.mPlaces = places;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = this.mInflater.inflate(R.layout.row_favourite_my_places, parent, false);

        return new MyPlacesListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Place place = mPlaces.get(position);
        if (place.getPhotos().size()>0){
            String image = place.getPhotos().get(0).getImage();

            Picasso.with(mContext)
                    .load(image)
                    .centerCrop()
                    .fit()
                    .into(holder.mImageView);
        }
        if (place.getTitle() == null || place.getTitle().equals("")){
            holder.mTitle.setVisibility(View.GONE);
        }
        else {
            holder.mTitle.setVisibility(View.VISIBLE);
            holder.mTitle.setText(place.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.row_favourite_my_place_image) ImageView mImageView;
        @BindView(R.id.row_favourite_my_place_title) TextView mTitle;
        @BindView(R.id.row_favourite_my_place_button_delete) Button mDeleteButton;
        @BindView(R.id.row_favourite_my_place_button_edit) Button mEditButton;
        @BindView(R.id.row_favourite_my_place_button_details) Button mDetailsButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

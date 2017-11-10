package com.maciejak.myplaces.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.model.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mati on 21.10.2017.
 */

public class MyPlacesListRecyclerViewAdapter extends RecyclerView.Adapter<MyPlacesListRecyclerViewAdapter.ViewHolder> {

    private List<Place> mPlaces;
    public List<Place> placesToDelete;
    private Context mContext;
    LayoutInflater mInflater;
    View.OnClickListener mOnClickListener;

    public MyPlacesListRecyclerViewAdapter(Context context, List<Place> places, View.OnClickListener onClickListener) {
        this.mPlaces = places;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mOnClickListener = onClickListener;
        placesToDelete = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = this.mInflater.inflate(R.layout.row_my_places_list, parent, false);
        view.setOnClickListener(mOnClickListener);

        return new MyPlacesListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Place place = mPlaces.get(position);
        String mapPhoto = place.getMapPhoto();

        Picasso.with(mContext)
                .load(mapPhoto)
                .centerCrop()
                .fit()
                .into(holder.mImageView);
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
        return ((mPlaces!=null) ? mPlaces.size() : 0);
    }

    public void removeItem(int position) {
        mPlaces.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Place item, int position) {
        mPlaces.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView){
        Place deletedPlace = mPlaces.get(viewHolder.getAdapterPosition());
        final int position = viewHolder.getAdapterPosition();
        Snackbar snackbar = Snackbar
                .make(recyclerView, mContext.getString(R.string.deleted), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, view -> {
                    if (position == 0 || position == getItemCount())
                        recyclerView.scrollToPosition(position);
                    restoreItem(deletedPlace, position);
                    placesToDelete.remove(deletedPlace);
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
        removeItem(position);
        placesToDelete.add(deletedPlace);
    }

    public Place getItem(int position){
        return mPlaces.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.row_my_places_list_image) ImageView mImageView;
        @BindView(R.id.row_my_places_list_title) TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

package com.maciejak.myplaces.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;
import com.maciejak.myplaces.managers.PlaceListManager;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mati on 21.10.2017.
 */

public class MyPlacesListRecyclerViewAdapter extends RecyclerView.Adapter<MyPlacesListRecyclerViewAdapter.ViewHolder> {

    private List<PlaceListResponse> mPlaces;
    private Context mContext;
    private MyPlacesListOnDataChangeListener mMyPlacesListOnDataChangeListener;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;

    public MyPlacesListRecyclerViewAdapter(Context context, List<PlaceListResponse> places, View.OnClickListener onClickListener, MyPlacesListOnDataChangeListener myPlacesListOnDataChangeListener) {
        this.mPlaces = places;
        this.mContext = context;
        this.mMyPlacesListOnDataChangeListener = myPlacesListOnDataChangeListener;
        this.mInflater = LayoutInflater.from(context);
        this.mOnClickListener = onClickListener;
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

        PlaceListResponse place = mPlaces.get(position);
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
        notifyItemRemoved(position);
        mMyPlacesListOnDataChangeListener.onDataChanged(mPlaces);
    }

    public void restoreItem(PlaceListResponse item, int position) {
        mPlaces.add(position, item);
        notifyItemInserted(position);
        mMyPlacesListOnDataChangeListener.onDataChanged(mPlaces);
    }

    public PlaceListResponse getItem(int position){
        return mPlaces.get(position);
    }

    public void updateList(List<PlaceListResponse> places){
        if (places != null && places.size() > 0){
            mPlaces.clear();
            mPlaces.addAll(places);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.row_my_places_list_image) ImageView mImageView;
        @BindView(R.id.row_my_places_list_title) TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface MyPlacesListOnDataChangeListener {
        void onDataChanged(List<PlaceListResponse> places);
    }
}

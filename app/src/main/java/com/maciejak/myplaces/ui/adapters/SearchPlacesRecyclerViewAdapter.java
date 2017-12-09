package com.maciejak.myplaces.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;
import com.maciejak.myplaces.model.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mati on 16.11.2017.
 */

public class SearchPlacesRecyclerViewAdapter extends RecyclerView.Adapter<SearchPlacesRecyclerViewAdapter.ViewHolder> implements Filterable{

    private Context mContext;
    private List<PlaceListResponse> mPlaces;
    private List<PlaceListResponse> mFilteredList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;

    public SearchPlacesRecyclerViewAdapter(Context context, List<PlaceListResponse> places, View.OnClickListener onClickListener) {
        mContext = context;
        mPlaces = places;
        mInflater = LayoutInflater.from(mContext);
        mOnClickListener = onClickListener;

        mFilteredList = mPlaces;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = this.mInflater.inflate(R.layout.row_search_places, parent, false);
        view.setOnClickListener(mOnClickListener);
        return new SearchPlacesRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaceListResponse place = mFilteredList.get(position);
        String mapPhoto = place.getMapPhoto();
        boolean hasTitle = !place.getTitle().isEmpty();
        boolean hasDescription = !place.getDescription().isEmpty();

        Picasso.with(mContext)
                .load(mapPhoto)
                .centerCrop()
                .fit()
                .into(holder.mImageView);

        holder.mTitle.setText(hasTitle ? place.getTitle() : mContext.getString(R.string.no_title));
        holder.mDescription.setText(hasDescription ? place.getDescription() : mContext.getString(R.string.no_description));
    }

    @Override
    public int getItemCount() {
        return ((mFilteredList !=null) ? mFilteredList.size() : 0);
    }

    public PlaceListResponse getItem(int position){
        return mFilteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String charString = constraint.toString();

                if (charString.isEmpty()){
                    mFilteredList = mPlaces;
                } else {
                    ArrayList<PlaceListResponse> filteredList = new ArrayList<>();

                    for (PlaceListResponse place : mPlaces){
                        if (place.getTitle().toLowerCase().contains(charString)
                                || place.getDescription().toLowerCase().contains(charString)){

                            filteredList.add(place);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredList = (ArrayList<PlaceListResponse>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateList(List<PlaceListResponse> places) {
        if (places != null && places.size() > 0){
            mPlaces.clear();
            mPlaces.addAll(places);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row_search_places_image)
        ImageView mImageView;
        @BindView(R.id.row_search_places_title)
        TextView mTitle;
        @BindView(R.id.row_search_places_description)
        TextView mDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

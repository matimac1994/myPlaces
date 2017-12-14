package com.maciejak.myplaces.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.TopPlaceResponseList;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mati on 14.12.2017.
 */

public class TopPlacesRecyclerViewAdapter extends RecyclerView.Adapter<TopPlacesRecyclerViewAdapter.ViewHolder> {

    private List<TopPlaceResponseList> mTopPlaces;
    private Context mContext;
    private LayoutInflater mInflater;
    private View.OnClickListener listener;

    public TopPlacesRecyclerViewAdapter(List<TopPlaceResponseList> topPlaces, Context context,View.OnClickListener listener) {
        mTopPlaces = topPlaces;
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = this.mInflater.inflate(R.layout.row_top_places, parent, false);
        view.setOnClickListener(listener);

        return new TopPlacesRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TopPlaceResponseList topPlace = mTopPlaces.get(position);

        String photo = topPlace.getMainPhoto();

        Picasso.with(mContext)
                .load(photo)
                .centerCrop()
                .fit()
                .into(holder.image);

        String rankTitle = topPlace.getRank() + ". " + topPlace.getName();
        holder.rankTitle.setText(rankTitle);
    }

    @Override
    public int getItemCount() {
        return mTopPlaces.size();
    }

    public TopPlaceResponseList getItem(int itemPosition) {
        return mTopPlaces.get(itemPosition);
    }

    public void updateList(List<TopPlaceResponseList> topPlaces){
        if (topPlaces != null && topPlaces.size() > 0){
            mTopPlaces.clear();
            mTopPlaces.addAll(topPlaces);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_top_places_image)
        ImageView image;

        @BindView(R.id.row_top_places_title)
        TextView rankTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

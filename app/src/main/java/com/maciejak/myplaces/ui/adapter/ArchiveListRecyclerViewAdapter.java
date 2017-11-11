package com.maciejak.myplaces.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.listener.ArchiveListOnDataChangeListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repository.PlaceRepository;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mati on 11.11.2017.
 */

public class ArchiveListRecyclerViewAdapter extends RecyclerView.Adapter<ArchiveListRecyclerViewAdapter.ViewHolder> {

    private List<Place> mPlaces;
    private Context mContext;
    private ArchiveListOnDataChangeListener mArchiveListOnDataChangeListener;
    private LayoutInflater mInflater;
    private PlaceRepository mPlaceRepository;

    public ArchiveListRecyclerViewAdapter(List<Place> places, Context context, ArchiveListOnDataChangeListener archiveListOnDataChangeListener) {
        mPlaces = places;
        mContext = context;
        mArchiveListOnDataChangeListener = archiveListOnDataChangeListener;
        mInflater = LayoutInflater.from(context);
        mPlaceRepository = new PlaceRepository();
    }

    @Override
    public ArchiveListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = this.mInflater.inflate(R.layout.row_archive_list, parent, false);
        return new ArchiveListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArchiveListRecyclerViewAdapter.ViewHolder holder, int position) {

        Place place = mPlaces.get(position);

        String mapPhoto = place.getMapPhoto();
        Picasso.with(mContext)
                .load(mapPhoto)
                .centerCrop()
                .fit()
                .into(holder.mImageView);

        holder.mTitleTextView.setText(place.getTitle());
        if (place.getDescription().isEmpty())
            holder.mDescriptionTextView.setText(R.string.no_description);
        else
            holder.mDescriptionTextView.setText(place.getDescription());

    }

    @Override
    public int getItemCount() {
        return ((mPlaces!=null) ? mPlaces.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_archive_list_image)
        ImageView mImageView;

        @BindView(R.id.row_archive_list_title_text_view)
        TextView mTitleTextView;

        @BindView(R.id.row_archive_list_description_text_view)
        TextView mDescriptionTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.row_archive_list_delete_button)
        public void onClickDeleteButton(){
            final int position = getAdapterPosition();
            Place place = mPlaces.get(position);
            mPlaceRepository.deletePlace(place);
            mPlaces.remove(place);
            notifyItemRemoved(position);
            mArchiveListOnDataChangeListener.onDataChanged();
        }

        @OnClick(R.id.row_archive_list_restore_button)
        public void onClickRestoreButton(){
            final int position = getAdapterPosition();
            Place place = mPlaces.get(position);
            mPlaceRepository.restorePlace(place);
            mPlaces.remove(place);
            notifyItemRemoved(position);
            mArchiveListOnDataChangeListener.onDataChanged();
        }
    }
}

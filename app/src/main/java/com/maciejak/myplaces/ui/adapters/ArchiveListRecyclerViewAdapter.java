package com.maciejak.myplaces.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Mati on 11.11.2017.
 */

public class ArchiveListRecyclerViewAdapter extends RecyclerView.Adapter<ArchiveListRecyclerViewAdapter.ViewHolder> {

    private List<PlaceListResponse> mPlaces;
    private List<Long> placeIds = new ArrayList<>();
    private Context mContext;
    private ArchiveListAdapterListener mArchiveListAdapterListener;
    private LayoutInflater mInflater;

    private PlaceRepository mPlaceRepository;
    private SparseBooleanArray mSelectedPlacesPositions;

    public ArchiveListRecyclerViewAdapter(List<PlaceListResponse> places, Context context, ArchiveListAdapterListener archiveListAdapterListener) {
        mPlaces = places;
        mContext = context;
        mArchiveListAdapterListener = archiveListAdapterListener;
        mInflater = LayoutInflater.from(context);

        mPlaceRepository = new PlaceRepository();
        mSelectedPlacesPositions = new SparseBooleanArray();
        setHasStableIds(true);
    }

    @Override
    public ArchiveListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = this.mInflater.inflate(R.layout.row_archive_list, parent, false);
        return new ArchiveListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArchiveListRecyclerViewAdapter.ViewHolder holder, int position) {
        PlaceListResponse place = mPlaces.get(position);
        boolean hasTitle = !place.getTitle().isEmpty();
        boolean hasDescription = !place.getDescription().isEmpty();

        String mapPhoto = place.getMapPhoto();
        Picasso.with(mContext)
                .load(mapPhoto)
                .centerCrop()
                .fit()
                .into(holder.mImageView);

        holder.mTitleTextView.setText(hasTitle ? place.getTitle() : mContext.getString(R.string.no_title));
        holder.mDescriptionTextView.setText(hasDescription ? place.getDescription() : mContext.getString(R.string.no_description));

        holder.itemView.setActivated(mSelectedPlacesPositions.get(position, false));

        if (holder.itemView.isActivated()){
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }
        else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        applyIconAppearance(holder, position);
    }

    public void updateList(List<PlaceListResponse> places){
        if (places != null && places.size() > 0){
            mPlaces.clear();
            mPlaces.addAll(places);
            notifyDataSetChanged();
        }
    }

    private void applyIconAppearance(ViewHolder holder, int position){
        if (mSelectedPlacesPositions.get(position, false)){
            holder.mImageFrontLayout.setVisibility(View.GONE);
            holder.mImageBackLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mImageBackLayout.setVisibility(View.GONE);
            holder.mImageFrontLayout.setVisibility(View.VISIBLE);
        }
    }

    public void clearSelections() {
        mSelectedPlacesPositions.clear();
        notifyDataSetChanged();
    }

    public void clearIds(){
        this.placeIds.clear();
    }

    @Override
    public int getItemCount() {
        return ((mPlaces!=null) ? mPlaces.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        return mPlaces.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public int getSelectedItemCount() {
        return mSelectedPlacesPositions.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(mSelectedPlacesPositions.size());
        for (int i = 0; i < mSelectedPlacesPositions.size(); i++) {
            items.add(mSelectedPlacesPositions.keyAt(i));
        }
        return items;
    }

    public List<Long> getPlaceIds() {
        return placeIds;
    }

    public PlaceListResponse getItem(int position){
        return mPlaces.get(position);
    }

    public void toggleSelection(int position) {
        PlaceListResponse place = mPlaces.get(position);
        if (mSelectedPlacesPositions.get(position, false)) {
            mSelectedPlacesPositions.delete(position);
            placeIds.remove(place.getId());
        } else {
            mSelectedPlacesPositions.put(position, true);
            placeIds.add(place.getId());
        }
        notifyItemChanged(position, mPlaces.get(position));
    }

    public void removePlaceFromList(int position) {
        mPlaces.remove(position);
        notifyItemRemoved(position);
        mArchiveListAdapterListener.onDataChanged(mPlaces);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_archive_list_image)
        ImageView mImageView;

        @BindView(R.id.row_archive_list_title_text_view)
        TextView mTitleTextView;

        @BindView(R.id.row_archive_list_description_text_view)
        TextView mDescriptionTextView;

        @BindView(R.id.row_archive_list_image_front)
        RelativeLayout mImageFrontLayout;

        @BindView(R.id.row_archive_list_image_back)
        RelativeLayout mImageBackLayout;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.row_archive_list_image_container)
        void onClickImage() {
            mArchiveListAdapterListener.onImageClicked(getAdapterPosition());
        }

        @OnClick(R.id.row_archive_list_title_description_container)
        void onClickTitleDescription() {
            mArchiveListAdapterListener.onTitleOrDescriptionClicked(getAdapterPosition());
        }

        @OnLongClick(R.id.row_archive_list_title_description_container)
        boolean onLongClickTitleDescription(){
            mArchiveListAdapterListener.onRowLongClicked(getAdapterPosition());
            itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }

        @OnLongClick(R.id.row_archive_list_image_container)
        boolean onLongClickImage(){
            mArchiveListAdapterListener.onRowLongClicked(getAdapterPosition());
            itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }

    }

    public interface ArchiveListAdapterListener{
        void onDataChanged(List<PlaceListResponse> places);

        void onImageClicked(int position);

        void onTitleOrDescriptionClicked(int position);

        void onRowLongClicked(int position);
    }
}

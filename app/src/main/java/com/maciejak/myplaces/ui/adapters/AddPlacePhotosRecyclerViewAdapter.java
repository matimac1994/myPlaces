package com.maciejak.myplaces.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mati on 22.10.2017.
 */

public class AddPlacePhotosRecyclerViewAdapter extends RecyclerView.Adapter<AddPlacePhotosRecyclerViewAdapter.ViewHolder> {

    private List<PlacePhotoResponse> addedPhotos;
    private Context mContext;
    private AddPlacePhotosListener listener;

    public AddPlacePhotosRecyclerViewAdapter(Context context, List<PlacePhotoResponse> addedPhotos, AddPlacePhotosListener listener){
        this.mContext = context;
        this.addedPhotos = addedPhotos;
        this.listener = listener;
    }

    @Override
    public AddPlacePhotosRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_place_photo, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlacePhotoResponse placePhotoResponse = addedPhotos.get(position);
        Picasso.with(mContext)
                .load(placePhotoResponse.getPlacePhotoUrl())
                .noPlaceholder()
                .fit()
                .centerCrop()
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return addedPhotos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;
        private ImageButton mImageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            this.mImageView = (ImageView)itemView.findViewById(R.id.row_add_place_photo_image);
            this.mImageButton = (ImageButton)itemView.findViewById(R.id.row_add_place_delete_button);

            this.mImageButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            listener.onClickDeletePhotoButton(addedPhotos.get(position), position);
        }
    }

    public interface AddPlacePhotosListener{
        void onClickDeletePhotoButton(PlacePhotoResponse placePhotoResponse, int position);
    }
}

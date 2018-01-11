package com.maciejak.myplaces.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.maciejak.myplaces.model.PlacePhoto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mati on 04.11.2017.
 */

public class EditPlacePhotosRecyclerViewAdapter extends RecyclerView.Adapter<EditPlacePhotosRecyclerViewAdapter.ViewHolder> {
    private List<PlacePhotoResponse> mPhotos;
    private Context mContext;
    private EditPlacePhotosRecyclerViewAdapterListener listener;

    public EditPlacePhotosRecyclerViewAdapter(Context context, List<PlacePhotoResponse> photos,
                                              EditPlacePhotosRecyclerViewAdapterListener listener){
        this.mPhotos = photos;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public EditPlacePhotosRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_edit_place_photo, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (mPhotos != null){
            Picasso.with(mContext)
                    .load(mPhotos.get(position).getPlacePhotoUrl())
                    .noPlaceholder()
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return ((mPhotos != null) ? mPhotos.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;
        private ImageButton mImageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            this.mImageView = (ImageView)itemView.findViewById(R.id.row_edit_place_photo_image);
            this.mImageButton = (ImageButton)itemView.findViewById(R.id.row_edit_place_delete_button);

            this.mImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            listener.onClickDeletePhotoButton(mPhotos.get(position), position);
        }
    }

    public interface EditPlacePhotosRecyclerViewAdapterListener{
        void onClickDeletePhotoButton(PlacePhotoResponse placePhotoResponse, int position);
    }
}

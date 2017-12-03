package com.maciejak.myplaces.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.model.PlacePhoto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mati on 04.11.2017.
 */

public class EditPlacePhotosRecyclerViewAdapter extends RecyclerView.Adapter<EditPlacePhotosRecyclerViewAdapter.ViewHolder> {
    private List<PlacePhoto> mPhotos;
    private Context mContext;
    private List<PlacePhoto> photosToDelete;

    public EditPlacePhotosRecyclerViewAdapter(Context context, List<PlacePhoto> photos, List<PlacePhoto> photosToDelete){
        this.mPhotos = photos;
        this.mContext = context;
        this.photosToDelete = photosToDelete;
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
                    .load(mPhotos.get(position).getImage())
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
            PlacePhoto placePhoto = mPhotos.get(position);
            photosToDelete.add(placePhoto);
            mPhotos.remove(position);
            notifyItemRemoved(position);
        }
    }

}

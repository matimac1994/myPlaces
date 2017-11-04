package com.maciejak.myplaces.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.model.PlacePhoto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mati on 02.11.2017.
 */

public class ShowPlaceViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<PlacePhoto> mPhotos;
    ImageView mImage;

    public ShowPlaceViewPagerAdapter(Context context, List<PlacePhoto> photos) {
        mContext = context;
        mPhotos = photos;
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.row_show_place_view_pager, null);
        mImage = (ImageView) view.findViewById(R.id.row_show_place_view_pager_image_view);
        String image = mPhotos.get(position).getImage();

//        mImage.setImageResource(R.drawable.myplaces_logo_sample);

        Picasso.with(mContext)
                .load(image)
                .centerCrop()
                .fit()
                .into(mImage);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}

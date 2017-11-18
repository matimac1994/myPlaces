package com.maciejak.myplaces.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maciejak.myplaces.listener.OnCloseFloatingActionMenu;
import com.maciejak.myplaces.ui.activity.BaseActivity;

/**
 * Created by Mati on 18.11.2017.
 */

public class BaseFragment extends Fragment {

    protected BaseActivity mBaseActivity;
    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mBaseActivity = (BaseActivity)context;
        mContext = context;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((OnCloseFloatingActionMenu)mContext).closeFloatingActionMenu();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


}

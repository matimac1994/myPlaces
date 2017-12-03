package com.maciejak.myplaces.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.utils.Const;

/**
 * Created by Mati on 28.11.2017.
 */

public class ErrorDialog {

    private Context mContext;
    private String message;

    public ErrorDialog(Context context, String message) {
        this.mContext = context;
        this.message = message;
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

        builder.setTitle(R.string.error);
        builder.setIcon(R.drawable.ic_action_warning);
        builder.setMessage(message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

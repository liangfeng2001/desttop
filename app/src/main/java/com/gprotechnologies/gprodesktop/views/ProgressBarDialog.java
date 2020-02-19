package com.gprotechnologies.gprodesktop.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.gprotechnologies.gprodesktop.R;

public class ProgressBarDialog {

    private AlertDialog alertDialog;
    private ProgressBar progressBar;
    private Activity activity;

    public ProgressBarDialog(Activity activity) {
        this.activity =activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View progressBarView = LayoutInflater.from(activity).inflate(R.layout.dialog_download, null);
                alertDialog = new AlertDialog.Builder(activity)
                        .setView(progressBarView)
                        .setTitle("Downloading...")
                        .create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
                progressBar = progressBarView.findViewById(R.id.pb_download);
                progressBar.setMax(100);
            }
        });
    }

    public void setCurrentProgress(int currentProgress) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(currentProgress);
            }
        });
    }

    public void dismiss() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        });
    }
}

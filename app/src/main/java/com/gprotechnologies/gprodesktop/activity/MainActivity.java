package com.gprotechnologies.gprodesktop.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gprotechnologies.gprodesktop.R;
import com.gprotechnologies.gprodesktop.adapter.AppRecycleViewAdapter;
import com.gprotechnologies.gprodesktop.bean.AppInfo;
import com.gprotechnologies.gprodesktop.utils.AppUtils;

public class MainActivity extends AppCompatActivity implements AppRecycleViewAdapter.OnItemCLickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView rcv;
    private AppRecycleViewAdapter adapter;
    private View passwordView;
    private AlertDialog passwordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rcv = findViewById(R.id.rcv);
        rcv.setLayoutManager(new GridLayoutManager(this, 4));

        adapter = new AppRecycleViewAdapter(AppUtils.getAppList(this), this);
        rcv.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(final AppInfo appInfo) {
        if (appInfo.getPackageName().matches("(.*settings$)|(.*rk$)")) {
            if (passwordView == null)
                passwordView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null);
            if (passwordDialog == null) {
                createDialog(appInfo);
            }
            passwordDialog.show();
            return;
        }
        startApp(appInfo);
    }

    private void createDialog(final AppInfo appInfo) {
        passwordDialog = new AlertDialog.Builder(this)
                .setView(passwordView)
                .setTitle("Please enter password")
                .setNegativeButton("cancel", null)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = ((EditText) passwordView.findViewById(R.id.et_setting_password)).getText().toString();
                        if(password.equals("gproadmin")){
                            startApp(appInfo);
                        }else {
                            Toast.makeText(MainActivity.this, "password error", Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
    }

    private void startApp(AppInfo appInfo) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


}

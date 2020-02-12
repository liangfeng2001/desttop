package com.gprotechnologies.gprodesktop.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gprotechnologies.gprodesktop.R;
import com.gprotechnologies.gprodesktop.adapter.AppRecycleViewAdapter;
import com.gprotechnologies.gprodesktop.bean.AppInfo;
import com.gprotechnologies.gprodesktop.event.MessageEvent;
import com.gprotechnologies.gprodesktop.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends Activity implements AppRecycleViewAdapter.OnItemCLickListener {

    public static final String PWD_HIDE = "hide";
    public static final String PWD_SHOW = "show";
    public static final String SHOW_APP_PACKAGE = "(com.gpro\\w*.\\w*.\\w*)|(com.android.settings)|(com.android.rk)";

    private RecyclerView rcv;
    private AppRecycleViewAdapter adapter;
    private View passwordView;
    private AlertDialog passwordDialog;
    private AppInfo currentApp;

    private static String passwordRegApp = "(.*settings$)|(.*rk$)";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        rcv = findViewById(R.id.rcv);
        rcv.setLayoutManager(new GridLayoutManager(this, 4));

        initAdapter(SHOW_APP_PACKAGE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppUpdate(MessageEvent event) {
        if (event == MessageEvent.APP_UPDATE) {
            initAdapter(SHOW_APP_PACKAGE);
        }
    }

    private void initAdapter(String reg) {
        if (adapter != null) {
            adapter.initAppList(AppUtils.getAppList(this, reg));
            return;
        }
        adapter = new AppRecycleViewAdapter(AppUtils.getAppList(this, reg), this);
        rcv.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AppInfo appInfo) {
        this.currentApp = appInfo;
        if(adapter.isEnableUninstall()){
            startActivity(new Intent(Intent.ACTION_DELETE, Uri.fromParts("package",currentApp.getPackageName(),null)));
            return;
        }
        if (passwordRegApp != null)
            if (currentApp.getPackageName().matches(passwordRegApp)) {
                if (passwordView == null)
                    passwordView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null);
                if (passwordDialog == null) {
                    createDialog();
                }
                passwordDialog.show();
                initDialogLayout();
                dialogShowPwd();
                return;
            }
        AppUtils.launchApp(MainActivity.this, currentApp);
    }

    private void initDialogLayout() {
        ((EditText) passwordView.findViewById(R.id.et_setting_password)).setText("");
        ((EditText) passwordView.findViewById(R.id.et_setting_password)).setInputType(0x00000081);
        ((ImageView) passwordDialog.findViewById(R.id.iv_show_password)).setTag(PWD_SHOW);
        ((ImageView) passwordDialog.findViewById(R.id.iv_show_password)).setBackgroundResource(R.mipmap.show_pwd);
    }

    private void dialogShowPwd() {
        passwordView.findViewById(R.id.iv_show_password).setOnClickListener(new View.OnClickListener() {
            private EditText editText;

            @Override
            public void onClick(View v) {
                editText = ((EditText) passwordView.findViewById(R.id.et_setting_password));
                if (String.valueOf(v.getTag()).equals(PWD_HIDE)) {
                    v.setTag(PWD_SHOW);
                    v.setBackgroundResource(R.mipmap.show_pwd);
                    editText.setInputType(0x00000081);
                } else {
                    v.setTag(PWD_HIDE);
                    v.setBackgroundResource(R.mipmap.hide_pwd);
                    editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private void createDialog() {
        passwordDialog = new AlertDialog.Builder(this)
                .setView(passwordView)
                .setTitle(getString(R.string.dialog_title))
                .setNegativeButton(getString(R.string.dialog_btn_cancel), null)
                .setPositiveButton(getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = ((EditText) passwordView.findViewById(R.id.et_setting_password)).getText().toString();
                        if (getString(R.string.gpro_password).equals(password)) {
                            AppUtils.launchApp(MainActivity.this, currentApp);
                        } else if (getString(R.string.ek_password).equals(password)) {
                            passwordRegApp = null;
                            initAdapter(null);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.dialog_pwd_error, Toast.LENGTH_LONG).show();
                        }
                        ((EditText) passwordView.findViewById(R.id.et_setting_password)).setText("");
                    }
                }).create();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}

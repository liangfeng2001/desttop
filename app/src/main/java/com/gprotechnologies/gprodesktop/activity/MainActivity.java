package com.gprotechnologies.gprodesktop.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.gprotechnologies.gprodesktop.utils.ShapUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.gprotechnologies.gprodesktop.consts.AppConst.*;

public class MainActivity extends Activity implements AppRecycleViewAdapter.OnItemCLickListener {

    private RecyclerView rcv;
    private AppRecycleViewAdapter adapter;
    private View passwordView;
    private AlertDialog passwordDialog;
    private AppInfo currentApp;

    private String passwordRegApp = PASSWORD_APP_PACKNAME;

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
        initAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppUpdate(MessageEvent event) {
        if (event == MessageEvent.APP_UPDATE) {
            initAdapter();
        }
    }

    /**
     * 初始化图标列表
     */
    private void initAdapter() {
        List<AppInfo> appList;
        if (ShapUtils.get(EK_MODE, true)) {
            appList = AppUtils.getAppList(this, null);
            AppInfo appInfo = new AppInfo(null, getResources().getDrawable(R.mipmap.list), ORIGINAL_LIST, null);
            appList.add(appInfo);
        } else {
            appList = AppUtils.getAppList(this, SHOW_APP_PACKAGE);
        }
        appList.add(new AppInfo(null, getResources().getDrawable(R.mipmap.update), UPDATE_APP, null));
        if (adapter != null) {
            adapter.initAppList(appList);
            return;
        }
        adapter = new AppRecycleViewAdapter(appList, this);
        rcv.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AppInfo appInfo) {
        if(ORIGINAL_LIST.equals(appInfo.getName())){ // 显示原app列表
            passwordRegApp = PASSWORD_APP_PACKNAME;
            ShapUtils.put(EK_MODE, false);
            initAdapter();
            return;
        }
        if(UPDATE_APP.equals(appInfo.getName())){ // 更新app
            startActivity(new Intent(this,UpdateActivity.class));
            return;
        }
        this.currentApp = appInfo;
        if (adapter.isEnableUninstall()) {
            startActivity(new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", currentApp.getPackageName(), null)));
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

    /**
     * 初始化密码输入框
     */
    private void initDialogLayout() {
        ((EditText) passwordView.findViewById(R.id.et_setting_password)).setText("");
        ((EditText) passwordView.findViewById(R.id.et_setting_password)).setInputType(0x00000081);
        ((ImageView) passwordDialog.findViewById(R.id.iv_show_password)).setTag(PWD_SHOW);
        ((ImageView) passwordDialog.findViewById(R.id.iv_show_password)).setBackgroundResource(R.mipmap.show_pwd);
    }

    /**
     * 显示提示框密码
     */
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

    /**
     * 创建输入密码提示框
     */
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
                            ShapUtils.put(EK_MODE, true);
                            initAdapter();
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

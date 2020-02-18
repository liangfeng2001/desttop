package com.gprotechnologies.gprodesktop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gprotechnologies.gprodesktop.R;
import com.gprotechnologies.gprodesktop.adapter.AppUpdateListViewAdapter;
import com.gprotechnologies.gprodesktop.consts.AppConst;
import com.gprotechnologies.gprodesktop.utils.AppUtils;
import com.gprotechnologies.gprodesktop.utils.ShapUtils;
import com.gprotechnologies.gprodesktop.utils.SmbUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class UpdateActivity extends AppCompatActivity implements AppUpdateListViewAdapter.OnAppNameClick {


    public static final int HANDLER_UPDATE_APP_LIST = 1;
    private RecyclerView rcvAppList;
    private EditText etRemoteIp;
    private Button btnGetRemoteFile;
    private AppUpdateListViewAdapter adapter;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case HANDLER_UPDATE_APP_LIST:
                    SmbFile[] files = (SmbFile[]) msg.obj;
                    if (files != null) {
                        adapter = new AppUpdateListViewAdapter(UpdateActivity.this, Arrays.asList(files), UpdateActivity.this);
                        rcvAppList.setAdapter(adapter);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        rcvAppList = findViewById(R.id.rcv_app_list);
        etRemoteIp = findViewById(R.id.et_remoteIp);
        etRemoteIp.setText(ShapUtils.get(AppConst.REMOTE_URL, AppConst.SMB_IP));
        btnGetRemoteFile = findViewById(R.id.btn_get_remote_file);
        btnGetRemoteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = etRemoteIp.getText().toString();
                if("".equals(ip)){
                    Toast.makeText(UpdateActivity.this, getString(R.string.enter_ip), Toast.LENGTH_SHORT).show();
                    return;
                }
                ShapUtils.put(AppConst.REMOTE_URL, ip);
                getRemoteAppList(String.format(AppConst.SMB_URL, ShapUtils.get(AppConst.REMOTE_URL, AppConst.SMB_IP)));
            }
        });
        rcvAppList.setLayoutManager(new GridLayoutManager(this, 1));
        getRemoteAppList(String.format(AppConst.SMB_URL, ShapUtils.get(AppConst.REMOTE_URL, AppConst.SMB_IP)));
    }

    private void getRemoteAppList(String remoteUrl) {
        new Thread() {
            @Override
            public void run() {
                SmbFile[] files = new SmbFile[0];
                try {
                    files = SmbUtils.getFiles(remoteUrl);
                } catch (MalformedURLException | SmbException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Message message = handler.obtainMessage(HANDLER_UPDATE_APP_LIST, files);
                handler.sendMessage(message);
            }
        }.start();
    }

    @Override
    public void onClick(SmbFile smbFile) {
        File cacheDir = getCacheDir();
        File downloadFile = SmbUtils.downloadFile(smbFile, cacheDir.getAbsolutePath());
        AppUtils.installApk(this, downloadFile);
    }
}

package com.gprotechnologies.gprodesktop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                    List<SmbFile> files = (List<SmbFile>) msg.obj;
                    if (files != null) {
                        adapter = new AppUpdateListViewAdapter(UpdateActivity.this, files, UpdateActivity.this);
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
                if ("".equals(ip)) {
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

    /**
     * 获取smb文件列表
     *
     * @param remoteUrl
     */
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
                List<SmbFile> fileList = filterFileByVersion(files, remoteUrl);
                Message message = handler.obtainMessage(HANDLER_UPDATE_APP_LIST, fileList);
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 根据版本过滤
     *
     * @param files
     * @param remoteUrl
     * @return
     */
    private List<SmbFile> filterFileByVersion(SmbFile[] files, String remoteUrl) {
        ArrayList<SmbFile> list = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<PackageInfo> ip = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : ip) { // 遍历已经安装的应用包名
            for (SmbFile file : files) { //遍历服务器上所有目录包名
                try {
                    if (file.isDirectory()) { //必须是目录
                        String remotePackageName = file.getName();
                        String fileName = remotePackageName.substring(0, remotePackageName.length() - 1);
                        String currentPackageName = packageInfo.applicationInfo.packageName;
                        if (currentPackageName.equals(fileName)) { //匹配已安装应用包名和服务器文件包名
                            SmbFile[] smbFiles = SmbUtils.getFiles(remoteUrl + remotePackageName); // 获取包名目录里的所有应用文件
                            SmbFile smbFile = sortByVersion(smbFiles).get(0);
                            String name1 = smbFile.getName();
                            String versionName = name1.substring(name1.indexOf("_v") + 2, name1.indexOf(".apk"));
                            String[] version1 = versionName.split("\\.");
                            String[] version2 = packageInfo.versionName.split("\\.");
                            if (version1.length < version2.length) { // 版本号错误
                                continue;
                            }
                            for (int i1 = 0; i1 < version1.length; i1++) { // 配对版本号
                                if (i1 == version1.length - 1) { //最后一位
                                    if (Integer.parseInt(version1[i1]) <= Integer.parseInt(version2[i1])) {
                                        break;
                                    }
                                } else {
                                    if (Integer.parseInt(version1[i1]) <= Integer.parseInt(version2[i1])) {
                                        continue;
                                    }
                                }
                                list.add(smbFile);
                                break;
                            }
                        }
                    }
                } catch (SmbException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }


    /**
     * 根绝文件版本号进行排序
     * 格式： _v1.0.0.apk
     * @param smbFiles
     * @return
     */
    private ArrayList<SmbFile> sortByVersion(SmbFile[] smbFiles) {
        ArrayList<SmbFile> files = new ArrayList<>(Arrays.asList(smbFiles));
        Collections.sort(files, new Comparator<SmbFile>() {
            @Override
            public int compare(SmbFile o1, SmbFile o2) {
                String name1 = o1.getName();
                String name2 = o2.getName();
                String[] version1 = name1.substring(name1.indexOf("_v") + 2, name1.indexOf(".apk")).split("\\.");
                String[] version2 = name2.substring(name2.indexOf("_v") + 2, name2.indexOf(".apk")).split("\\.");
                if (version1.length != version2.length) return 0;
                for (int i1 = 0; i1 < version1.length; i1++) { // 配对版本号
                    if (i1 == version1.length - 1) { //最后一位
                        return -(Integer.parseInt(version1[i1]) - Integer.parseInt(version2[i1]));
                    } else {
                        if (Integer.parseInt(version1[i1]) <= Integer.parseInt(version2[i1])) {
                            continue;
                        } else {
                            return -1;
                        }
                    }
                }
                return 0;
            }
        });
        return files;
    }

    @Override
    public void onClick(SmbFile smbFile) {
        new Thread() {
            @Override
            public void run() {
                File downloadFile = SmbUtils.downloadFile(UpdateActivity.this, smbFile, Environment.getExternalStorageDirectory() + "/GproDesktop/");
                AppUtils.installApk(UpdateActivity.this, downloadFile);
            }
        }.start();
    }
}

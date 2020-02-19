package com.gprotechnologies.gprodesktop.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gprotechnologies.gprodesktop.views.ProgressBarDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SmbUtils {

    public static SmbFile[] getFiles(String remoteUrl) throws MalformedURLException, SmbException {
        SmbFile smbFile = new SmbFile(remoteUrl);
        return smbFile.listFiles();
    }

    public static File downloadFile(Activity activity, SmbFile smbFile, String localDir) {
        ProgressBarDialog progressBarDialog = new ProgressBarDialog(activity);
        File localPath = new File(localDir);
        if (!localPath.exists()) {
            localPath.mkdirs();
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        File localFile = null;
        try {
            String fileName = smbFile.getName();
            localFile = new File(localDir + File.separator + fileName);
            in = new BufferedInputStream(new SmbFileInputStream(smbFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            int contentLength = smbFile.getContentLength();
            int currentLength = 0;
            int len;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                out.flush();
                buffer = new byte[1024];
                currentLength += len;
                progressBarDialog.setCurrentProgress((int) ((currentLength+.0f) / (contentLength+.0f) * 100));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            progressBarDialog.dismiss();
        }
        return localFile;
    }
}

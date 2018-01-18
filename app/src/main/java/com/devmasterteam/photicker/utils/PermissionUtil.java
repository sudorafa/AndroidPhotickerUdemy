package com.devmasterteam.photicker.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.devmasterteam.photicker.R;

/**
 * Created by oRafa on 18/01/2018.
 */

public class PermissionUtil {

    public static final int CAMERA_PERMISSION = 0;

    private static boolean needToAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static boolean hasCameraPermission(Context context) {
        if (needToAskPermission())
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    public static void asksCameraPermission(final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(activity)
                    .setMessage(activity.getString(R.string.permission_camera_explanation))
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PermissionUtil.CAMERA_PERMISSION);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionUtil.CAMERA_PERMISSION);
        }
    }
}

/*
* The MIT License (MIT)

* Copyright (c) 2015 Michal Tajchert

* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.findtech.threePomelos.musicserver;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.findtech.threePomelos.utils.ToastUtil;

/**
 * @author Administrator
 */
public class Nammu {

    private static Context context;

    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static void init(Context context) {
        Nammu.context = context.getApplicationContext();
    }


    /**
     * Not that needed method but if we override others it is good to keep same.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermission(String permissionName) {
        if (context == null) {
            throw new RuntimeException("Before comparing permissions you need to call Nammu.initCatchException(context)");
        }
        return PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permissionName);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermission(Activity activity, String[] PERMISSIONS, int requestCode, String permissionName, String notice) {

        if (!checkPermission(permissionName)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permissionName)) {
                if (notice != null) {
                    ToastUtil.showToast(context, notice);
                }
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS, requestCode
                );
            } else {
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS, requestCode
                );
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermission(Activity activity, String[] PERMISSIONS, int requestCode, String notice) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    PERMISSIONS[1])) {
                if (notice != null) {
                    ToastUtil.showToast(context, notice);
                }
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS, requestCode
                );
            } else {
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS, requestCode
                );
            }

    }


}
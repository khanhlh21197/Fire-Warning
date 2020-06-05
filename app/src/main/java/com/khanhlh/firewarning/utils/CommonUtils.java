/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.khanhlh.firewarning.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.khanhlh.firewarning.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by amitshekhar on 07/07/17.
 */

public final class CommonUtils {

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getTimestamp() {
        return new SimpleDateFormat(AppConstants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, "UTF-8");
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
//        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static void toast(Context context, int messageId) {
        ((Activity) context).runOnUiThread(() -> {
            Toast.makeText(context, context.getString(messageId), Toast.LENGTH_SHORT).show();
        });
    }

    public static void toast(Context context, String message) {
        ((Activity) context).runOnUiThread(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }

    public static boolean isNetworkConnected(Context activity) {
        boolean val = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            }
            NetworkInfo mobile = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobile != null && mobile.isAvailable() && mobile.isConnected()) {
                val = true;

                int networkType = mobile.getSubtype();
                Log.d("TAG", "networkType: " + networkType);
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
                        // 11
                        // 2G
                        // 2G
                        Toast.makeText(activity,
                                activity.getString(R.string.require_2g),
                                Toast.LENGTH_LONG).show();
                        // createAlertDialog(activity,
                        // activity.getString(R.string.require_2g),
                        // activity.getString(R.string.app_name)).show();
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by
                        // 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by
                        // 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by
                        // 15
                        // 3G
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by
                        // 13
                        // 4G
                        break;
                    default:
                        Toast.makeText(
                                activity,
                                activity.getString(R.string.require_netwotk_unknown),
                                Toast.LENGTH_LONG).show();
                        break;
                }
            } else {
                NetworkInfo wifi = connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifi != null && wifi.isAvailable() && wifi.isConnected()) {
                    Toast.makeText(activity,
                            activity.getString(R.string.require_wifi),
                            Toast.LENGTH_SHORT).show();
                    val = true;
                }
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString(), e);
        }
        Log.d("TAG", "isNetworkConnected: " + val);
        return val;
    }
}

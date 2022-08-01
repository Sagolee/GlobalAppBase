package com.sago.globalbase.push;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Method;

/**
 * xxxxx
 *
 * @author Sago丶
 * @date 2021/3/1
 */

public class FCMPushUtils {
    public static void init() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult();

            }
        });
    }


    public static void dealNotifyJump(Context context, String open_type, String open_value) {
        if (context != null) {
            collapseStatusBar(context);

        }
    }

    public static void collapseStatusBar(Context context) {
        try {
            @SuppressLint("WrongConstant")
            Object service = context.getSystemService("statusbar");
            if (null == service) {
                return;
            }
            Class<?> clazz = Class.forName("android.app.StatusBarManager");
            int sdkVersion = android.os.Build.VERSION.SDK_INT;
            Method collapse = null;
            if (sdkVersion <= 16) {
                collapse = clazz.getMethod("collapse");
            } else {
                collapse = clazz.getMethod("collapsePanels");
            }
            collapse.setAccessible(true);
            collapse.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

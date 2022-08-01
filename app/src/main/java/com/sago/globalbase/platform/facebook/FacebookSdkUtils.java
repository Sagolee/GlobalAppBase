package com.sago.globalbase.platform.facebook;

import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * xxxxx
 *
 * @author Sago丶
 * @date 2020/12/25
 */
public class FacebookSdkUtils {

    public static void init(Context context) {
        FacebookSdk.sdkInitialize(context);
    }
}

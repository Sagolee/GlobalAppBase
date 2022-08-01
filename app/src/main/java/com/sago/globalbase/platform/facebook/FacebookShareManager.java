package com.sago.globalbase.platform.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

/**
 * xxxxx
 *
 * @author Sago丶
 * @date 2021/1/11
 */

public class FacebookShareManager {
    private final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    private final String FACEBOOK_MESSENGER_PACKAGE_NAME = "com.facebook.orca";

    private final String SUPPORT_PERSON_FACEBOOK_ID = "100061109239344";
    private final String FACEBOOK_MESSENGER_LINK_ADDRESS = "fb-messenger://user/";

    private static FacebookShareManager mInstance;

    private CallbackManager mCallbackManager;

    private FacebookShareManager() {
    }

    public static FacebookShareManager instance() {
        if (mInstance == null) {
            synchronized (FacebookShareManager.class) {
                if (mInstance == null) {
                    mInstance = new FacebookShareManager();
                }
            }
        }
        return mInstance;
    }

    public void sendMessageToSupportPerson(Activity activity) {
        if (!isAppInstalled(activity, FACEBOOK_MESSENGER_PACKAGE_NAME)) {

            return;
        }
        try {
            Intent intent;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_MESSENGER_LINK_ADDRESS + SUPPORT_PERSON_FACEBOOK_ID));
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sharePic(final Activity activity, Bitmap image, FacebookCallback<Sharer.Result> callback) {
        if (!isAppInstalled(activity, FACEBOOK_PACKAGE_NAME)) {
            if (callback != null) {
                callback.onCancel();
            }
            return;
        }
        if (mCallbackManager == null) {
            mCallbackManager = CallbackManager.Factory.create();
        }
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(mCallbackManager, callback);
        if (!activity.isFinishing()) {
            //调用分享弹窗
            shareDialog.show(content);
        }
    }

    public void onShareResult(int requestCode, int resultCode, Intent data) {
        if (mCallbackManager != null) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void unRegisterShareCallback() {
        if (mCallbackManager != null) {
            LoginManager.getInstance().unregisterCallback(mCallbackManager);
        }
    }

    /**
     * @param context
     * @param apkPackageName
     * @return
     */
    private  boolean isAppInstalled(Context context, String apkPackageName) {
        if (TextUtils.isEmpty(apkPackageName)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        boolean result = false;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(apkPackageName, 0);
            if (packageInfo != null) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}

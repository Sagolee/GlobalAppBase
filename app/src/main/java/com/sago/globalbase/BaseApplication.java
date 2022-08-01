package com.sago.globalbase;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.sago.globalbase.platform.facebook.FacebookSdkUtils;
import com.sago.globalbase.push.FCMPushUtils;

import java.util.Map;

/**
 * xxxxx
 *
 * @author Sago丶
 * @date 2019-06-25
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        FacebookSdkUtils.init(context);
        initAppSFlyer(context);
        FCMPushUtils.init();
    }

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(base);
    }

    @Override
    public Resources getResources() {
        try {
            Resources res = super.getResources();
            if (res.getConfiguration().fontScale != 1) {
                Configuration config = new Configuration();
                config.setToDefaults();
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return super.getResources();
        }
    }

    private static void initFaceBookSdk(Context context) {

    }

    private static void initAppSFlyer(Context context) {
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                if(conversionData != null){
                    Object object = conversionData.get("af_status");
                    if(object instanceof String){
                        String af_status = (String) object;
                    }
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
            }
        };
        AppsFlyerLib.getInstance().init(context.getString(R.string.appsflyer_af_dev_key), conversionListener, context);
        AppsFlyerLib.getInstance().start(context);
    }
}

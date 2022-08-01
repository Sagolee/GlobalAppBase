package com.sago.globalbase.push;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;


public class FCMPushJumpActivity extends Activity {
    public static final String KEY_OPEN_TYPE = "open_type";
    public static final String KEY_OPEN_VALUE = "open_value";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dealIntent(getIntent());
        finish();
    }

    private void dealIntent(Intent intent) {
        if (intent != null) {
            String open_type = intent.getStringExtra(KEY_OPEN_TYPE);
            String open_value = intent.getStringExtra(KEY_OPEN_VALUE);

            FCMPushUtils.dealNotifyJump(this, open_type, open_value);
        }
    }
}

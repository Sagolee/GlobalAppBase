package com.sago.globalbase.platform.gms;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sago.globalbase.platform.common.LoginConstants;
import com.sago.globalbase.platform.common.LoginInfo;
import com.sago.globalbase.platform.common.ThirdLoginCallBack;

import static com.sago.globalbase.platform.common.LoginConstants.LOGIN_REQUEST_CODE;


/**
 * xxxxx
 *
 * @author Sago丶
 * @date 2020/12/22
 */
public class GoogleLoginManager {

    private static GoogleLoginManager mInstance;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions mGsOptions;

    private ThirdLoginCallBack mThirdLoginCallBack;

    private GoogleLoginManager() {
        mGsOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    public static GoogleLoginManager instance() {
        if (mInstance == null) {
            synchronized (GoogleLoginManager.class) {
                if (mInstance == null) {
                    mInstance = new GoogleLoginManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 登录
     */
    public void doLogin(Activity context, ThirdLoginCallBack thirdLoginCallBack) {
        this.mThirdLoginCallBack = thirdLoginCallBack;
        if (mGoogleSignInClient == null) {
            mGoogleSignInClient = GoogleSignIn.getClient(context, mGsOptions);
        }
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        context.startActivityForResult(signInIntent, LOGIN_REQUEST_CODE);
    }

    public boolean checkLogin(Activity context) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            return true;
        }
        return false;
    }

    public void doLoginOut(Activity context) {
        if (mGoogleSignInClient == null) {
            mGoogleSignInClient = GoogleSignIn.getClient(context, mGsOptions);
        }
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    public void onLoginResult(int requestCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                LoginInfo loginInfo = new LoginInfo();
                loginInfo.setUserid(account.getId());
                loginInfo.setNickname(account.getDisplayName());
                Uri photoUri = account.getPhotoUrl();
                if (photoUri != null) {
                    loginInfo.setFigureurl(photoUri.toString());
                }
                loginInfo.setPlatform(LoginConstants.PLATFORM_GOOGLE);
                if (mThirdLoginCallBack != null) {
                    mThirdLoginCallBack.onSuccess(loginInfo);
                }
            } catch (ApiException e) {
                if (e.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    if (mThirdLoginCallBack != null) {
                        mThirdLoginCallBack.onError(LoginConstants.PLATFORM_GOOGLE, LoginConstants.LOGIN_CANCEL, null);
                    }
                } else {
                    if (mThirdLoginCallBack != null) {
                        mThirdLoginCallBack.onError(LoginConstants.PLATFORM_GOOGLE, LoginConstants.LOGIN_ERROR, null);
                    }
                }

            }
        }
    }
}

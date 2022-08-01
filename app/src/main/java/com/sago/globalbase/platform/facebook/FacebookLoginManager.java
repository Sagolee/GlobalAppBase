package com.sago.globalbase.platform.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sago.globalbase.platform.common.LoginConstants;
import com.sago.globalbase.platform.common.LoginInfo;
import com.sago.globalbase.platform.common.ThirdLoginCallBack;


import org.json.JSONObject;

import java.util.Arrays;

/**
 * xxxxx
 *
 * @author Sago丶
 * @date 2020/12/22
 */
public class FacebookLoginManager {

    private static FacebookLoginManager mInstance;

    private CallbackManager callbackManager;
    private ThirdLoginCallBack mThirdLoginCallBack;

    private FacebookLoginManager() {
    }

    public static FacebookLoginManager instance() {
        if (mInstance == null) {
            synchronized (FacebookLoginManager.class) {
                if (mInstance == null) {
                    mInstance = new FacebookLoginManager();
                }
            }
        }
        return mInstance;
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    public void doLogin(Activity activity, ThirdLoginCallBack thirdLoginCallBack) {
        this.mThirdLoginCallBack = thirdLoginCallBack;

        registerLoginCallback();
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
    }

    private void registerLoginCallback() {
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getUserInfo(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        if (mThirdLoginCallBack != null) {
                            mThirdLoginCallBack.onError(LoginConstants.PLATFORM_FACEBOOK, LoginConstants.LOGIN_CANCEL, null);
                        }
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (mThirdLoginCallBack != null) {
                            mThirdLoginCallBack.onError(LoginConstants.PLATFORM_FACEBOOK, LoginConstants.LOGIN_ERROR, null);
                        }
                    }
                });
    }

    public void onLoginResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void unRegisterLoginCallback() {
        if (callbackManager != null) {
            LoginManager.getInstance().unregisterCallback(callbackManager);
        }
    }

    private void getUserInfo(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (object == null) {
                    if (mThirdLoginCallBack != null) {
                        mThirdLoginCallBack.onError(LoginConstants.PLATFORM_FACEBOOK, LoginConstants.LOGIN_ERROR, null);
                    }
                    return;
                }
                setLoginInfo(object);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link,gender,birthday,picture,locale,updated_time,timezone,age_range,first_name,last_name");

        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getUserPic(String facebookUserId, final OnGetUserPicCallback callback) {
        Bundle parameters = new Bundle();
        parameters.putBoolean("redirect", false);
        parameters.putString("height", "200");
        parameters.putString("type", "normal");
        parameters.putString("width", "200");
        GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + facebookUserId + "/picture", parameters, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                String avatarUrl = "";
                if (response != null) {
                    JSONObject responseJsonObject = response.getJSONObject();
                    if (responseJsonObject != null) {
                        try {
                            JSONObject dataJsonObject = responseJsonObject.getJSONObject("data");
                            avatarUrl = dataJsonObject.getString("url");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (callback != null) {
                    callback.onCompleted(avatarUrl);
                }
            }
        });
        graphRequest.executeAsync();
    }

    private void setLoginInfo(JSONObject object) {
        if (object != null && isLoggedIn()) {
            final LoginInfo loginInfo = new LoginInfo();

            String userid = object.optString("id");
            loginInfo.setUserid(userid);
            loginInfo.setNickname(object.optString("name"));
            loginInfo.setPlatform(LoginConstants.PLATFORM_FACEBOOK);
            //获取用户头像
            getUserPic(userid, new OnGetUserPicCallback() {
                @Override
                public void onCompleted(String picUrl) {
                    loginInfo.setFigureurl(picUrl);
                    if (mThirdLoginCallBack != null) {
                        mThirdLoginCallBack.onSuccess(loginInfo);
                    }
                }
            });
        } else {
            if (mThirdLoginCallBack != null) {
                mThirdLoginCallBack.onError(LoginConstants.PLATFORM_FACEBOOK, LoginConstants.LOGIN_ERROR, null);
            }
        }
    }

    interface OnGetUserPicCallback {
        void onCompleted(String picUrl);
    }
}

package com.sago.globalbase.platform.common;

/**
 * desc:
 * created by Sago
 * 2022/8/1
 */

public interface ThirdLoginCallBack {
    void onError(int platForm, int code, String errorMsg);


    /**
     * @param loginInfo          调用微信，微博，QQ授权之后的用户信息对象
     */
    void onSuccess(LoginInfo loginInfo);
}

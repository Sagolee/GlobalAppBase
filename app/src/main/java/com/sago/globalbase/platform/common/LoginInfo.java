package com.sago.globalbase.platform.common;

import java.io.Serializable;

/**
 * desc:
 * created by Sago
 * 2022/8/1
 */

public class LoginInfo implements Serializable {
    private String nickname;
    private String userid;
    private String figureurl;
    private int platform;
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFigureurl() {
        return figureurl;
    }

    public void setFigureurl(String figureurl) {
        this.figureurl = figureurl;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }
}

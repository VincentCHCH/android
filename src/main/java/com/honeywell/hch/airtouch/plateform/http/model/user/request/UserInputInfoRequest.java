package com.honeywell.hch.airtouch.plateform.http.model.user.request;

import java.io.Serializable;

/**
 * Created by zhujunyu on 2016/12/25.
 */

public class UserInputInfoRequest implements Serializable {

    private String title;
    private String feedInfo;
    private String categroy;
    private String[] imageUrl;
    private String[] imgType;

    public UserInputInfoRequest(String title, String feedInfo, String categroy, String[] imageUrl, String[] imgType) {
        this.title = title;
        this.feedInfo = feedInfo;
        this.categroy = categroy;
        this.imageUrl = imageUrl;
        this.imgType = imgType;
    }
}
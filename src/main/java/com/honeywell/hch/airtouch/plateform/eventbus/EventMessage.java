package com.honeywell.hch.airtouch.plateform.eventbus;

import android.os.Bundle;

/**
 * Created by h127856 on 4/6/17.
 */
public class EventMessage {

    //event bus message type
    private String mMessageType;

    //event bus message param
    private Bundle mParamBundle;


    public EventMessage(String messageType, Bundle bundle){
        mMessageType = messageType;
        mParamBundle = bundle;
    }

    public String getMessageType(){
        return mMessageType;
    }

    public Bundle getParams(){
        return mParamBundle;
    }

}

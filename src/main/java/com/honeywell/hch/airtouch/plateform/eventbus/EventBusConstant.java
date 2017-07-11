package com.honeywell.hch.airtouch.plateform.eventbus;

/**
 * Created by h127856 on 4/6/17.
 */

public class EventBusConstant {

    public static final String PARAM_JS_CMD = "command_param";

    public static final String DEVICE_CONTROL_CMD_MESSAGE = "device-control";

    public static final String GOTO_DEVICE_CONTROL_ACTIVITY  = "goto_devicecontrol_activity";

    public static final String FRESH_DATA = "fresh_data";

    public static final String GO_TO_BACK_ACTION = "go-to-back";

    public static final String AFTER_DEVICE_CONTROL = "after_device_control";

    public static final String END_AP_FIND_DEVICE = "end_ap_find_device";

    public static final String HTTP_JS_RESPONSE_RESULT = "request_js_response";

    public static final String SEND_CMD_RESULT = "send_cmmd_result";

    public static final String HTTP_ACTION = "http_action";

    public static final String JS_CMD_EVENT_ACTION = "js_cmd_event";



    public static final String START_ACTIVITY = "start_activity_event";

    public static final String FINISH_ACTIVITY = "finish_activity_event";

    public static final String PLUGIN_EVENT_BUS_PARAM = "plugin_event_bus_value";

    public static final String PLUGIN_EVENT_BUS = "plugin_event_bus";

    public static final String PLUGIN_MSG_KEY = "plugin_msg_key";

    public static final String CALLBACK_RESULT = "callback_result";

    public static final String CALLBACK_ID= "callback_id";

    public static final String AVIDEO_RECEIVE_CALL = "avideo_receive_call"; //向APP端推送有人call的msg
    public static final String AVIDEO_RECEIVE_END = "audio_receive_end";//当其他人已经挂断时；
    public static final String AVIDEO_ACCEPT_SELF = "audio_accept_self";//自己接听
    public static final String AVIDEO_ACCEPT_OTHER = "avideo_accept_other";//别人接听
    public static final String AVIDEO_OPEN_DOOR = "avideo_open_door";//开门


}

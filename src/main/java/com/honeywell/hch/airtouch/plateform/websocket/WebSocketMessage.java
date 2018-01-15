/******************************************************************************
 * Copyright 2011-2012 Tavendo GmbH
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.honeywell.hch.airtouch.plateform.websocket;

import java.net.URI;
import java.util.List;

import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * WebSockets message classes.
 * The master thread and the background reader/writer threads communicate using these messages
 * for WebSockets connections.
 */
public class WebSocketMessage {
    public static class WebSocketCloseCode {
        public static final int NORMAL = 1000;
        public static final int ENDPOINT_GOING_AWAY = 1001;
        public static final int ENDPOINT_PROTOCOL_ERROR = 1002;
        public static final int ENDPOINT_UNSUPPORTED_DATA_TYPE = 1003;
        public static final int RESERVED = 1004;
        public static final int RESERVED_NO_STATUS = 1005;
        public static final int RESERVED_NO_CLOSING_HANDSHAKE = 1006;
        public static final int ENDPOINT_BAD_DATA = 1007;
        public static final int POLICY_VIOLATION = 1008;
        public static final int MESSAGE_TOO_BIG = 1009;
        public static final int ENDPOINT_NEEDS_EXTENSION = 1010;
        public static final int UNEXPECTED_CONDITION = 1011;
        public static final int RESERVED_TLS_REQUIRED = 1015;
    }


    /// Base message class.
    public static class Message {
    }

    /// Quite background thread.
    public static class Quit extends Message {
    }

    /// Initial WebSockets handshake (client request).
    public static class ClientHandshake extends Message {
        private final URI mURI;
        private final URI mOrigin;
        private final String[] mSubprotocols;
        private List<BasicNameValuePair> mHeaderList = null;


        ClientHandshake(URI uri) {
            this.mURI = uri;
            this.mOrigin = null;
            this.mSubprotocols = null;
        }

        ClientHandshake(URI uri, URI origin, String[] subprotocols) {
            this.mURI = uri;
            this.mOrigin = origin;
            this.mSubprotocols = subprotocols;
        }


        public URI getURI() {
            return mURI;
        }

        public URI getOrigin() {
            return mOrigin;
        }

        public String[] getSubprotocols() {
            return mSubprotocols;
        }

        public List<BasicNameValuePair> getHeaderList(){
            return mHeaderList;
        }

        public void setHeaderList(List<BasicNameValuePair> homeList){
            mHeaderList = homeList;
        }
    }

    /// Initial WebSockets handshake (server response).
    public static class ServerHandshake extends Message {
        private boolean mSuccess;

        public ServerHandshake(boolean success) {
            mSuccess = success;
        }

        public boolean isSuccess() {
            return mSuccess;
        }
    }

    /// WebSockets connection lost
    public static class ConnectionLost extends Message {
    }

    public static class ServerError extends Message {
        private int mStatusCode;
        private String mStatusMessage;

        public ServerError(int statusCode, String statusMessage) {
            mStatusCode = statusCode;
            mStatusMessage = statusMessage;
        }

        public int getStatusCode() {
            return mStatusCode;
        }

        public String getStatusMessage() {
            return mStatusMessage;
        }
    }

    /// WebSockets reader detected WS protocol violation.
    public static class ProtocolViolation extends Message {

        private WebSocketException mException;

        public ProtocolViolation(WebSocketException e) {
            mException = e;
        }

        public WebSocketException getException() {
            return mException;
        }
    }

    /// An exception occured in the WS reader or WS writer.
    public static class Error extends Message {

        private Exception mException;

        public Error(Exception e) {
            mException = e;
        }

        public Exception getException() {
            return mException;
        }
    }

    /// WebSockets text message to send or received.
    public static class TextMessage extends Message {

        private String mPayload;

        TextMessage(String payload) {
            mPayload = payload;
        }

        public String getPayload() {
            return mPayload;
        }
    }

    /// WebSockets raw (UTF-8) text message to send or received.
    public static class RawTextMessage extends Message {

        private byte[] mPayload;

        RawTextMessage(byte[] payload) {
            mPayload = payload;
        }

        public byte[] getPayload() {
            return mPayload;
        }
    }

    /// WebSockets binary message to send or received.
    public static class BinaryMessage extends Message {

        private byte[] mPayload;

        BinaryMessage(byte[] payload) {
            mPayload = payload;
        }

        public byte[] getPayload() {
            return mPayload;
        }
    }

    /// WebSockets close to send or received.
    public static class Close extends Message {
        private int mCode;
        private String mReason;


        Close() {
            mCode = WebSocketCloseCode.UNEXPECTED_CONDITION;
            mReason = null;
        }

        Close(int code) {
            mCode = code;
            mReason = null;
        }

        Close(int code, String reason) {
            mCode = code;
            mReason = reason;
        }


        public int getCode() {
            return mCode;
        }

        public String getReason() {
            return mReason;
        }
    }

    /// WebSockets ping to send or received.
    public static class Ping extends Message {

        private byte[] mPayload;

        Ping() {
            mPayload = null;
        }

        Ping(byte[] payload) {
            mPayload = payload;
        }

        public byte[] getPayload() {
            return mPayload;
        }
    }

    /// WebSockets pong to send or received.
    public static class Pong extends Message {

        private byte[] mPayload;

        Pong() {
            mPayload = null;
        }

        Pong(byte[] payload) {
            mPayload = payload;
        }

        public byte[] getPayload() {
            return mPayload;
        }

        public void setPayload(byte[] payload) {
            mPayload = payload;
        }
    }

}

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

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.honeywell.hch.airtouch.library.LibApplication;
import com.honeywell.hch.airtouch.library.util.LogUtil;
import com.honeywell.hch.airtouch.plateform.storage.UserInfoSharePreference;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.message.BasicNameValuePair;


public class WebSocketConnection implements WebSocket {
    private static final String TAG = WebSocketConnection.class.getSimpleName();
    private static final String WS_URI_SCHEME = "ws";
    private static final String WSS_URI_SCHEME = "wss";
    private static final String WS_WRITER = "WebSocketWriter";
    private static final String WS_READER = "WebSocketReader";

    private static Handler mHandler;

    private WebSocketReader mWebSocketReader;
    private WebSocketWriter mWebSocketWriter;

    private Socket mSocket;
    private SocketManager mSocketManager;

    private URI mWebSocketURI;
    private String[] mWebSocketSubprotocols;

    private WeakReference<ConnectionHandler> mWebSocketConnectionObserver;

    private WebSocketOptions mWebSocketOptions;
    private boolean mPreviousConnection = false;

    private List<BasicNameValuePair> mWsHeaders;


    private static int index = 0;

    private List<Integer> dontReconnectCode = new ArrayList<>();



    public WebSocketConnection() {
        this.mHandler = new ThreadHandler(this);
    }

    /**
     * 根据各个业务，设定不同的错误码
     * @param dontReconnectCode
     */
    public void setDontReconnectCode(List<Integer> dontReconnectCode){
        this.dontReconnectCode = dontReconnectCode;
    }


    //
    // Forward to the writer thread
    public void sendTextMessage(String payload) {
        mWebSocketWriter.forward(new WebSocketMessage.TextMessage(payload));
    }


    public void sendRawTextMessage(byte[] payload) {
        mWebSocketWriter.forward(new WebSocketMessage.RawTextMessage(payload));
    }


    public void sendBinaryMessage(byte[] payload) {
        mWebSocketWriter.forward(new WebSocketMessage.BinaryMessage(payload));
    }

    public void sendPingMessage() {
        mWebSocketWriter.forward(new WebSocketMessage.Ping());
    }


    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected() && !mSocket.isClosed();
    }


    private void failConnection(int code, String reason) {
        Log.d(TAG, "fail connection [code = " + code + ", reason = " + reason);

        try {
            if (mWebSocketReader != null) {
                mWebSocketReader.quit();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //join需要在子线程里运行，否则会ANR
                            mWebSocketReader.join();
                        } catch (InterruptedException e) {
                            LogUtil.error(TAG, "fail connection", e);
                        }
                    }
                }).start();

            } else {
                Log.d(TAG, "mReader already NULL");
            }

            if (mWebSocketWriter != null) {
                if (mWebSocketWriter.isAlive()) {
                    mWebSocketWriter.forward(new WebSocketMessage.Quit());
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mWebSocketWriter.join();
                        } catch (Exception e) {
                            LogUtil.error(TAG, "fail connection", e);
                        }
                        try {
                            if (mSocketManager != null){
                                mSocketManager.stopConnection();
                            }
                        }catch (Exception e){
                            LogUtil.error(TAG, "fail mSocketManager", e);
                        }


                    }
                }).start();

            } else {
                Log.d(TAG, "mWriter already NULL");
            }

            Log.d(TAG, "worker threads stopped");
        } catch (Exception e) {
           Log.e(TAG,"failConnection Exception ==" +e.toString());
        }finally {
            onClose(code, reason);
        }

    }


    public void connect(URI webSocketURI, ConnectionHandler connectionObserver) throws WebSocketException {
        connect(webSocketURI, connectionObserver, new WebSocketOptions());
    }

    public void connect(URI webSocketURI, ConnectionHandler connectionObserver, WebSocketOptions options) throws WebSocketException {
        connect(webSocketURI, null, connectionObserver, options);
    }

    public void connect(URI webSocketURI, String[] subprotocols, ConnectionHandler connectionObserver, WebSocketOptions options) throws WebSocketException {
        if (isConnected()) {
            throw new WebSocketException("already connected");
        }

        if (webSocketURI == null) {
            throw new WebSocketException("WebSockets URI null.");
        } else {
            this.mWebSocketURI = webSocketURI;
            if (!mWebSocketURI.getScheme().equals(WS_URI_SCHEME) && !mWebSocketURI.getScheme().equals(WSS_URI_SCHEME)) {
                throw new WebSocketException("unsupported scheme for WebSockets URI");
            }

            this.mWebSocketSubprotocols = subprotocols;
            this.mWebSocketConnectionObserver = new WeakReference<ConnectionHandler>(connectionObserver);
            this.mWebSocketOptions = new WebSocketOptions(options);

            connect();
        }
    }

    public void connect(String wsUri, String[] wsSubprotocols, WebSocket.ConnectionHandler wsHandler, WebSocketOptions options, List<BasicNameValuePair> headers) throws WebSocketException {
        try {
            index = 0;
            this.mWebSocketURI = new URI(wsUri);
            if (!mWebSocketURI.getScheme().equals(WS_URI_SCHEME) && !mWebSocketURI.getScheme().equals(WSS_URI_SCHEME)) {
                throw new WebSocketException("unsupported scheme for WebSockets URI");
            }
            this.mWebSocketSubprotocols = wsSubprotocols;
            this.mWebSocketConnectionObserver = new WeakReference<ConnectionHandler>(wsHandler);
            this.mWebSocketOptions = new WebSocketOptions(options);
            mWsHeaders = headers;
            connect();
        } catch (Exception e) {
            LogUtil.error(TAG, "connect", e);
        }
    }


    public void disconnect() {
        if (mWebSocketWriter != null && mWebSocketWriter.isAlive()) {
            mWebSocketWriter.forward(new WebSocketMessage.Close());
        } else {
            Log.d(TAG, "Could not send WebSocket Close .. writer already null");
        }

        this.mPreviousConnection = false;
        this.mConsumeThreadRunning = false;
        concurrentLinkedQueue.clear();
    }

    /**
     * Reconnect to the server with the latest options
     *
     * @return true if reconnection performed
     */
    public boolean reconnect() {
        if (!isConnected() && (mWebSocketURI != null)) {

            try {
                List<String> urlStrings = new ArrayList<>();
                urlStrings.addAll(UserInfoSharePreference.getWsUrl());
                index = (index + 1) % urlStrings.size();
                mWebSocketURI = new URI(urlStrings.get(index));
                Log.i(TAG,urlStrings.get(index));
            } catch (Exception e) {

            }

            connect();
            return true;
        }
        return false;
    }


    private void connect() {


        mSocketManager = new SocketManager(mWebSocketURI, mWebSocketOptions);

        mSocketManager.startConnection();


        this.mSocket = mSocketManager.getSocket();

        if (mSocket == null) {
            onClose(ConnectionHandler.WebSocketCloseNotification.CANNOT_CONNECT.ordinal(), mSocketManager.getFailureMessage());
        } else if (mSocket.isConnected()) {
            try {
                createReader();
                createWriter();

                WebSocketMessage.ClientHandshake clientHandshake = new WebSocketMessage.ClientHandshake(mWebSocketURI, null, mWebSocketSubprotocols);
                clientHandshake.mHeaderList = mWsHeaders;
                mWebSocketWriter.forward(clientHandshake);
            } catch (Exception e) {
                onClose(ConnectionHandler.WebSocketCloseNotification.INTERNAL_ERROR.ordinal(), e.getLocalizedMessage());
                LogUtil.error(TAG, "connect", e);
            }
        } else {
            onClose(ConnectionHandler.WebSocketCloseNotification.CANNOT_CONNECT.ordinal(), "could not connect to WebSockets server");
        }


    }

    private ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
    private boolean mConsumeThreadRunning = true;
    private Thread mConsumeThread = null;
    /**
     * Perform reconnection
     *
     * @return true if reconnection was scheduled
     */
    protected void scheduleReconnect() {
        /**
         * Reconnect only if:
         *  - connection active (connected but not disconnected)
         *  - has previous success connections
         *  - reconnect interval is set
         */
        consumeLinkedQueue();
        final int interval = mWebSocketOptions.getReconnectInterval();
//        boolean shouldReconnect =
////                mSocket != null
////                && mSocket.isConnected()
////                && mPreviousConnection
////                &&
//                (interval > 0);

        if (UserInfoSharePreference.isUserAccountHasData()) {
            concurrentLinkedQueue.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "WebSocket reconnecting...");
                    try {
                        Thread.sleep(interval);
                    } catch (Exception e) {

                    }
                    reconnect();
                }
            }));

            Log.d(TAG, "WebSocket reconnection scheduled");

        }
    }

    private void consumeLinkedQueue(){
        if (mConsumeThread == null){
            mConsumeThread  =  new Thread(new Runnable() {
                @Override
                public void run() {
                    while(mConsumeThreadRunning){
                        try{
                            if (!concurrentLinkedQueue.isEmpty()){
                                Log.e(TAG, "WebSocket consumeLinkedQueue...");

                                Thread thread = (Thread) concurrentLinkedQueue.poll();
                                thread.start();
                            }

                            Thread.sleep(500);
                        }catch (Exception e){

                        }

                    }
                }
            });
            mConsumeThread.start();
        }


    }

    /**
     * Common close handler
     *
     * @param code   Close code.
     * @param reason Close reason (human-readable).
     */
    private void onClose(int code, String reason) {
        Log.e(TAG,"WebSocketCloseNotification code = " + code);
        if (dontReconnectCode != null && !dontReconnectCode.contains(code)) {
            scheduleReconnect();
        }

        ConnectionHandler webSocketObserver = mWebSocketConnectionObserver.get();
        if (webSocketObserver != null) {
            try {
                webSocketObserver.onClose(code, reason);
            } catch (Exception e) {
                LogUtil.error(TAG, "onClose", e);
            }
        } else {
            Log.d(TAG, "WebSocketObserver null");
        }
    }


    protected void processAppMessage(Object message) {
    }


    /**
     * Create WebSockets background writer.
     */
    protected void createWriter() {
        mWebSocketWriter = new WebSocketWriter(mHandler, mSocket, mWebSocketOptions, WS_WRITER);
        mWebSocketWriter.start();

        synchronized (mWebSocketWriter) {
            try {
                mWebSocketWriter.wait();
            } catch (InterruptedException e) {
            }
        }
    }


    /**
     * Create WebSockets background reader.
     */
    protected void createReader() {

        mWebSocketReader = new WebSocketReader(mHandler, mSocket, mWebSocketOptions, WS_READER);
        mWebSocketReader.start();

        synchronized (mWebSocketReader) {
            try {
                mWebSocketReader.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    private void handleMessage(Message message) {
        ConnectionHandler webSocketObserver = mWebSocketConnectionObserver.get();
        if (message.obj instanceof WebSocketMessage.TextMessage) {
            WebSocketMessage.TextMessage textMessage = (WebSocketMessage.TextMessage) message.obj;

            if (webSocketObserver != null) {
                webSocketObserver.onTextMessage(textMessage.mPayload);
            } else {
                Log.d(TAG, "could not call onTextMessage() .. handler already NULL");
            }

        } else if (message.obj instanceof WebSocketMessage.RawTextMessage) {
            WebSocketMessage.RawTextMessage rawTextMessage = (WebSocketMessage.RawTextMessage) message.obj;

            if (webSocketObserver != null) {
                webSocketObserver.onRawTextMessage(rawTextMessage.mPayload);
            } else {
                Log.d(TAG, "could not call onRawTextMessage() .. handler already NULL");
            }

        } else if (message.obj instanceof WebSocketMessage.BinaryMessage) {
            WebSocketMessage.BinaryMessage binaryMessage = (WebSocketMessage.BinaryMessage) message.obj;

            if (webSocketObserver != null) {
                webSocketObserver.onBinaryMessage(binaryMessage.mPayload);
            } else {
                Log.d(TAG, "could not call onBinaryMessage() .. handler already NULL");
            }

        } else if (message.obj instanceof WebSocketMessage.Ping) {
            WebSocketMessage.Ping ping = (WebSocketMessage.Ping) message.obj;
            Log.d(TAG, "WebSockets Ping received");

            WebSocketMessage.Pong pong = new WebSocketMessage.Pong();
            pong.mPayload = ping.mPayload;
            mWebSocketWriter.forward(pong);

        } else if (message.obj instanceof WebSocketMessage.Pong) {
            WebSocketMessage.Pong pong = (WebSocketMessage.Pong) message.obj;

            Log.d(TAG, "WebSockets Pong received" + pong.mPayload);

        } else if (message.obj instanceof WebSocketMessage.Close) {
            WebSocketMessage.Close close = (WebSocketMessage.Close) message.obj;

            Log.d(TAG, "WebSockets Close received (" + close.getCode() + " - " + close.getReason() + ")");

            mWebSocketWriter.forward(new WebSocketMessage.Close(WebSocketMessage.WebSocketCloseCode.NORMAL));

        } else if (message.obj instanceof WebSocketMessage.ServerHandshake) {
            WebSocketMessage.ServerHandshake serverHandshake = (WebSocketMessage.ServerHandshake) message.obj;

            Log.d(TAG, "opening handshake received");

            if (serverHandshake.mSuccess) {
                if (webSocketObserver != null) {
                    webSocketObserver.onOpen();
                } else {
                    Log.d(TAG, "could not call onOpen() .. handler already NULL");
                }
                mPreviousConnection = true;
            }

        } else if (message.obj instanceof WebSocketMessage.ConnectionLost) {
            //			WebSocketMessage.ConnectionLost connectionLost = (WebSocketMessage.ConnectionLost) message.obj;
            failConnection(ConnectionHandler.WebSocketCloseNotification.CONNECTION_LOST.ordinal(), "WebSockets connection lost");

        } else if (message.obj instanceof WebSocketMessage.ProtocolViolation) {
            //			WebSocketMessage.ProtocolViolation protocolViolation = (WebSocketMessage.ProtocolViolation) message.obj;
            failConnection(((WebSocketMessage.ProtocolViolation)message.obj).mException.getErrorCode(), "WebSockets protocol violation");

        } else if (message.obj instanceof WebSocketMessage.Error) {
            WebSocketMessage.Error error = (WebSocketMessage.Error) message.obj;
            failConnection(ConnectionHandler.WebSocketCloseNotification.INTERNAL_ERROR.ordinal(), "WebSockets internal error (" + error.mException.toString() + ")");

        } else if (message.obj instanceof WebSocketMessage.ServerError) {
            WebSocketMessage.ServerError error = (WebSocketMessage.ServerError) message.obj;
            failConnection(ConnectionHandler.WebSocketCloseNotification.SERVER_ERROR.ordinal(), "Server error " + error.mStatusCode + " (" + error.mStatusMessage + ")");

        } else {
            processAppMessage(message.obj);
        }
    }


    public static class SocketManager {
        private static final String WS_CONNECTOR = "WebSocketConnector";

        private final URI mWebSocketURI;

        private Socket mSocket = null;
        private String mFailureMessage = null;

        private Handler mHandler;


        public SocketManager(URI uri, WebSocketOptions options) {

            this.mWebSocketURI = uri;
        }


        public void startConnection() {
            try {
                String host = mWebSocketURI.getHost();
                int port = mWebSocketURI.getPort();

                if (port == -1) {
                    if (mWebSocketURI.getScheme().equals(WSS_URI_SCHEME)) {
                        port = 443;
                    } else {
                        port = 80;
                    }
                }

                SocketFactory factory = null;
                if (mWebSocketURI.getScheme().equalsIgnoreCase(WSS_URI_SCHEME)) {
                    String cerFileName =  mWebSocketURI.toString().contains("wss://homecloud.honeywell.com.cn") ? "GeoTrust_Global_CA.PEM" : "qa.cer";
                    factory = getProductCertificatesFactory(cerFileName);
                    Log.e("haha", "factory === " + factory);
                    if (factory == null) {
                        SSLContext sc = SSLContext.getInstance("TLSv1.2");
                        sc.init(null, null, null);
                        factory = new Tls12SocketFactory(sc.getSocketFactory());
                    }

                } else {
                    factory = SocketFactory.getDefault();
                }

                // Do not replace host string with InetAddress or you lose automatic host name verification
                this.mSocket = factory.createSocket(host, port);
            } catch (IOException e) {
                this.mFailureMessage = e.getLocalizedMessage();
                LogUtil.error(TAG, "startConnection", e);
            } catch (NoSuchAlgorithmException e) {
                LogUtil.error(TAG, "startConnection", e);
            } catch (KeyManagementException e) {
                LogUtil.error(TAG, "startConnection", e);
            }

            synchronized (this) {
                notifyAll();
            }
        }


        private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型 固定值

        private SSLSocketFactory getProductCertificatesFactory(String cerFileName) {
            InputStream ksIn = null;

            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                ksIn = LibApplication.getContext().getResources().getAssets().open(cerFileName);
                CertificateFactory cerFactory = CertificateFactory.getInstance("X.509", "BC");
                Certificate cer = cerFactory.generateCertificate(ksIn);


                //创建一个证书库，并将证书导入证书库
                KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("trust", cer);


                TrustManagerFactory trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                final X509TrustManager origTrustmanager = (X509TrustManager) trustManagers[0];
                TrustManager[] wrappedTrustManagers = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return origTrustmanager.getAcceptedIssuers();
                            }

                            public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                                try {
                                    origTrustmanager.checkClientTrusted(certs, authType);
                                } catch (CertificateException e) {
                                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, e.toString());
                                }
                            }

                            public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                                if (certs == null || certs.length == 0) {
                                    throw new IllegalArgumentException("certificate is null or empty");
                                }
                                if (authType == null || authType.length() == 0) {
                                    throw new IllegalArgumentException("authtype is null or empty");
                                }
                                try {
                                    origTrustmanager.checkServerTrusted(certs, authType);
                                } catch (CertificateException e) {
                                    throw new CertificateException("certificate is not trust");
                                }
                            }
                        }
                };

                sslContext.init(null, wrappedTrustManagers, new SecureRandom());

                return new Tls12SocketFactory(sslContext.getSocketFactory());

            } catch (CertificateException e) {
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, e.toString());
            } catch (Exception e) {
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, e.toString());
            } finally {
                try {
                    if (ksIn != null) {
                        ksIn.close();
                    }
                } catch (Exception e) {
                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, e.toString());
                }

            }
            return null;

        }

        public void stopConnection() {
            try {
                mSocket.close();
                this.mSocket = null;

            } catch (Exception e) {
                this.mFailureMessage = e.getLocalizedMessage();
            }
        }


        public Socket getSocket() {
            return mSocket;
        }

        public String getFailureMessage() {
            return mFailureMessage;
        }
    }


    private static class ThreadHandler extends Handler {
        private final WeakReference<WebSocketConnection> mWebSocketConnection;


        public ThreadHandler(WebSocketConnection webSocketConnection) {
            super();
            this.mWebSocketConnection = new WeakReference<WebSocketConnection>(webSocketConnection);
        }


        @Override
        public void handleMessage(Message message) {
            if (mWebSocketConnection != null) {
                WebSocketConnection webSocketConnection = mWebSocketConnection.get();
                if (webSocketConnection != null) {
                    webSocketConnection.handleMessage(message);
                }
            }

        }
    }
}

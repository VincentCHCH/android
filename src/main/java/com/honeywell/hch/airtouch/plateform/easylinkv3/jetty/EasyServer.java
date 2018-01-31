package com.honeywell.hch.airtouch.plateform.easylinkv3.jetty;

import com.honeywell.hch.airtouch.plateform.easylinkv3.helper.EasyLinkCallBack;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


/**
 * Created by SIN on 2017/1/13.
 */
public class EasyServer {
    private int mPort;
    public Server mServer;

    public EasyServer(int port) {
        mPort = port;
    }

    public synchronized void start(EasyLinkCallBack easylinkcb) {
        if ((mServer != null) && (mServer.isStarted())) {
            return;
        }
        if (mServer == null) {
            ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletHandler.addServlet(new ServletHolder(new EasyServlet(easylinkcb)), "/auth-setup");

            HandlerList handlerList = new HandlerList();
            handlerList.addHandler(servletHandler);
            mServer = new Server(mPort);
            mServer.setHandler(handlerList);
        }

        try {
            mServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        if ((mServer == null) || (mServer.isStopped())) {
            return;
        }
        try {
//            Thread.sleep(3000);
            mServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean isStarted() {
        if (mServer == null) {
            return false;
        }
        return mServer.isStarted();
    }
}


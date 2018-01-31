package com.honeywell.hch.airtouch.plateform.easylinkv3.jetty;


import com.honeywell.hch.airtouch.plateform.easylinkv3.helper.ComHelper;
import com.honeywell.hch.airtouch.plateform.easylinkv3.helper.EasyLinkCallBack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.honeywell.hch.airtouch.plateform.easylinkv3.helper.EasyLinkErrCode.CALLBACK_CODE;


/**
 * Created by SIN on 2017/1/13.
 */
public class EasyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
//    public static final String TAG = "EasyServlet";
    private EasyLinkCallBack elcb;
    private ComHelper comfunc = new ComHelper();

    public EasyServlet(EasyLinkCallBack easylinkcb) {
        elcb = easylinkcb;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        PrintWriter out = resp.getWriter();
        out.println("{}");
        out.flush();

        String msg = readFully(req.getInputStream(), "utf8");
        comfunc.successCBEasyLink(CALLBACK_CODE, msg, elcb);
    }

    public String readFully(InputStream inputStream, String encoding) throws IOException {
        return new String(readFully(inputStream), encoding);
    }

    private byte[] readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }
}

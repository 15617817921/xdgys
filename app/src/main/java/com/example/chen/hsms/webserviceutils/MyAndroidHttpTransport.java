package com.example.chen.hsms.webserviceutils;

import com.example.chen.hsms.webservice.ServiceConnectionSE;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

import java.io.IOException;

/**
 * Created by Administrator on 2018/3/1.
 */

public class MyAndroidHttpTransport extends HttpTransportSE {

    private int timeout = 30000; //默认超时时间为30s

    public MyAndroidHttpTransport(String url) {
        super(url);
    }

    public MyAndroidHttpTransport(String url, int timeout) {
        super(url);
        this.timeout = timeout;
    }

    protected ServiceConnection getServiceConnection(String url) throws IOException {
        ServiceConnectionSE serviceConnection = new ServiceConnectionSE(url);
        serviceConnection.setConnectionTimeOut(timeout);
        return new ServiceConnectionSE(url);
    }
}

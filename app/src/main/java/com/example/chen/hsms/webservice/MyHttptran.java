package com.example.chen.hsms.webservice;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

import java.io.IOException;

public class MyHttptran extends HttpTransportSE {
	private int timeout = 10000;

	public MyHttptran(String url) {
		super(url);
		// TODO Auto-generated constructor stub
	}

	public MyHttptran(String url, int timeout) {
		super(url);
		this.timeout = timeout;
	}

	protected ServiceConnection getServiceConnection(String url)
			throws IOException {
		ServiceConnectionSE serviceConnection = new ServiceConnectionSE(url);
		serviceConnection.setConnectionTimeOut(timeout);
		return new ServiceConnectionSE(url);
	}
}

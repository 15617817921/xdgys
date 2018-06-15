package com.example.chen.hsms.webservice;
    
import org.ksoap2.transport.ServiceConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
    
public class ServiceConnectionSE   
  implements ServiceConnection   
{   
  private HttpURLConnection connection;   
    
  public ServiceConnectionSE(String url)   
    throws IOException   
  {   
    this.connection = ((HttpURLConnection)new URL(url).openConnection());   
    this.connection.setUseCaches(false);   
    this.connection.setDoOutput(true);   
    this.connection.setDoInput(true);   
  }   
    
  public void connect() throws IOException {   
    this.connection.connect();   
  }   
    
  public void disconnect() {   
    this.connection.disconnect();   
  }   
    
  public void setRequestProperty(String string, String soapAction) {   
    this.connection.setRequestProperty(string, soapAction);   
  }   
    
  public void setRequestMethod(String requestMethod) throws IOException {   
    this.connection.setRequestMethod(requestMethod);   
  }   
    
  public OutputStream openOutputStream() throws IOException {   
    return this.connection.getOutputStream();   
  }   
    
  public InputStream openInputStream() throws IOException {   
    return this.connection.getInputStream();   
  }   
    
  public InputStream getErrorStream() {   
    return this.connection.getErrorStream();   
  }   
     
  //�������ӷ������ĳ�ʱʱ��,���뼶,��Ϊ�Լ���ӵķ���   
  public void setConnectionTimeOut(int timeout){   
      this.connection.setConnectTimeout(timeout);   
  }   
}   
package com.example.chen.hsms.ws;

/**
 * Created by CHEN on 2017-6-14.
 */

public class WSAgent {
    public static String LoginUserID;
    public static String LoginUserName;
    public static String ExeDir;
    /// <summary>
    /// 登陆科室类别 1供应室  2其他科室
    /// </summary>
    public static int LoginKeShiType;
    public static String LoginUserKeShiID;
    public static String LoginUserKeShiMC;
    public static int ClientType = 20;
    public static String ClientVersion = "1.0.0.0";
    /// <summary>
    /// 获取客户端信息
    /// </summary>
    /// <returns></returns>
    public static ClientInfo GetClientInfo()
    {
        ClientInfo clientInfo = new ClientInfo();
//        clientInfo.ClientType = ClientType;
//        clientInfo.ClientVersion = ClientVersion;
//        clientInfo.InvokeUserID = LoginUserID;
        clientInfo.setProperty(0,10);
        clientInfo.setProperty(1,"0.0.0.0");
        clientInfo.setProperty(2,"guest");
        clientInfo.setProperty(3,"");
        return clientInfo;
    }
}

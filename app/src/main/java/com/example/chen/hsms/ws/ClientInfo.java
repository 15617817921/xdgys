package com.example.chen.hsms.ws;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by CHEN on 2017-6-14.
 */

public class ClientInfo implements KvmSerializable {


    public ClientInfo()
    {
        this.ClientType = 10;
        this.ClientVersion = "0.0.0.0";
        this.InvokeUserID = "guest";
        this.Reserved = "";
    }

    /// <summary>
    /// 客户端类型 10:PC  20:PPC  -1:UnKnown
    /// </summary>
    public int ClientType;
    /// <summary>
    /// 客户端版本
    /// </summary>
    public String ClientVersion;
    /// <summary>
    /// 调用者ID
    /// </summary>
    public String InvokeUserID;
    /// <summary>
    /// 保留字段
    /// </summary>
    public String Reserved;

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return ClientType;
            case 1:
                return ClientVersion;
            case 2:
                return InvokeUserID;
            case 3:
                return Reserved;
            default:
                break;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 0;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                ClientType = (int)o;
                break;
            case 1:
                ClientVersion = o.toString();
                break;
            case 2:
                InvokeUserID =  o.toString();
                break;
            case 3:
                Reserved = o.toString();
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "CarId";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Cost";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "Dates";
                break;
            default:
                break;
        }
    }
}

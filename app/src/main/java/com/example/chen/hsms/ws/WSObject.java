package com.example.chen.hsms.ws;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by CHEN on 2017-6-14.
 */

public class WSObject implements KvmSerializable{

    /// <summary>
    /// string值域
    /// </summary>
    public String _strValue;
    /// <summary>
    /// 整形值域
    /// </summary>
    public int _intValue;
    /// <summary>
    /// 值类型
    /// </summary>
    public String _valueType;


    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return _strValue;
            case 1:
                return _intValue;
            case 2:
                return _valueType;
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
                _strValue = o.toString();
                break;
            case 1:
                _intValue = (int)o;
                break;
            case 2:
                _valueType =  o.toString();
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

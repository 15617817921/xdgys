package com.example.chen.hsms.ws;

/**
 * Created by CHEN on 2017-6-15.
 */

public class Hsms {

    /// <summary>
    /// 操作返回标志ID
    /// </summary>
    public String ReturnID;
    /// <summary>
    /// 操作返回标志 0 成功  -1失败
    /// </summary>
    public int ReturnCode;
    /// <summary>
    /// 返回信息 失败时为错误提示信息
    /// </summary>
    public String ResturnMsg;
    /// <summary>
    /// 返回数据json
    /// </summary>
    public String ReturnJson;

    public String getReturnID() {
        return ReturnID;
    }

    public void setReturnID(String returnID) {
        ReturnID = returnID;
    }

    public int getReturnCode() {
        return ReturnCode;
    }

    public void setReturnCode(int returnCode) {
        ReturnCode = returnCode;
    }

    public String getResturnMsg() {
        return ResturnMsg;
    }

    public void setResturnMsg(String resturnMsg) {
        ResturnMsg = resturnMsg;
    }

    public String getReturnJson() {
        return ReturnJson;
    }

    public void setReturnJson(String returnJson) {
        ReturnJson = returnJson;
    }
}

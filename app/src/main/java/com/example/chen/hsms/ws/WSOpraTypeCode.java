package com.example.chen.hsms.ws;

/**
 * Created by CHEN on 2017-6-14.
 */

public class WSOpraTypeCode {
    //根据id查追踪记录
    public static final String 通过清洗机ID查看清洗记录 = "7628";
    public static final String 通过消毒锅ID查看消毒物品包 = "7629";

    //缓存类
    public static final String 获取用户缓存信息  = "1007";


    public static final String 添加用户 = "1000";
    public static final String 验证用户 = "1004";
    public static final String 获取用户信息 = "1003";

    public static final String 获取基础数据明细 = "1113";
    //清点
    public static final String 执行查询SQL语句 = "10";
    public static final String 获取回收单2 = "8505";
    public static final String 获取制包单明细 = "7703";
    public static final String 获取回收单对应明细 = "8506";

    //His数据
    public static final String 获取His回收单 = "8512";
    //清洗
    public static final String 获取清洗机 = "9000";

    public static final String 获取物品包追踪记录_同批次物品 = "7604";
    public static final String 获取物品包清洗记录_同批次物品 = "7606";
    public static final String 修改物品包清洗记录_同批次物品 = "7608";

    public static final String 添加清洗机 = "9002";

    public static final String 获取物品追踪记录照片 = "7620";
    //清洗篮
    public static final String 物品包修改清洗篮 = "7612";
    public static final String 物品包绑定清洗篮 = "7613";
    public static final String 物品包解绑清洗篮 = "7614";
    public static final String 获取清洗篮中物品包 = "7615";

    //清洗机里面详细记录
    public static final String 通过清洗机查看清洗记录 = "7617";

    public static final String 获取物品包追踪记录_旧条码号 = "7611";

    public static final String 清洗篮绑定清洗架 = "7618";
    public static final String 清空清洗架 = "7619";


    public static final String 获取旧条码生成的新追踪记录 = "7616";//
    public static final String 获取首次生成物品包 = "7631";
    public static final String 绑定旧物品包和新条码 = "7632";

    //   灭菌
    public static final String 增加消毒锅 = "1600";

    public static final String 获取消毒车内物品 = "7622";


    public static final String 获取未审核回收单明细 = "8511";

    //制包提交
    public static final String 更新回收单和明细 = "8509";
    public static final String 获取回收单对应明细和物品包明细 = "8508";
    public static final String 制包单审核制包 = "7506";

    //拍照
    public static final String 物品包拍照保存 = "7621";


    public static final String 根据排班表生成制包单 = "5004";

    public static final String 获取请领单 = "8003";
    public static final String 获取请领单2 = "8004";

    /// <summary>
    /// (包括出库物品和出库物品明细)
    /// </summary>
    public static final String 获取物品分类 = "1303";
    public static final String 获取消毒锅 = "1603";
    public static final String 添加灭菌记录 = "1604";

    public static final String 获取消毒包裹材料 = "1703";

    public static final String 查找病人 = "1804";
    public static final String 获取病人 = "1803";
    public static final String 添加病人 = "1800";

    public static final String 获取服务器时间 = "00";
    public static final String 获取系统参数 = "20";
    public static final String 执行查询Sql语句 = "10";
    public static final String 执行更新Sql语句_单条 = "11";
    public static final String 执行更新Sql语句_多条 = "12";

    public static final String 获取客户端信息 = "25";
    public static final String 增加客户端 = "26";

    public static final String 写入系统日志 = "30";
    public static final String 写入操作日志 = "31";

    public static final String 发送消息 = "40";
    public static final String 接收消息 = "41";
    public static final String 更新消息状态 = "42";

    //内部 接口 调用PC端存储过程
    public static final String 获取新生成物品包追踪记录 = "7630";

    public static final String 提交制包单 = "7500";
    public static final String 获取制包计划单 = "7503";
    public static final String 获取物品包明细 = "1403";
    public static final String 获取物品包 = "1503";

    public static final String 获取物品包追踪记录 = "7603";
    public static final String 修改物品包追踪记录 = "7602";

    public static final String 获取物品包追踪记录_同批次同物品 = "7604";

    public static final String 添加清洗配件 = "9009";//detset¤+清洗记录json
    public static final String 删除清洗配件 = "9008";
    public static final String 获取清洗配件 = "9007";//string¤where type=1篮 2是架 and isuse=0是空的，1是是再用的

    public static final String 提交发放单 = "7800";
    public static final String 审核发放单 = "7806";

    public static final String 获取发放单 = "7803";
    public static final String 获取发放单明细 = "7903";
    public static final String 获取发放单明细2 = "7904";


    public static final String 获取自备包 = "8103";
    public static final String 更新自备包 = "8102";

    public static final String 增加器械报损单 = "8200";
    public static final String 删除器械报损单 = "8201";
    public static final String 修改器械报损单 = "8202";
    public static final String 获取器械报损单 = "8203";

    public static final String 提交回收单 = "8502";

    public static final String 内部接口_存放物品包 = "II1000";
    public static final String 内部接口_发放物品包 = "II1001";
    public static final String 内部接口_回收物品包 = "II1002";
    public static final String 内部接口_报损器械 = "II1003";
}

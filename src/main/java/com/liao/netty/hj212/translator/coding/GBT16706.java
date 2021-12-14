package com.szewec.netty.hj212.translator.coding;

import com.szewec.netty.hj212.translator.CodeMean;

/**
 * ?? http://www.mep.gov.cn/image20010518/1967.pdf
 * Created by xiaoyao9184 on 2017/12/30.
 */
public enum GBT16706 implements CodeMean {

    _21("地表水质量监测"),
    _22("空气质量监测"),
    _23("声环境质量监测"),
    _24("地下水质量监测"),
    _25("土壤质量监测"),
    _26("海水质量监测"),
    _27("挥发性有机物监测"),

    _31("大气环境污染源"),
    _32("地表水体环境污染源"),
    _33("地下水体环境污染源"),
    _34("海洋环境污染源"),
    _35("土壤环境污染源"),
    _36("声环境污染源"),
    _37("振动环境污染源"),
    _38("放射性环境污染源"),
    _39("工地扬尘污染源"),

    _41("电磁环境污染源"),

    _51("烟气排放过程监控"),
    _52("污水排放过程监控"),

    _91("系统交互");


    private String code;
    private String meaning;

    GBT16706(String meaning){
        this.code = name().substring(1);
        this.meaning = meaning;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String mean() {
        return meaning;
    }
}

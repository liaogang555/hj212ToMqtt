package com.szewec.netty.hj212.format.hbt212.model.mixin;

import com.szewec.netty.hj212.format.hbt212.model.Data;
import com.szewec.netty.hj212.format.hbt212.model.DataFlag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * 混合
 * 解决Map转为
 * @see Data 时剔除的字段
 * Created by xiaoyao9184 on 2017/12/19.
 */
@Deprecated
public abstract class DataMixin {

    @JsonIgnore
    abstract String getDataFlag();

    @JsonIgnore
    abstract void setDataFlag(List<DataFlag> dataFlag);

    @JsonIgnore
    abstract String getCp();

    @JsonIgnore
    abstract void setCp(String cp);
}

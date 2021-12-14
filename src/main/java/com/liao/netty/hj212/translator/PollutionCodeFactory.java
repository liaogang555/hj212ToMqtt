package com.szewec.netty.hj212.translator;

import com.szewec.netty.hj212.translator.coding.PollutionGas;
import com.szewec.netty.hj212.translator.coding.PollutionNoise;
import com.szewec.netty.hj212.translator.coding.PollutionWater;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PollutionCodeFactory {

    private static final PollutionCodeFactory INSTANCE = new PollutionCodeFactory();

    private PollutionCodeFactory() {
    }

    public static PollutionCodeFactory getInstance() {
        return INSTANCE;
    }

    public Class<? extends Enum> getCodeMean(String systemCode) {
        switch (systemCode) {
            case "22":
                return PollutionGas.class;
            case "23":
                return PollutionNoise.class;
            case "24":
                return PollutionWater.class;
            default:
                log.info("systemCode {} not defined, please check!", systemCode);
        }
        return null;
    }

}

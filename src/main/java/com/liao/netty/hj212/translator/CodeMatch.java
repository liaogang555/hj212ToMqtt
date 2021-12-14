package com.szewec.netty.hj212.translator;

public interface CodeMatch extends CodePattern {

    boolean match(String code);
}

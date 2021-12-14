package com.szewec.netty.hj212.translator.coding;

import com.szewec.netty.hj212.translator.CodeMatch;
import com.szewec.netty.hj212.translator.CodeMean;
import com.szewec.netty.hj212.translator.CodePattern;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 系统类别
 * 6.6.1.1 类别划分
 * Created by xiaoyao9184 on 2017/12/30.
 */
public enum SystemType implements CodeMean, CodePattern, CodeMatch {

    _10_29("环境质量类别","[12]\\d"),
    _30_49("环境污染源类别","[34]\\d"),
    _50_69("工况类别","[56]\\d"),
    _91_99("系统交互类别","[9][1-9]"),
    _A0_Z9("于未知系统编码扩展","[A-Z][A-Z0-9]|[A-Z0-9][A-Z]"),
    _UNKNOW("未知","[0-9A-Z]{2}",10000);

    private String code;
    private String meaning;
    private String pattern;
    private Predicate<String> predicate;
    private int order;

    SystemType(String meaning,String pattern){
        this.code = name().substring(1);
        this.meaning = meaning;
        this.pattern = pattern;
        this.predicate = Pattern.compile(this.pattern).asPredicate();
        this.order = ordinal();
    }

    SystemType(String meaning,String pattern,int order){
        this.code = name().substring(1);
        this.meaning = meaning;
        this.pattern = pattern;
        this.predicate = Pattern.compile(this.pattern).asPredicate();
        this.order = order;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String mean() {
        return meaning;
    }

    @Override
    public String pattern() {
        return pattern;
    }

    @Override
    public boolean match(String code) {
        return predicate.test(code);
    }

    @Override
    public int order() {
        return order;
    }

}

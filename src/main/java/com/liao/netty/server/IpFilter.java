package com.szewec.netty.server;

import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IpFilter implements IpFilterRule {

    private List<String> filterIp;

    public IpFilter(List<String> filterIp) {
        this.filterIp = filterIp;
    }

    @Override
    public boolean matches(InetSocketAddress remoteAddress) {
        if (filterIp.contains(remoteAddress.getHostString())){
            return true;
        }
        return false;
    }

    @Override
    public IpFilterRuleType ruleType() {
        return IpFilterRuleType.REJECT;
    }
}

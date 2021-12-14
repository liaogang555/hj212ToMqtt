package com.szewec.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.util.CharsetUtil;

/**
 * @author pjmike
 * @create 2018-10-24 15:20
 */
public class NettyServerHandlerInitializer extends ChannelInitializer<Channel> {

    private HJ212Handler handler;

    public NettyServerHandlerInitializer(HJ212Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //空闲检测
                .addLast(new ServerIdleStateHandler())
                .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                .addLast(new NettyServerHandler(handler))
                .addLast(new RuleBasedIpFilter(new IpFilter(handler.getFilterIps())));
    }
}

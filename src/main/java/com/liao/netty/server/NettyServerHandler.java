package com.szewec.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pjmike
 * @create 2018-10-24 15:43
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

    private HJ212Handler hj212Handler;

    public NettyServerHandler(HJ212Handler handler) {
        this.hj212Handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {
        String msg = (String) object;
        log.info("receive from host {}, msg {}.", ctx.channel().remoteAddress(), msg);

//        String identifier = msg.getCharSequence(0,2, CharsetUtil.UTF_8).toString();
        if (!msg.startsWith("##")) {
            log.info(" unknown identifier {} from host {}, payload {}. ", "##",
                    ctx.channel().remoteAddress(), msg);
            ctx.writeAndFlush(msg);
            return;
        }

        String response = hj212Handler.MessageHandler(ctx,msg);

        log.info("########### response {}.",response);
        if (StringUtils.isNotBlank(response)){
            log.info("--------- response---------- .");
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("hj212 channel connected, ip:{}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String host = ((InetSocketAddress)ctx.channel().remoteAddress()).getHostName();
        if (hj212Handler.getFilterIps().contains(host)){
            ctx.close();
            return;
        }
        if (HJ212Handler.device2channelMap.containsValue(ctx.channel())) {
            Collection<Channel> values = HJ212Handler.device2channelMap.values();
            values.remove(ctx.channel());
        }
        log.info("hj212 channel disconnected, ip:{}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常:{}", cause.getMessage());
        cause.printStackTrace();
    }
}

package com.szewec.netty.server;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HJ212AckHandler {

    /*public static void handler8001Resp(Header header, ChannelHandlerContext ctx, int replyId, int result) {
        T0001 ret = new T0001();
        header.setMessageId(JT808.平台通用应答);
        ret.setHeader(header);
        ret.setResultCode(result);
        ret.setReplyId(replyId);
        ret.setSerialNo(header.getSerialNo());
        ret.getHeader().setSerialNo(0);
        ByteBuf resp = encoder.encode(ret);
        log.info("return T8001 hex = {}", ByteBufUtil.hexDump(resp));
        ctx.writeAndFlush(resp);
    }

    public static void handler8100Resp(Header header, ChannelHandlerContext ctx, String token, int result){
        T8100 ret = new T8100();
        header.setMessageId(JT808.终端注册应答);
        ret.setHeader(header);
        ret.setResultCode(result);
        if (StringUtils.isNotEmpty(token)) {
            ret.setToken(token);
        }
        ret.setSerialNo(header.getSerialNo());
        ret.getHeader().setSerialNo(0);
        ByteBuf resp = encoder.encode(ret);
        log.info("return T8100 device register hex = {}", ByteBufUtil.hexDump(resp));
        ctx.writeAndFlush(resp);
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }*/
}

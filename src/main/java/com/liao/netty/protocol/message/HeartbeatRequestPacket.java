package com.szewec.netty.protocol.message;

import com.szewec.netty.protocol.message.command.Command;
import lombok.Data;

/**
 * @author pjmike
 * @create 2018-10-25 16:12
 */
@Data
public class HeartbeatRequestPacket extends Packet {

    @Override
    public Byte getCommand() {
        return Command.HEARTBEAT_REQUEST;
    }
}

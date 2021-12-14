package com.szewec.netty.mqttClient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.concurrent.Future;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttClientServer {

    @Value("${mqtt.port}")
    private int port;

    @Value("${mqtt.host}")
    private String host;

    @Value("${mqtt.clientId}")
    private String clientId;

    @Value("${mqtt.userName}")
    private String userName;

    @Value("${mqtt.password}")
    private String password;

    private MqttClient mqttClient;

    @PostConstruct
    public void init() throws InterruptedException {
        EventLoopGroup loop = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

        mqttClient = new MqttClientImpl((topic, payload) ->
                log.info(topic + "=>" + payload.toString(StandardCharsets.UTF_8)));
        mqttClient.setEventLoop(loop);
        mqttClient.getClientConfig().setChannelClass(NioSocketChannel.class);
        mqttClient.getClientConfig().setClientId(clientId);
        mqttClient.getClientConfig().setUsername(userName);
        mqttClient.getClientConfig().setPassword(password);
        mqttClient.getClientConfig().setProtocolVersion(MqttVersion.MQTT_3_1_1);
        mqttClient.getClientConfig().setReconnect(true);
        mqttClient.setCallback(new MqttClientCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                cause.printStackTrace();
            }

            @Override
            public void onSuccessfulReconnect() {
                log.info("mqtt client reconnect successful!");
            }
        });
        mqttClient.connect(host, port)
                .addListener(future -> {
                    try {
                        MqttConnectResult result = (MqttConnectResult) future.get(15, TimeUnit.SECONDS);
                        if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
                            log.error("error:" + result.getReturnCode() + "--");
                            mqttClient.disconnect();
                        } else {
                            log.info("mqtt client connect success! going to Subscribe topic.");
                            mqttClient.on(constants.GATEWAY_ATTRIBUTES_TOPIC);
//                            mqttClient.on(constants.GATEWAY_MAIN_TOPIC);
                            mqttClient.on(constants.GATEWAY_RPC_TOPIC);
//                            mqttClient.on(constants.GATEWAY_ATTRIBUTES_REQUEST_TOPIC);
                            mqttClient.on(constants.GATEWAY_ATTRIBUTES_RESPONSE_TOPIC);
//                            mqttClient.on(constants.GATEWAY_RPC_RESPONSE_TOPIC);
//                mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"read-property\"}", StandardCharsets.UTF_8));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).await(5, TimeUnit.SECONDS);
    }

    public MqttClient getMqttClient() {
        if (mqttClient == null) {
            try {
                init();
            } catch (InterruptedException e) {
                log.error("InterruptedException: init mqttClient error, message {}.", e.getCause());
            }
        }
        return mqttClient;
    }
}

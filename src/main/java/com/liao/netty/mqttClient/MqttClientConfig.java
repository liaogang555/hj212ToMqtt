package com.szewec.netty.mqttClient;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.handler.ssl.SslContext;
import java.net.SocketAddress;
import java.util.Random;
import java.util.function.Consumer;
import javax.net.ssl.SSLEngine;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class MqttClientConfig {

    private SslContext sslContext;
    private final String randomClientId;

    private String clientId;
    private int timeoutSeconds = 120;
    private int heartbeatInterval=60;
    private MqttVersion protocolVersion = MqttVersion.MQTT_3_1;
    private String username = null;
    private String password = null;
    private boolean cleanSession = true;
    private MqttLastWill lastWill;

    private Class<? extends Channel> channelClass = NioSocketChannel.class;

    private SocketAddress bindAddress;

    private Consumer<SSLEngine> sslEngineConsumer=(engine)->{};

    private boolean reconnect = true;
    private long retryInterval = 5L;

    public MqttClientConfig() {
        this(null);
    }

    public MqttClientConfig(SslContext sslContext) {
        this.sslContext = sslContext;
        Random random = new Random();
        String id = "netty-mqtt/";
        String[] options = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".split("");
        for (int i = 0; i < 8; i++) {
            id += options[random.nextInt(options.length)];
        }
        this.clientId = id;
        this.randomClientId = id;
    }


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            this.clientId = randomClientId;
        } else {
            this.clientId = clientId;
        }
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        if (timeoutSeconds != -1 && timeoutSeconds <= 0) {
            throw new IllegalArgumentException("timeoutSeconds must be > 0 or -1");
        }
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        if (heartbeatInterval != -1 && heartbeatInterval <= 0) {
            throw new IllegalArgumentException("heartbeatInterval must be > 0 or -1");
        }
        this.heartbeatInterval = heartbeatInterval;
    }

    public MqttVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(MqttVersion protocolVersion) {
        if (protocolVersion == null) {
            throw new NullPointerException("protocolVersion");
        }
        this.protocolVersion = protocolVersion;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }


    public MqttLastWill getLastWill() {
        return lastWill;
    }

    public void setLastWill(MqttLastWill lastWill) {
        this.lastWill = lastWill;
    }

    public Class<? extends Channel> getChannelClass() {
        return channelClass;
    }

    public void setChannelClass(Class<? extends Channel> channelClass) {
        this.channelClass = channelClass;
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public void setSslContext(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public SocketAddress getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(SocketAddress bindAddress) {
        this.bindAddress = bindAddress;
    }

    public Consumer<SSLEngine> getSslEngineConsumer() {
        return sslEngineConsumer;
    }

    public void setSslEngineConsumer(Consumer<SSLEngine> sslEngineConsumer) {
        this.sslEngineConsumer = sslEngineConsumer;
    }
}

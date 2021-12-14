package com.szewec.netty.server;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.szewec.netty.hj212.format.hbt212.core.T212Generator;
import com.szewec.netty.hj212.format.hbt212.core.T212Mapper;
import com.szewec.netty.hj212.format.hbt212.core.feature.GeneratorFeature;
import com.szewec.netty.hj212.format.hbt212.model.CpData;
import com.szewec.netty.hj212.format.hbt212.model.Data;
import com.szewec.netty.hj212.format.hbt212.model.DataFlag;
import com.szewec.netty.hj212.format.hbt212.model.Pollution;
import com.szewec.netty.hj212.translator.H212Translator;
import com.szewec.netty.hj212.translator.PollutionCodeFactory;
import com.szewec.netty.mqttClient.MqttClientServer;
import com.szewec.netty.hj212.format.hbt212.exception.T212FormatException;
import com.szewec.netty.hj212.format.segment.base.cfger.Feature;
import com.szewec.netty.utils.Constant;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.activation.UnsupportedDataTypeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HJ212Handler {

    @Autowired
    private MqttClientServer mqttClientServer;

    @Autowired
    ApplicationContext applicationContext;

    @Value("${tcp.filterIp}")
    private String filterIp;

    private List<String> filterIpLists = new ArrayList<>();

    public static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Map<String, Channel> device2channelMap = new ConcurrentHashMap<>();

    public static ArrayListMultimap<String, String> listMultimap = ArrayListMultimap.create();

    private static final T212Mapper mapper = new T212Mapper().enableDefaultParserFeatures().enableDefaultVerifyFeatures();

    public String MessageHandler(ChannelHandlerContext ctx, String msg) throws Exception {
        String response = "";
        Data data = mapper.readData(msg);
        String mn = data.getMn();
        String st = data.getSt();
        if (device2channelMap.containsKey(mn)) {
            Channel oldChannel = device2channelMap.get(mn);
            if (oldChannel.id() != ctx.channel().id()) {
                log.info("channel is changed, close old channel {} and update map new channel {}.", oldChannel.id(), ctx.channel().id());
                device2channelMap.put(mn, ctx.channel());
            }
        } else {
            device2channelMap.put(mn, ctx.channel());
        }
        log.info("################# data result {} ##########", data);

        Map<String, Pollution> pollution = data.getCp().getPollution();
        Class<? extends Enum> classType = PollutionCodeFactory.getInstance().getCodeMean(st);
        if (classType == null) {
            return response;
        }
        String mean = "";
        Map<String, Object> pollutionNew = new HashMap<>();
        for (String key : pollution.keySet()) {
            try {
                mean = H212Translator.I.translation(classType, key);
            } catch (ClassNotFoundException e) {
                log.warn("translation error, can not find pollution {}, error message {}.", key, e.getMessage());
            }
            pollutionNew.put(mean, pollution.get(key).getRtd());
        }
        String dateStr = data.getCp().getDataTime();
        dateStr = getDateFormat(dateStr, "yyyyMMddHHmmss");
        pollutionNew.put("DataTime", dateStr);
        log.info("------------pollutionNew {} ", pollutionNew);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(mn, Arrays.asList(pollutionNew));
        log.info("publish to mqtt json {}.", jsonObject.toJSONString());

        mqttClientServer.getMqttClient().publish("v1/gateway/telemetry",
                Unpooled.copiedBuffer(jsonObject.toJSONString(), StandardCharsets.UTF_8));
        if (DataFlag.A.isMarked(data.getDataFlag())) {
            log.info("########### need response ");
            try {
                response = mapper.writeDataAsString(Data.builder().qn(data.getQn()).st(data.getSt()).cn(Constant.DATA_RESPONSE)
                        .pw(data.getPw()).mn(data.getMn()).dataFlag(Arrays.asList(DataFlag.V0))
                        .cp(CpData.builder().build()).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    private static JSONObject getTelemetryData(String mn, Map<String, String> cpData) {
        JSONObject jsonObject = new JSONObject();
        System.out.println("@#@@@@@@@@@@@ list  " + Arrays.asList(cpData));
        jsonObject.put(mn, Arrays.asList(cpData));
        return jsonObject;
    }

    private String getDateFormat(String dateStr, String expression) {
        Date date = null;
        try {
            date = DateUtils.parseDate(dateStr, expression);
            dateStr = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            log.error(" date format error, date {}, expression {}, error {}.", dateStr, expression, e.getCause());
        }
        return dateStr;
    }

    public static void ack(ChannelHandlerContext ctx, String data) {

        StringWriter writer = new StringWriter();
        T212Generator generator = new T212Generator(writer);
        generator.setGeneratorFeature(Feature.collectFeatureDefaults(GeneratorFeature.class));
        try {
            generator.writeHeader();
            generator.writeDataAndLenAndCrc(getT212Data(data).toCharArray());
            generator.writeFooter();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            generator.close();
        }

    }

    public static String getT212Data(String data) throws IOException, T212FormatException {

        Map<String, Object> map = mapper.readDeepMap(data);
        log.info("@@@@@@@@@@@@@ map {} @@@@@@@@@@@@", map.toString());
        map.put("ST", 91);
        map.put("CN", 9014);
        map.put("QN", format.format(new Date()));

        return mapper.writeMapAsString(map);
    }

    public List<String> getFilterIps() {
        if (filterIpLists.size() == 0) {
            String[] filerIps = filterIp.split(";");
            filterIpLists = Arrays.asList(filerIps);
        }
        return filterIpLists;
    }

    public static void main(String[] args) {
        System.out.println("----------" + format.format(new Date()));
        String cp = "##0136ST=32;CN=2011;PW=123456;MN=LD130133000015;CP=" +
                "&&DataTime=20160824003817;B01-Rtd=36.91;011-Rtd=231.0,011-Flag=N;060-Rtd=1.803,060-Flag=N&&" +
                "4980\r\n";
        Map<String, Object> data = new HashMap<>();
        try {
            data = mapper.readDeepMap(cp);
        } catch (IOException | T212FormatException e) {
            e.printStackTrace();
        }
        log.info("----------- {} -------", data.toString());
        Map<String, String> cpData = (Map<String, String>) data.get("CP");
        String telemetryData = getTelemetryData(data.get("MN").toString(), cpData).toJSONString();
        System.out.println("############ telemetryData " + telemetryData);
    }
}

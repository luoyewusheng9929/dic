package com.gzhu.dic_platform.common.mqtt;

import com.gzhu.dic_platform.common.config.MqttProperties;
import com.gzhu.dic_platform.common.exception.GlobalException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MqttPublisher {

    private final MqttClient mqttClient;
    private final MqttProperties mqttProperties;

    @Autowired
    public MqttPublisher(MqttClient mqttClient, MqttProperties mqttProperties) {
        this.mqttClient = mqttClient;
        this.mqttProperties = mqttProperties;
    }

    public void publish(String topicSuffix, Map<String, Object> params) throws MqttException {
        if (params == null || !params.containsKey("dn")) {
            throw new GlobalException("缺少必需的设备编号！");
        }
        String dn = params.get("dn").toString();
        String topic = mqttProperties.getTopicsPublished().stream()
                .filter(t -> t.endsWith(topicSuffix))
                .findFirst()
                .map(t -> t + "/" + dn)  // 在主题后追加 dn
                .orElseThrow(() -> new IllegalArgumentException("Invalid command"));

        String payload = new com.google.gson.Gson().toJson(params);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(mqttProperties.getQos());
        mqttClient.publish(topic, message);
        System.out.println("Published message to topic " + topic);
    }
}

package com.gzhu.dic_platform.common.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    private final MqttProperties mqttProperties;

    public MqttConfig(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }

    @Bean
    public MqttClient mqttClient() throws MqttException {
        // 创建 MqttClient 实例
        return new MqttClient(mqttProperties.getBrokerUrl(), mqttProperties.getClientId(), new MemoryPersistence());
    }
}

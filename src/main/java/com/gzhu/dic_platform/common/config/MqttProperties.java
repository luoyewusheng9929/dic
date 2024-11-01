package com.gzhu.dic_platform.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "dic.mqtt")
@Data
public class MqttProperties {

    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
    private int qos;
    private int connectionTimeout;
    private int keepAliveInterval;
    private List<String> topicsSubscribed;
    private List<String> topicsPublished;
}

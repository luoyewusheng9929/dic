package com.gzhu.dic_platform.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.qos}")
    private int qos;

    @Value("${mqtt.topics}")
    private String[] topics;  // 多个主题数组

    @Autowired
    private InfluxDBClient influxDBClient;

    // 批量缓存队列
    private List<Point> batchPoints = new CopyOnWriteArrayList<>();
    private static final int BATCH_SIZE = 500;  // 批量大小

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);

        // 设置回调
        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                // 解析 MQTT 消息并存储到 InfluxDB
                try {
                    Map<String, Object> data = parseMessage(payload);
                    addToBatch(data);
                } catch (Exception e) {
                    System.err.println("Failed to store message to InfluxDB: " + e.getMessage());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Delivery complete: " + token.isComplete());
            }
        });

        // 连接并订阅多个主题
        client.connect(options);
        client.subscribe(topics, new int[]{qos});  // 为每个主题指定 QoS，假设所有主题使用相同的 QoS
        System.out.println("Subscribed to topics: " + String.join(", ", topics));

        return client;
    }

    // 解析 MQTT 消息的帮助方法
    private Map<String, Object> parseMessage(String payload) throws Exception {
        // 假设接收到的 payload 是 JSON 格式，转换为 Map 对象
        return new ObjectMapper().readValue(payload, Map.class);
    }

    // 将数据添加到批量队列中
    private void addToBatch(Map<String, Object> data) {
        try {
            String dn = data.get("dn").toString();  // 根据实际情况获取
            Integer idx = (Integer) data.get("idx");
            Double x = ((Number) data.get("x")).doubleValue();
            Double y = ((Number) data.get("y")).doubleValue();
            Long timestamp = ((Number) data.get("time")).longValue();

            // 创建 InfluxDB 数据点
            Point point = Point.measurement("event_points")
                    .addTag("dn", dn)
                    .addTag("idx", String.valueOf(idx))
                    .addField("x", x)
                    .addField("y", y)
                    .time(Instant.ofEpochMilli(timestamp), WritePrecision.MS);

            // 添加数据点到批量队列
            batchPoints.add(point);

            // 如果达到批量大小，则批量写入
            if (batchPoints.size() >= BATCH_SIZE) {
                flushBatch();
            }
        } catch (Exception e) {
            System.err.println("Failed to add data to batch: " + e.getMessage());
        }
    }

    // 将批量数据写入 InfluxDB
    private void flushBatch() {
        try (var writeApi = influxDBClient.makeWriteApi()) {
            writeApi.writePoints("dic", "gzhu", batchPoints);
            writeApi.flush();  // 手动刷新确保批量写入
            System.out.println("Batch of " + batchPoints.size() + " points written to InfluxDB.");
            batchPoints.clear();  // 清空批量队列
        } catch (Exception e) {
            System.err.println("Failed to flush batch to InfluxDB: " + e.getMessage());
        }
    }
}

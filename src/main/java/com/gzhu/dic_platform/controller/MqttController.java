package com.gzhu.dic_platform.controller;

import com.gzhu.dic_platform.common.exception.GlobalException;
import com.gzhu.dic_platform.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mq/")
@Tag(name = "mqtt消息控制页面")
public class MqttController {

    private static final String broker = "tcp://1.14.135.194:1883";
    private static final String topic_prefix = "dic/device/control/";
    private static final String username = "emqx";
    private static final String password = "public";
    private static final String clientId = "mqttx_controlInfoPublisher";
    private static final Gson gson = new Gson();

    // MQTT 发布消息的通用方法
    private void publishControlMessage(String command, Map<String, Object> params) throws MqttException {
        MqttClient client = new MqttClient(broker, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);

        client.connect(options);

        Map<String, Object> controlMessage = new HashMap<>();
        controlMessage.put("command", command);
        controlMessage.put("params", params != null ? params : new HashMap<>());
        controlMessage.put("timestamp", System.currentTimeMillis());

        if (params != null && params.get("dn") != null) {
            String topic = topic_prefix + params.get("dn");
            String payload = gson.toJson(controlMessage);
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(0);

            client.publish(topic, message);
            System.out.println("Published message: " + payload);
            client.disconnect();
        }else throw new GlobalException("缺少必需的设备编号！");
    }

    // 启动算法接口
    @PostMapping("/start")
    @Operation(summary = "启动算法")
    public Result startAlgorithm(@RequestBody Map<String, Object> startParams) {
        try {
            publishControlMessage("start", startParams);
            return Result.ok(true, 200, "Algorithm started successfully!");
        } catch (MqttException e) {
            e.printStackTrace();
            return Result.error(false, 500, "Failed to start algorithm: " + e.getMessage());
        }
    }

    // 停止算法接口
    @PostMapping("/stop")
    @Operation(summary = "停止算法")
    public Result stopAlgorithm(@RequestBody Map<String, Object> stopParams) {
        try {
            publishControlMessage("stop", stopParams);
            return Result.ok(true, 200,"Algorithm stopped successfully!");
        } catch (MqttException e) {
            e.printStackTrace();
            return Result.error(false, 500, "Failed to stop algorithm: " + e.getMessage());
        }
    }

    // 更新算法参数接口
    @PostMapping("/update")
    @Operation(summary = "更新算法参数")
    public Result updateAlgorithmParams(@RequestBody Map<String, Object> updateParams) {
        try {
            publishControlMessage("update_params", updateParams);
            return Result.ok(true, 200, "Algorithm parameters updated successfully!");
        } catch (MqttException e) {
            e.printStackTrace();
            return Result.error(false, 500, "Failed to update algorithm parameters: " + e.getMessage());
        }
    }
}

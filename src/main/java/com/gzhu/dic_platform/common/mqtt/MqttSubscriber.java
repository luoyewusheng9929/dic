package com.gzhu.dic_platform.common.mqtt;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzhu.dic_platform.common.config.MqttProperties;
import com.gzhu.dic_platform.common.utils.QiniuUploadUtil;
import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.common.utils.UploadImgUtil;
import com.gzhu.dic_platform.domain.CameraImage;
import com.gzhu.dic_platform.domain.CameraInfo;
import com.gzhu.dic_platform.domain.CameraSetting;
import com.gzhu.dic_platform.service.CameraImageService;
import com.gzhu.dic_platform.service.CameraInfoService;
import com.gzhu.dic_platform.service.CameraSettingService;
import com.gzhu.dic_platform.service.ImageUpdateService;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MqttSubscriber {

    @Autowired
    private CameraInfoService cameraInfoService;
    @Autowired
    private CameraImageService cameraImageService;
    @Autowired
    private CameraSettingService cameraSettingService;

    private final ImageUpdateService imageUpdateService;
    private final MqttClient mqttClient;
    private final MqttProperties mqttProperties;
    private final InfluxDBClient influxDBClient;
    private final List<Point> batchPoints = new CopyOnWriteArrayList<>();
    private static final int BATCH_SIZE = 50;

    @Autowired
    public MqttSubscriber(ImageUpdateService imageUpdateService, MqttClient mqttClient, MqttProperties mqttProperties, InfluxDBClient influxDBClient) {
        this.imageUpdateService = imageUpdateService;
        this.mqttClient = mqttClient;
        this.mqttProperties = mqttProperties;
        this.influxDBClient = influxDBClient;
    }

    @PostConstruct
    public void init() {
        try {
            // 配置回调和连接逻辑
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                    reconnect(); // 断开连接后自动重连
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    try {
                        switch (topic) {
                            case "dic/pit/disp":
                                handleDeformationTopic(payload);
                                break;
                            case "dic/pit/lastImg":
                                handleImageTopic(payload);
                                break;
                            case "dic/pit/sensor/temperature":
                                handleTemperatureTopic(payload);
                                break;
                            case "dic/pit/sensor/posture":
                                handlePostureTopic(payload);
                                break;
                            case "dic/pit/sensor/gps":
                                handleGpsTopic(payload);
                                break;
                            default:
                                System.out.println("Unhandled topic: " + topic);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to store message to InfluxDB: " + e.getMessage());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery complete: " + token.isComplete());
                }
            });

            // 在此处进行连接和订阅
            connectAndSubscribe();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connectAndSubscribe() throws MqttException {
        if (!mqttClient.isConnected()) {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttProperties.getUsername());
            options.setPassword(mqttProperties.getPassword().toCharArray());
            options.setConnectionTimeout(mqttProperties.getConnectionTimeout());
            options.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());

            mqttClient.connect(options);
            mqttClient.subscribe(mqttProperties.getTopicsSubscribed().toArray(new String[0]));
            System.out.println("Subscribed to topics: " + mqttProperties.getTopicsSubscribed());
        }
    }

    private void reconnect() {
        // 自动重连并重新订阅
        while (!mqttClient.isConnected()) {
            try {
                System.out.println("Attempting to reconnect to MQTT Broker...");
                connectAndSubscribe();
                System.out.println("Reconnected and resubscribed to topics.");
            } catch (MqttException e) {
                System.err.println("Reconnection failed: " + e.getMessage());
                try {
                    Thread.sleep(60000); // 每隔 60 秒重试一次
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // 处理 dic/pit/deformation 主题
    private void handleDeformationTopic(String payload) throws Exception {
        Map<String, Object> data = parseMessage(payload);
        addToBatch(data);  // 添加数据到批量处理队列
    }

    // 处理 dic/pit/image 主题
    private void handleImageTopic(String payload) {
        try {
            Map<String, Object> data = parseMessage(payload);
            String deviceNumber = data.get("dn").toString();
            String encodeImg = data.get("encodeImg").toString();
            // 将 Base64 字符串解码为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(encodeImg);
            // 使用工具类方法上传文件
//            String uploadResult = UploadImgUtil.uploadImg(deviceNumber, imageBytes);
//            System.out.println("Upload result: " + uploadResult);
            //使用七牛云工具类上传文件
            String uploadResult = QiniuUploadUtil.uploadImg(deviceNumber, imageBytes);
            //在camera_image表中新增一条记录
            CameraImage cameraImage = new CameraImage();
            cameraImage.setImage(uploadResult);
            cameraImage.setProject("DIC_Gzhu");
            cameraImage.setCamera(deviceNumber);
            cameraImageService.save(cameraImage);
            //更新camera_info表中的最新图
            LambdaUpdateWrapper<CameraInfo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(CameraInfo::getDeviceNumber, deviceNumber); //where deviceNumber = ?
            wrapper.set(CameraInfo::getLastImg, uploadResult);  // set lastImg = ?
            cameraInfoService.update(wrapper);

            // 使用 ImageUpdateService 完成等待的 future
            CompletableFuture<Result> future = imageUpdateService.removePendingFuture(deviceNumber);
            if (future != null) {
                future.complete(Result.ok(true, 200, "刷新图像成功！").data("lastImg", uploadResult));
            }
        } catch (Exception e) {
            System.err.println("Failed to decode and upload image: " + e.getMessage());
        }
    }

    // 处理 dic/pit/sensor/temperature 主题
    private void handleTemperatureTopic(String payload) throws Exception {
        System.out.println("Received temperature data: " + payload);
        // 解析温度数据，并进行存储或进一步处理
        Map<String, Object> data = parseMessage(payload);
        String dn = data.get("dn").toString();
        Double temperature = (Double) data.get("temperature");
        CameraSetting settings = cameraSettingService.getSettings(dn);
        settings.setTemperature(temperature);
        cameraSettingService.updateById(settings);
    }

    // 处理 dic/pit/sensor/posture 主题
    private void handlePostureTopic(String payload) throws Exception {
        System.out.println("Received posture data: " + payload);
        // 解析姿态数据，并进行存储或进一步处理
        Map<String, Object> data = parseMessage(payload);
        String dn = data.get("dn").toString();
        Integer pitchAngle = (Integer) data.get("pitchAngle");
        Integer rollAngle = (Integer) data.get("rollAngle");
        Integer horizontalAngle = (Integer) data.get("yawAngle");
        CameraSetting settings = cameraSettingService.getSettings(dn);
        settings.setPitchAngle(pitchAngle);
        settings.setRollAngle(rollAngle);
        settings.setHorizontalAngle(horizontalAngle);
        cameraSettingService.updateById(settings);
    }

    // 处理 dic/pit/sensor/gps 主题
    public void handleGpsTopic(String payload) throws Exception {
        System.out.println("Received GPS data: " + payload);
        Map<String, Object> data = parseMessage(payload);
        // 解析 GPS 数据，并进行存储或进一步处理
        String dn = data.get("dn").toString();
        String latitude = data.get("latitude").toString();
        String longitude = data.get("longitude").toString();
        CameraInfo info = cameraInfoService.getInfoByDeviceNumber(dn);
        info.setLatitude(latitude);
        info.setLongitude(longitude);
        cameraInfoService.updateById(info);
    }

    private Map<String, Object> parseMessage(String payload) throws Exception {
        return new ObjectMapper().readValue(payload, Map.class);
    }

    private void addToBatch(Map<String, Object> data) {
        try {
            String dn = Optional.ofNullable(data.get("dn"))
                    .map(Object::toString)
                    .orElse(null);

            Integer idx = Optional.ofNullable(data.get("idx"))
                    .map(val -> (Integer) val)
                    .orElse(null);

            Double x = Optional.ofNullable(data.get("offsetX"))
                    .map(val -> ((Number) val).doubleValue())
                    .filter(val -> !val.isNaN()) // 过滤掉 NaN 值
                    .orElse(null);

            Double y = Optional.ofNullable(data.get("offsetY"))
                    .map(val -> ((Number) val).doubleValue())
                    .filter(val -> !val.isNaN()) // 过滤掉 NaN 值
                    .orElse(null);

            Long timestamp = Optional.ofNullable(data.get("time"))
                    .map(val -> ((Number) val).longValue())
                    .orElse(null);

            // 如果任何一个字段为空，直接返回
            if (dn == null || idx == null || x == null || y == null || timestamp == null) {
                return;
            }

            Point point = Point.measurement("event_points")
                    .addTag("dn", dn)
                    .addTag("idx", String.valueOf(idx))
                    .addField("x", x)
                    .addField("y", y)
                    .time(Instant.ofEpochMilli(timestamp), WritePrecision.MS);

            batchPoints.add(point);

            if (batchPoints.size() >= BATCH_SIZE) {
                flushBatch();
            }
        } catch (Exception e) {
            System.err.println("Failed to add data to batch: " + e.getMessage() + data);
        }
    }

    private void flushBatch() {
        try (var writeApi = influxDBClient.makeWriteApi()) {
            writeApi.writePoints("dic", "gzhu", batchPoints);
            writeApi.flush();
            System.out.println("Batch of " + batchPoints.size() + " points written to InfluxDB.");
            batchPoints.clear();
        } catch (Exception e) {
            System.err.println("Failed to flush batch to InfluxDB: " + e.getMessage());
        }
    }
}

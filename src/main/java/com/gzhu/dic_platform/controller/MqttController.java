package com.gzhu.dic_platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzhu.dic_platform.common.mqtt.MqttPublisher;
import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.dto.TargetSpotDTO;
import com.gzhu.dic_platform.service.ImageUpdateService;
import com.gzhu.dic_platform.service.TargetSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/mq/")
@Tag(name = "MQTT消息控制接口")
public class MqttController {

    private final MqttPublisher mqttPublisher;

    private final ImageUpdateService imageUpdateService;

    @Autowired
    private TargetSpotService targetSpotService;

    @Autowired
    public MqttController(MqttPublisher mqttPublisher, ImageUpdateService imageUpdateService) {
        this.mqttPublisher = mqttPublisher;
        this.imageUpdateService = imageUpdateService;
    }

    // 启动算法接口
    @PostMapping("/start")
    @Operation(summary = "启动算法")
    public Result startAlgorithm(@RequestBody Map<String, Object> startParams) {
        try {
            mqttPublisher.publish("start", startParams);
            return Result.ok(true, 200, "启动算法指令发送成功!");
        } catch (MqttException e) {
            e.printStackTrace();
            return Result.error(false, 500, "启动算法指令发送失败!");
        }
    }

    // 停止算法接口
    @PostMapping("/stop")
    @Operation(summary = "停止算法")
    public Result stopAlgorithm(@RequestBody Map<String, Object> stopParams) {
        try {
            mqttPublisher.publish("stop", stopParams);
            return Result.ok(true, 200,"停止算法指令发送成功!");
        } catch (MqttException e) {
            e.printStackTrace();
            return Result.error(false, 500, "停止算法指令发送失败!");
        }
    }

    // 停止算法接口
    @PostMapping("/reboot")
    @Operation(summary = "设备重启")
    public Result deviceReboot(@RequestBody Map<String, Object> rebootParams) {
        try {
            mqttPublisher.publish("reboot", rebootParams);
            return Result.ok(true, 200,"设备重启指令发送成功!");
        } catch (MqttException e) {
            e.printStackTrace();
            return Result.error(false, 500, "设备重启指令发送失败!");
        }
    }

    // 更新算法参数接口
    @PutMapping("/update")
    @Operation(summary = "更新算法参数")
    public Result updateAlgorithmParams(@RequestBody Map<String, Object> updateParams) {
        try {
            mqttPublisher.publish("deviceParams", updateParams);
            return Result.ok(true, 200, "更新算法参数指令发送成功!");
        } catch (MqttException e) {
            e.printStackTrace();
            return Result.error(false, 500, "更新算法参数指令发送失败!");
        }
    }

    // 更新算法参数接口
    @PostMapping("/configSync")
    @Operation(summary = "配置同步")
    public Result configSynchronize(@RequestBody Map<String, Object> syncParams) {
        try {
            mqttPublisher.publish("configSync", syncParams);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        return Result.ok(true, 200, "配置同步指令发送成功!");
    }

    // 刷新图像接口
    @PostMapping("/updateLastImg")
    @Operation(summary = "刷新图像")
    public CompletableFuture<Result> updateImage(@RequestBody Map<String, Object> flushImageParams) {
        CompletableFuture<Result> future = new CompletableFuture<>();

        try {
            String deviceNumber = flushImageParams.get("dn").toString();
            imageUpdateService.addPendingFuture(deviceNumber, future);
            mqttPublisher.publish("lastImg", flushImageParams);
        } catch (MqttException e) {
            e.printStackTrace();
            future.complete(Result.error(false, 500, "刷新图像失败"));
        }

        return future;
    }

    // 更新靶点参数
    @PutMapping("/updateRoi")
    @Operation(summary = "更新靶点参数")
    public Result updateRoi(@RequestParam("baseImg") MultipartFile baseImg,
                            @Parameter(description = "数据格式：[{\n" +
                                    "  \"id\": 0,\n" +
                                    "  \"spotNumber\": 0,\n" +
                                    "  \"spotName\": \"靶点1\",\n" +
                                    "  \"distance\": 20,\n" +
                                    "  \"width\": 23,\n" +
                                    "  \"height\": 24,\n" +
                                    "  \"isBasepoint\": 0,\n" +
                                    "  \"camera\": \"FNF24050053\",\n" +
                                    "  \"project\": \"\",\n" +
                                    "  \"xcoordinate\": 100,\n" +
                                    "  \"ycoordinate\": 110\n" +
                                    "},{xxxx}]", required = false)
                            @RequestParam("spotList") String spotsJson) {
        try {
            // 检查文件是否为空
            if (baseImg == null || baseImg.isEmpty()) {
                return Result.error(false, 400, "图片不能为空");
            }

            // Decode the JSON string to List<TargetSpotDTO>
            ObjectMapper objectMapper = new ObjectMapper();
            List<TargetSpotDTO> spots = objectMapper.readValue(spotsJson,
                    new TypeReference<>() {});

            boolean flag = targetSpotService.updateSpots(spots);
            if(!flag)  return Result.error(false, 500, "更新靶点信息失败！");

            HashMap<String, Object> updateParams = new HashMap<>();
            updateParams.put("dn", spots.getFirst().getCamera());
            updateParams.put("spotList", spots);
            // 将图片数据编码为 Base64 字符串
            String encodedImage = Base64.getEncoder().encodeToString(baseImg.getBytes());
            updateParams.put("encodeImg", encodedImage);
            // 发布包含图片的参数
            mqttPublisher.publish("RoiAndBaseImg", updateParams);

            return Result.ok(true, 200, "更新靶点参数指令发送成功!");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(false, 500, "更新靶点参数指令发送失败!");
        }
    }
}

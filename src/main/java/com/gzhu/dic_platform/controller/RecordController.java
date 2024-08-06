package com.gzhu.dic_platform.controller;

import com.gzhu.dic_platform.common.utils.Result;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.write.Point;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "influxdb页面")
public class RecordController {

    @Autowired
    private InfluxDBClient influxDBClient;

    @Operation(summary = "数据上传，从远端linux系统接收数据并存储到influxdb中")
    @PostMapping("/uplinks/data")
    public String receiveData(@RequestBody List<Map<String, Object>> dataList) {
        System.out.println("Received data list: " + dataList.size() + " items."); // 增加日志
        try (var writeApi = influxDBClient.getWriteApi()) {
            for (Map<String, Object> data : dataList) {
                // 从请求数据中解析字段
                String dn = "d7e7ae3d595c053f"; // 可以根据实际情况动态获取
                Integer idx = (Integer) data.get("idx");
                Double x = ((Number) data.get("x")).doubleValue();
                Double y = ((Number) data.get("y")).doubleValue();
                Long timestamp = ((Number) data.get("time")).longValue();

                // 创建InfluxDB数据点
                Point point = Point.measurement("event_points")
                        .addTag("dn", dn)
                        .addTag("idx", String.valueOf(idx))
                        .addField("x", x)
                        .addField("y", y)
                        .time(Instant.ofEpochMilli(timestamp), WritePrecision.MS);

                // 写入数据到InfluxDB
                writeApi.writePoint(point);
                System.out.println("Point written: " + point.toLineProtocol()); // 增加日志
            }
            writeApi.flush(); // 确保所有数据都写入
            return "Data received and stored successfully.";
        } catch (Exception e) {
            return "Failed to process data: " + e.getMessage();
        }
    }

    @Operation(summary = "获取数据，从influxdb中根据条件获取数据")
    @GetMapping("links/data")
    public Result getData(
            @RequestParam(name = "deviceNumber", required = false) String deviceNumber,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
//            @RequestParam(name = "samplingDuration", required = false, defaultValue = "1") int samplingDuration,
//            @RequestParam(name = "samplingMethod", required = false, defaultValue = "mean") String samplingMethod,
            @RequestParam(name = "limit", required = false, defaultValue = "1000") int limit) {

        List<Map<String, Object>> result = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();


        // 计算当前时间的前一天
        Instant now = Instant.now();
        String defaultStartTime = now.minus(1, ChronoUnit.DAYS).toString(); // 当前时间的前一天
        String defaultEndTime = now.toString(); // 当前时间

        // 基础查询语句
        queryBuilder.append(String.format(
                "from(bucket: \"dic\") |> range(start: %s, stop: %s) ",
                startTime != null ? formatTime(startTime) : defaultStartTime,
                endTime != null ? formatTime(endTime) : defaultEndTime
        ));

        // 指定查询的表（measurement）
        queryBuilder.append("|> filter(fn: (r) => r._measurement == \"event_points\") ");

        // 如果 deviceNumber 存在，添加过滤条件
        if (!StringUtils.isEmpty(deviceNumber)){
            queryBuilder.append(String.format("|> filter(fn: (r) => r.dn == \"%s\") ", deviceNumber));
        }

        // 采样和限制
//        queryBuilder.append(String.format("|> aggregateWindow(every: %ds, fn: %s) |> limit(n: %d)",
//                samplingDuration, samplingMethod.toLowerCase(), limit));
        // 限制
        queryBuilder.append(String.format("|> limit(n: %d)", limit));

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(queryBuilder.toString());

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                result.add(record.getValues());
            }
        }

        return Result.ok().data("records", result);
    }

    private String formatTime(String dateTime) {
        return DateTimeFormatter.ISO_INSTANT.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneOffset.UTC)
                        .parse(dateTime)
        );
    }
}


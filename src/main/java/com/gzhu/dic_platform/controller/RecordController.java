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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
            @RequestParam(name = "idx", required = false) Integer idx,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            @RequestParam(name = "samplingDuration", required = false) Integer samplingDuration,
            @RequestParam(name = "samplingMethod", required = false) String samplingMethod,
            @RequestParam(name = "limit", required = false, defaultValue = "600") int limit) {

        List<Map<String, Object>> result = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) && startTime.equals(endTime)) {
            return Result.error(false, 500, "查询时间范围为空！");
        }

        // 计算当前时间的前一天
        Instant now = Instant.now();
        String defaultStartTime = now.minus(180, ChronoUnit.MINUTES).toString(); // 当前时间的前一天
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

        // 如果 idx 存在，添加过滤条件
        if (idx != null) {
            queryBuilder.append(String.format("|> filter(fn: (r) => r.idx == \"%d\") ", idx));
        }

        // 采样
        if(samplingDuration != null && StringUtils.isNotEmpty(samplingMethod)) {
            queryBuilder.append(String.format("|> aggregateWindow(every: %dms, fn: %s, createEmpty: false)",
                    samplingDuration, samplingMethod.toLowerCase()));
        }

        // 时间倒序排序，确保最新的数据排在前面
        queryBuilder.append("|> sort(columns: [\"_time\"], desc: true) ");

        // 限制
        queryBuilder.append(String.format("|> limit(n: %d)", limit));

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(queryBuilder.toString());

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                // 获取原始的 UTC 时间戳
                Object utcTimeObj = record.getValueByKey("_time");

                // 检查是否有时间字段
                if (utcTimeObj instanceof Instant utcTime) {
                    // 将 UTC 时间转换为北京时间（UTC+8）
                    ZonedDateTime beijingTime = utcTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneOffset.ofHours(8));
//                    // 格式化为你需要的格式，可以保留ZonedDateTime对象或转换为字符串
                    String formattedTime = beijingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                    // 替换为北京时间
                    record.getValues().put("_time", formattedTime);
                }

                // 添加转换后的记录到结果
                result.add(record.getValues());
            }
        }

        return Result.ok().data("total", result.size()).data("records", result);
    }

    private String formatTime(String dateTime) {
        // 解析传入的中国北京时间，并转换为UTC时间
        ZonedDateTime utcTime = ZonedDateTime.parse(dateTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"))).withZoneSameInstant(ZoneOffset.UTC);
        // 格式化为 ISO 8601 格式
        return DateTimeFormatter.ISO_INSTANT.format(utcTime);
    }
}


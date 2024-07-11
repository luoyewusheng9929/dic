package com.gzhu.dic_platform.controller;

import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.service.UserDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@Tag(name = "用户数据页面")
public class UserDataController {

    @Autowired
    private UserDataService userDataService;

    @Operation(
            summary = "上传数据到云平台"
    )
    @PostMapping("/upload")
    public Result uploadData(@RequestBody Map<String, Object> data) {
        // 处理接收到的数据
        System.out.println(data);

        try {
            userDataService.createUserData(data);
            return Result.ok(true, 200, "data upload success");
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(false,500, "data upload fail").data("msg", e.getMessage());
        }
    }

    @Operation( summary = "获取全部数据" )
    @GetMapping
    public Result getUserData() {
        ArrayList<Map<String, Object>> records = userDataService.getUserData();
        return Result.ok(true, 200, "data upload success").data("records", records);
    }

    @Operation(summary = "根据靶点号，开始时间，截至时间进行条件查询，条件可以为空")
    @GetMapping("/search")
    public Result getByConditions(
            @Parameter(description = "靶点号", required = false)
            @RequestParam(value = "targetSpotNumber", required = false) Integer targetSpotNumber,
            @Parameter(description = "开始时间，格式: yyyy-MM-dd HH:mm:ss", required = false)
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @Parameter(description = "结束时间，格式： yyyy-MM-dd HH:mm:ss", required = false)
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
            ArrayList<Map<String, Object>> records = userDataService.findByConditions(targetSpotNumber, startTime, endTime);
            return Result.ok(true, 200, "data upload success").data("records", records);
    }
}

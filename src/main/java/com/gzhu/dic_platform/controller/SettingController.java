package com.gzhu.dic_platform.controller;

import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.domain.CameraSetting;
import com.gzhu.dic_platform.dto.CameraSettingDTO;
import com.gzhu.dic_platform.service.CameraSettingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "设置页面")
public class SettingController {
    @Autowired
    private CameraSettingService settingService;

    @GetMapping
    public Result getSettings() {
        CameraSetting settings = settingService.getSettings();
        return Result.ok(true, 200, null).data("settings", settings);
    }

    @PutMapping
    public Result updateSettings(@RequestBody CameraSettingDTO settingsDTO) {
        try {
            settingService.updateSettings(settingsDTO);
            return Result.ok(true, 200, "Settings updated successfully");
        } catch (Exception e) {
            return Result.error(false, HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

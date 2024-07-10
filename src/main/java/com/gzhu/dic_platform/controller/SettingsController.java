package com.gzhu.dic_platform.controller;

import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.domain.CameraSettings;
import com.gzhu.dic_platform.service.CameraSettingsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "设置页面")
public class SettingsController {
    @Autowired
    private CameraSettingsService settingsService;

    @GetMapping
    public Result getSettings() {
        CameraSettings settings = settingsService.getSettings();
        return Result.ok(true, 200, null).data("settings", settings);
    }

    @PutMapping
    public Result updateSettings(@RequestBody CameraSettings settings) {
        try {
            settingsService.updateSettings(settings);
            return Result.ok(true, 200, "Settings updated successfully");
        } catch (Exception e) {
            return Result.error(false, HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

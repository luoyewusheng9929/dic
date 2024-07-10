package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.domain.CameraSettings;
import com.gzhu.dic_platform.service.CameraSettingsService;
import com.gzhu.dic_platform.mapper.CameraSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 23617
* @description 针对表【camera_settings】的数据库操作Service实现
* @createDate 2024-07-04 21:12:29
*/
@Service
public class CameraSettingsServiceImpl extends ServiceImpl<CameraSettingsMapper, CameraSettings>
    implements CameraSettingsService{

    @Autowired
    private CameraSettingsMapper cameraSettingsMapper;
    @Override
    public CameraSettings getSettings() {
        return cameraSettingsMapper.selectById(1L);
    }

    public void updateSettings(CameraSettings settings) {
        LambdaQueryWrapper<CameraSettings> queryWrapper = new LambdaQueryWrapper<CameraSettings>();
        queryWrapper.eq(CameraSettings::getId, 1L);
        CameraSettings currentSettings = getOne(queryWrapper);
        if(currentSettings == null)
            throw new RuntimeException("Settings not found");

        currentSettings.setFocalLength(settings.getFocalLength());
        currentSettings.setRollAngle(settings.getRollAngle());
        currentSettings.setHorizontalAngle(settings.getHorizontalAngle());
        currentSettings.setPitchAngle(settings.getPitchAngle());
        currentSettings.setTemperature(settings.getTemperature());
        currentSettings.setExposureTime(settings.getExposureTime());
        currentSettings.setFrameRate(settings.getFrameRate());
        currentSettings.setUpperLimit(settings.getUpperLimit());
        currentSettings.setLowerLimit(settings.getLowerLimit());
        currentSettings.setUploadInterval(settings.getUploadInterval());
        currentSettings.setSignalStrength(settings.getSignalStrength());
        currentSettings.setSignalRatio(settings.getSignalRatio());
        updateById(currentSettings);
    }
}





package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.domain.CameraSetting;
import com.gzhu.dic_platform.dto.CameraSettingDTO;
import com.gzhu.dic_platform.mapper.CameraSettingMapper;
import com.gzhu.dic_platform.service.CameraSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 23617
* @description 针对表【camera_settings】的数据库操作Service实现
* @createDate 2024-07-04 21:12:29
*/
@Service
public class CameraSettingServiceImpl extends ServiceImpl<CameraSettingMapper, CameraSetting>
    implements CameraSettingService {

    @Autowired
    private CameraSettingMapper cameraSettingMapper;
    @Override
    public CameraSetting getSettings() {
        return cameraSettingMapper.selectById(1L);
    }

    public void updateSettings(CameraSettingDTO settingsDTO) {
        LambdaQueryWrapper<CameraSetting> queryWrapper = new LambdaQueryWrapper<CameraSetting>();
        queryWrapper.eq(CameraSetting::getId, 1L);
        CameraSetting currentSettings = getOne(queryWrapper);
        if(currentSettings == null)
            throw new RuntimeException("Settings not found");

        currentSettings.setFocalLength(settingsDTO.getFocalLength());
        currentSettings.setRollAngle(settingsDTO.getRollAngle());
        currentSettings.setHorizontalAngle(settingsDTO.getHorizontalAngle());
        currentSettings.setPitchAngle(settingsDTO.getPitchAngle());
        currentSettings.setTemperature(settingsDTO.getTemperature());
        currentSettings.setExposureTime(settingsDTO.getExposureTime());
        currentSettings.setFrameRate(settingsDTO.getFrameRate());
        currentSettings.setUpperLimit(settingsDTO.getUpperLimit());
        currentSettings.setLowerLimit(settingsDTO.getLowerLimit());
        currentSettings.setUploadInterval(settingsDTO.getUploadInterval());
        currentSettings.setSignalStrength(settingsDTO.getSignalStrength());
        currentSettings.setSignalRatio(settingsDTO.getSignalRatio());
        updateById(currentSettings);
    }
}





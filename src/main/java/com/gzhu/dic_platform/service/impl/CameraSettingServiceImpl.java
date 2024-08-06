package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.common.exception.GlobalException;
import com.gzhu.dic_platform.common.utils.BeanCopyUtils;
import com.gzhu.dic_platform.domain.CameraSetting;
import com.gzhu.dic_platform.dto.CameraSettingDTO;
import com.gzhu.dic_platform.mapper.CameraSettingMapper;
import com.gzhu.dic_platform.service.CameraInfoService;
import com.gzhu.dic_platform.service.CameraSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    public CameraSetting getSettings(String deviceNumber) {
        LambdaQueryWrapper<CameraSetting> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(CameraSetting::getCamera, deviceNumber);
        return cameraSettingMapper.selectOne(queryWrapper);
    }

    public void updateSettings(CameraSettingDTO settingsDTO) {
        LambdaQueryWrapper<CameraSetting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CameraSetting::getCamera, settingsDTO.getCamera());
        CameraSetting result = cameraSettingMapper.selectOne(queryWrapper);
        if(result == null) {
            throw new GlobalException("设备编号不存在");
        }

        CameraSetting cameraSetting = BeanCopyUtils.copyBean(settingsDTO, CameraSetting.class);
        cameraSetting.setId(result.getId());
        cameraSettingMapper.updateById(cameraSetting);
    }
}





package com.gzhu.dic_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhu.dic_platform.domain.CameraSetting;
import com.gzhu.dic_platform.dto.CameraSettingDTO;

/**
* @author 23617
* @description 针对表【camera_settings】的数据库操作Service
* @createDate 2024-07-04 21:12:29
*/
public interface CameraSettingService extends IService<CameraSetting> {

    CameraSetting getSettings();

    void updateSettings(CameraSettingDTO settingDTO);
}

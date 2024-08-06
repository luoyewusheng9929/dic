package com.gzhu.dic_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gzhu.dic_platform.domain.CameraInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhu.dic_platform.dto.CameraInfoDTO;

import java.util.ArrayList;

/**
* @author 23617
* @description 针对表【camera_info】的数据库操作Service
* @createDate 2024-07-24 21:21:43
*/
public interface CameraInfoService extends IService<CameraInfo> {

    CameraInfo getInfoByDeviceNumber(String deviceNumber);

    IPage<CameraInfo> getAllInfoByPage(int pageNum, int pageSize);

    boolean addInfo(String deviceNumber);

    boolean updateInfo(CameraInfoDTO cameraInfoDTO);

    boolean deleteInfoByDeviceNumber(String deviceNumber);
}

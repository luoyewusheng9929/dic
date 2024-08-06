package com.gzhu.dic_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gzhu.dic_platform.domain.CameraImage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhu.dic_platform.domain.CameraInfo;

/**
* @author 23617
* @description 针对表【camera_image】的数据库操作Service
* @createDate 2024-08-01 21:33:11
*/
public interface CameraImageService extends IService<CameraImage> {


    IPage<CameraImage> getImgByPage(String deviceNumber, int pageNum, int pageSize);
}

package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.domain.CameraImage;
import com.gzhu.dic_platform.service.CameraImageService;
import com.gzhu.dic_platform.mapper.CameraImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 23617
* @description 针对表【camera_image】的数据库操作Service实现
* @createDate 2024-08-01 21:33:11
*/
@Service
public class CameraImageServiceImpl extends ServiceImpl<CameraImageMapper, CameraImage>
    implements CameraImageService{

    @Autowired
    private CameraImageMapper cameraImageMapper;

    @Override
    public IPage<CameraImage> getImgByPage(String deviceNumber, int pageNum, int pageSize) {
        Page<CameraImage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CameraImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CameraImage::getCamera, deviceNumber);
        return cameraImageMapper.selectPage(page, wrapper);
    }
}





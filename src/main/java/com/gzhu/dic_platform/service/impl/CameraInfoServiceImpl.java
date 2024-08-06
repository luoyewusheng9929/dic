package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.common.exception.GlobalException;
import com.gzhu.dic_platform.common.utils.BeanCopyUtils;
import com.gzhu.dic_platform.common.utils.UploadImgUtil;
import com.gzhu.dic_platform.domain.CameraInfo;
import com.gzhu.dic_platform.dto.CameraInfoDTO;
import com.gzhu.dic_platform.service.CameraInfoService;
import com.gzhu.dic_platform.mapper.CameraInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

/**
* @author 23617
* @description 针对表【camera_info】的数据库操作Service实现
* @createDate 2024-07-24 21:21:43
*/
@Service
public class CameraInfoServiceImpl extends ServiceImpl<CameraInfoMapper, CameraInfo>
    implements CameraInfoService{

    @Autowired
    private CameraInfoMapper cameraInfoMapper;

    @Override
    public CameraInfo getInfoByDeviceNumber(String deviceNumber) {
        LambdaQueryWrapper<CameraInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CameraInfo::getDeviceNumber, deviceNumber);
        return getOne(queryWrapper);
    }

    @Override
    public IPage<CameraInfo> getAllInfoByPage(int pageNum, int pageSize) {
        Page<CameraInfo> page = new Page<>(pageNum, pageSize);
        return cameraInfoMapper.selectPage(page, null);
    }

    @Override
    public boolean addInfo(String deviceNumber) {
        // 定义设备编号的格式要求：长度为16，包含数字和小写字母
        String regex = "^[a-z0-9]{16}$";
        // 校验设备编号格式
        if (!deviceNumber.matches(regex)) {
            throw new GlobalException("设备编号格式不正确！应为16位数字和小写字母的组合。");
        }
        // 检查 device_number 是否已存在, 避免重复
        if (cameraInfoMapper.selectOne(new LambdaQueryWrapper<CameraInfo>().eq(CameraInfo::getDeviceNumber, deviceNumber)) != null) {
            throw new GlobalException("设备编号已存在！");
        }
        CameraInfo cameraInfo = new CameraInfo();
        cameraInfo.setDeviceNumber(deviceNumber);
        return save(cameraInfo);
    }

    @Override
    public boolean updateInfo(CameraInfoDTO cameraInfoDTO) {
        MultipartFile uploadInstallImg = cameraInfoDTO.getUploadInstallImg();
        String uploadInstallImgFilename = UploadImgUtil.uploadImg(cameraInfoDTO.getDeviceNumber(), uploadInstallImg);

        CameraInfo cameraInfo = BeanCopyUtils.copyBean(cameraInfoDTO, CameraInfo.class);
        cameraInfo.setInstallImg(uploadInstallImgFilename);
        LambdaUpdateWrapper<CameraInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CameraInfo::getDeviceNumber, cameraInfo.getDeviceNumber());
        return cameraInfoMapper.update(cameraInfo, updateWrapper) > 0;
    }

    @Override
    public boolean deleteInfoByDeviceNumber(String deviceNumber) {
        return cameraInfoMapper.updateByDeviceNumber(deviceNumber, true);
    }
}





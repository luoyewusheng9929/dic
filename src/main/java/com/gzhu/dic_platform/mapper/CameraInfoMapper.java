package com.gzhu.dic_platform.mapper;

import com.gzhu.dic_platform.domain.CameraInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 23617
* @description 针对表【camera_info】的数据库操作Mapper
* @createDate 2024-07-29 20:41:38
* @Entity com.gzhu.dic_platform.domain.CameraInfo
*/
public interface CameraInfoMapper extends BaseMapper<CameraInfo> {

    boolean updateByDeviceNumber(@Param("deviceNumber") String deviceNumber, @Param("isDeleted") boolean isDeleted);
}





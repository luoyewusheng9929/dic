package com.gzhu.dic_platform.service;

import com.gzhu.dic_platform.domain.TargetSpot;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhu.dic_platform.dto.TargetSpotDTO;

/**
* @author 23617
* @description 针对表【target_spot】的数据库操作Service
* @createDate 2024-07-11 16:46:03
*/
public interface TargetSpotService extends IService<TargetSpot> {

    void addTargetSpot(TargetSpotDTO targetSpotDTO);

    int getTargetSpotNums();
}

package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.domain.TargetSpot;
import com.gzhu.dic_platform.dto.TargetSpotDTO;
import com.gzhu.dic_platform.service.TargetSpotService;
import com.gzhu.dic_platform.mapper.TargetSpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 23617
* @description 针对表【target_spot】的数据库操作Service实现
* @createDate 2024-07-11 16:46:03
*/
@Service
public class TargetSpotServiceImpl extends ServiceImpl<TargetSpotMapper, TargetSpot>
    implements TargetSpotService{

    @Autowired
    private TargetSpotMapper targetSpotMapper;
    @Override
    public void addTargetSpot(TargetSpotDTO targetSpotDTO) {
        TargetSpot targetSpot = new TargetSpot();
        targetSpot.setSpotNumber(targetSpotDTO.getSpotNumber());
        targetSpot.setSpotName(targetSpotDTO.getSpotName());
        targetSpot.setXCoordinate(targetSpotDTO.getXCoordinate());
        targetSpot.setYCoordinate(targetSpotDTO.getYCoordinate());
        targetSpot.setDistance(targetSpotDTO.getDistance());
        targetSpot.setWidth(targetSpotDTO.getWidth());
        targetSpot.setHeight(targetSpotDTO.getHeight());
        targetSpot.setCamera(targetSpotDTO.getCamera());
        targetSpot.setProject(targetSpotDTO.getProject());
        save(targetSpot);
    }

    @Override
    public int getTargetSpotNums() {
        return targetSpotMapper.countById();
    }
}





package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.common.utils.BeanCopyUtils;
import com.gzhu.dic_platform.domain.TargetSpot;
import com.gzhu.dic_platform.dto.TargetSpotDTO;
import com.gzhu.dic_platform.service.TargetSpotService;
import com.gzhu.dic_platform.mapper.TargetSpotMapper;
import com.gzhu.dic_platform.vo.TargetSpotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        TargetSpot targetSpot = BeanCopyUtils.copyBean(targetSpotDTO, TargetSpot.class);
        save(targetSpot);
    }

    @Override
    public int getTargetSpotNums(String deviceNumber) {
        LambdaQueryWrapper<TargetSpot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TargetSpot::getCamera, deviceNumber);
        List<TargetSpot> targetSpots = targetSpotMapper.selectList(wrapper);
        return targetSpots.size();
    }

    @Override
    public ArrayList<TargetSpotVO> getTargetSpotByCameraId(String deviceNumber) {
        return (ArrayList<TargetSpotVO>) targetSpotMapper.getAllByCamera(deviceNumber);
    }

    @Override
    public boolean updateSpots(List<TargetSpotDTO> spots) {
        try {
            List<TargetSpot> targetSpots = BeanCopyUtils.copyBeanList(spots, TargetSpot.class);
            List<Long> incomingIds = targetSpots.stream()
                    .map(TargetSpot::getId)
                    .collect(Collectors.toList());

            // 获取数据库中对应相机编号的所有的靶点
            LambdaQueryWrapper<TargetSpot> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TargetSpot::getCamera, spots.getFirst().getCamera());
            List<TargetSpot> existingSpots = targetSpotMapper.selectList(queryWrapper);

            // 找出需要删除的靶点
            List<TargetSpot> spotsToDelete = existingSpots.stream()
                    .filter(spot -> !incomingIds.contains(spot.getId()))
                    .collect(Collectors.toList());

            // 标记删除的靶点
            for (TargetSpot spotToDelete : spotsToDelete) {
                targetSpotMapper.deleteById(spotToDelete);
            }

            // 更新或插入靶点
            for (TargetSpot ts : targetSpots) {
                if (ts.getId() != null) {
                    targetSpotMapper.updateById(ts);
                } else {
                    targetSpotMapper.insert(ts);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public HashMap<String, Object> getAllTargetSpotsByProject(String project) {
        //先获取所有的靶点信息
        List<TargetSpot> spotList = targetSpotMapper.getTargetSpotsList(null);

        //获取设备在线的所有靶点信息
        List<TargetSpot> onlineSpots = targetSpotMapper.getTargetSpotsList(1);
        HashMap<String, Object> targetInfoMap = new HashMap<>();
        targetInfoMap.put("spotList", spotList);
        targetInfoMap.put("total", spotList.size());
        targetInfoMap.put("onlineSpots", onlineSpots);
        targetInfoMap.put("onlineNum", onlineSpots.size());
        return targetInfoMap;
    }
}





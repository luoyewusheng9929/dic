package com.gzhu.dic_platform.mapper;

import com.gzhu.dic_platform.domain.TargetSpot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhu.dic_platform.vo.TargetSpotVO;

import java.util.List;

/**
* @author 23617
* @description 针对表【target_spot】的数据库操作Mapper
* @createDate 2024-07-25 17:51:41
* @Entity com.gzhu.dic_platform.domain.TargetSpot
*/
public interface TargetSpotMapper extends BaseMapper<TargetSpot> {
    List<TargetSpotVO> getAllByCamera(String deviceNumber);

    List<TargetSpot> getTargetSpotsList(Integer online);
}





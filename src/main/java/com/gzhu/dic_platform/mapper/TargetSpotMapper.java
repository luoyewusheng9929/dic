package com.gzhu.dic_platform.mapper;

import com.gzhu.dic_platform.domain.TargetSpot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 23617
* @description 针对表【target_spot】的数据库操作Mapper
* @createDate 2024-07-11 16:46:02
* @Entity com.gzhu.dic_platform.domain.TargetSpot
*/
public interface TargetSpotMapper extends BaseMapper<TargetSpot> {

    int countById();

}





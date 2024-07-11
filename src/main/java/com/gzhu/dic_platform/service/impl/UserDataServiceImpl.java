package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.domain.UserData;
import com.gzhu.dic_platform.service.UserDataService;
import com.gzhu.dic_platform.mapper.UserDataMapper;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* @author 23617
* @description 针对表【user_data】的数据库操作Service实现
* @createDate 2024-07-10 00:34:39
*/
@Service
public class UserDataServiceImpl extends ServiceImpl<UserDataMapper, UserData>
    implements UserDataService{

    @Override
    public void createUserData(Map<String, Object> data) throws ParseException {
        List<Map<String, Object>> batch = (List<Map<String, Object>>) data.get("batch");
        for (Map<String, Object> map : batch) {
            UserData userData = new UserData();
            userData.setOffsetX((Double) map.get("offsetX"));
            userData.setOffsetY((Double) map.get("offsetY"));
            userData.setTargetSpotNumber((Integer) map.get("targetSpotNumber"));
            // 解析时间字符串
            String timeStr = (String) map.get("time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 使用中国标准时间时区
            userData.setTime(sdf.parse(timeStr));
            save(userData);
        }
    }

    @Override
    public ArrayList<Map<String, Object>> getUserData() {
        List<UserData> list = list();
        return convertToMapList(list);
    }

    @Override
    public ArrayList<Map<String, Object>> findByConditions(Integer targetSpotNumber, Date startTime, Date endTime) {
        LambdaQueryWrapper<UserData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(targetSpotNumber != null, UserData::getTargetSpotNumber, targetSpotNumber);
        wrapper.gt(startTime != null, UserData::getTime, startTime);
        wrapper.le(endTime != null, UserData::getTime, endTime);
        List<UserData> list = list(wrapper);
        return convertToMapList(list);
    }

    // 私有方法，用于转换UserData列表到Map列表
    private ArrayList<Map<String, Object>> convertToMapList(List<UserData> list) {
        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (UserData userData : list) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("offsetX", userData.getOffsetX());
            result.put("offsetY", userData.getOffsetY());
            result.put("targetSpotNumber", userData.getTargetSpotNumber());
            result.put("time", userData.getTime());
            mapList.add(result);
        }
        return mapList;
    }
}





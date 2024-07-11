package com.gzhu.dic_platform.service;

import com.gzhu.dic_platform.domain.UserData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
* @author 23617
* @description 针对表【user_data】的数据库操作Service
* @createDate 2024-07-10 00:34:39
*/
public interface UserDataService extends IService<UserData> {

    void createUserData(Map<String, Object> data) throws ParseException;

    ArrayList<Map<String, Object>> getUserData();

    ArrayList<Map<String, Object>> findByConditions(Integer targetSpotNumber, Date startTime, Date endTime);
}

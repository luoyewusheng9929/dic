package com.gzhu.dic_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhu.dic_platform.domain.UserData;
import com.gzhu.dic_platform.service.UserDataService;
import com.gzhu.dic_platform.mapper.UserDataMapper;
import org.springframework.stereotype.Service;

/**
* @author 23617
* @description 针对表【user_data】的数据库操作Service实现
* @createDate 2024-07-10 00:34:39
*/
@Service
public class UserDataServiceImpl extends ServiceImpl<UserDataMapper, UserData>
    implements UserDataService{

}





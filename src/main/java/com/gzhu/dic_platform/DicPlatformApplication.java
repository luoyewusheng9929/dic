package com.gzhu.dic_platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gzhu.dic_platform.mapper")
public class DicPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(DicPlatformApplication.class, args);
    }

}

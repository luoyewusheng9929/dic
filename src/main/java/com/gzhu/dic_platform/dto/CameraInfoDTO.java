package com.gzhu.dic_platform.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class CameraInfoDTO {
    /**
     * 设备编号
     */
    private String deviceNumber;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 安装地址
     */
    private String installAddress;

    /**
     * 安装时间
     */
    private Date installTime;

    /**
     * 上传的安装图片
     */
    private MultipartFile uploadInstallImg;
}

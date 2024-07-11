package com.gzhu.dic_platform.dto;

import lombok.Data;

@Data
public class CameraSettingDTO {
    /**
     * 焦距
     */
    private Integer focalLength;

    /**
     * 安装翻滚角度
     */
    private Integer rollAngle;

    /**
     * 安装水平角度
     */
    private Integer horizontalAngle;

    /**
     * 安装俯仰角度
     */
    private Integer pitchAngle;

    /**
     * 运行温度
     */
    private Integer temperature;

    /**
     * 曝光时间
     */
    private Integer exposureTime;

    /**
     * 帧率
     */
    private Integer frameRate;

    /**
     * 阈值上限
     */
    private Integer upperLimit;

    /**
     * 阈值下限
     */
    private Integer lowerLimit;

    /**
     * 图片上传周期
     */
    private Integer uploadInterval;

    /**
     * 信号强度
     */
    private Integer signalStrength;

    /**
     * 信噪比
     */
    private Integer signalRatio;
}

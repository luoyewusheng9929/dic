package com.gzhu.dic_platform.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName camera_setting
 */
@TableName(value ="camera_setting")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CameraSetting implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    /**
     * 相机
     */
    private String camera;

    /**
     * 所属项目
     */
    private String project;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
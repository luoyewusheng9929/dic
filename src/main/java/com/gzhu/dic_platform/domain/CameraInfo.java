package com.gzhu.dic_platform.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName camera_info
 */
@TableName(value ="camera_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CameraInfo implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 是否在线
     */
    private Integer online;

    /**
     * 离线时间
     */
    private Date lastTime;

    /**
     * 安装时间
     */
    private Date installTime;

    /**
     * 图像分辨率宽
     */
    private Integer rrWidth;

    /**
     * 图像分辨率高
     */
    private Double rrHeight;

    /**
     * 安装图片名称
     */
    private String installImg;

    /**
     * 基准图片名称
     */
    private String baseImg;

    /**
     * 最新图片名称
     */
    private String lastImg;

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

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
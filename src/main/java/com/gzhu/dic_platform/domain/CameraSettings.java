package com.gzhu.dic_platform.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName camera_settings
 */
@TableName(value ="camera_settings")
@Data
public class CameraSettings implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private Integer focalLength;

    /**
     * 
     */
    private Integer rollAngle;

    /**
     * 
     */
    private Integer horizontalAngle;

    /**
     * 
     */
    private Integer pitchAngle;

    /**
     * 
     */
    private Integer temperature;

    /**
     * 
     */
    private Integer exposureTime;

    /**
     * 
     */
    private Integer frameRate;

    /**
     * 
     */
    private Integer upperLimit;

    /**
     * 
     */
    private Integer lowerLimit;

    /**
     * 
     */
    private Integer uploadInterval;

    /**
     * 
     */
    private Integer signalStrength;

    /**
     * 
     */
    private Integer signalRatio;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
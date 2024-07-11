package com.gzhu.dic_platform.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName target_spot
 */
@TableName(value ="target_spot")
@Data
public class TargetSpot implements Serializable {
    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 靶点序号
     */
    private Integer spotNumber;

    /**
     * 靶点名称
     */
    private String spotName;

    /**
     * x坐标
     */
    private Integer xCoordinate;

    /**
     * y坐标
     */
    private Integer yCoordinate;

    /**
     * 距离
     */
    private Double distance;

    /**
     * 靶框宽度
     */
    private Integer width;

    /**
     * 靶框高度
     */
    private Integer height;

    /**
     * 所属相机
     */
    private String camera;

    /**
     * 所属项目
     */
    private String project;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package com.gzhu.dic_platform.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName target_spot
 */
@TableName(value ="target_spot")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
     * 是否为基准点
     */
    private Integer isBasepoint;

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
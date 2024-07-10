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
 * @TableName user_data
 */
@TableName(value ="user_data")
@Data
public class UserData implements Serializable {
    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 水平方向上的偏移
     */
    private Double offsetX;

    /**
     * 竖直方向上的偏移
     */
    private Double offsetY;

    /**
     * 第几个靶点
     */
    private Integer targetSpotNumber;

    /**
     * 这一帧对应的时间
     */
    private Date time;

    /**
     * 所属项目
     */
    private String project;

    /**
     * 对应拍摄相机
     */
    private String camera;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
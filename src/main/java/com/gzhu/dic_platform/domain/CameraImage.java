package com.gzhu.dic_platform.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName camera_image
 */
@TableName(value ="camera_image")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CameraImage implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图像名+格式
     */
    private String image;

    /**
     * 相机id
     */
    private String camera;

    /**
     * 所属项目
     */
    private String project;

    /**
     * 图像上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
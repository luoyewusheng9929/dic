package com.gzhu.dic_platform.dto;

import lombok.Data;

@Data
public class TargetSpotDTO {
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
}
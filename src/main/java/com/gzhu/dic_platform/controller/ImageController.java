package com.gzhu.dic_platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.domain.CameraImage;
import com.gzhu.dic_platform.service.CameraImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/img")
@Tag(name = "图像信息页面")
public class ImageController {

    @Autowired
    private CameraImageService cameraImageService;

    @GetMapping("/all/{deviceNumber}/{pageNum}/{pageSize}")
    @Operation(summary = "根据设备编号分页查询获取所有的图片信息")
    public Result getAllImg(@PathVariable String deviceNumber, @PathVariable int pageNum, @PathVariable int pageSize) {
        IPage<CameraImage> page = cameraImageService.getImgByPage(deviceNumber, pageNum, pageSize);
        return Result.ok().data("records", page.getRecords()).data("total", page.getTotal());
    }
}

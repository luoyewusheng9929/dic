package com.gzhu.dic_platform.controller.test;

import com.gzhu.dic_platform.common.exception.GlobalException;
import com.gzhu.dic_platform.common.utils.QiniuUploadUtil;
import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.common.utils.UploadImgUtil;
import com.gzhu.dic_platform.service.CameraInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/test")
@Tag(name = "测试页面")
public class TestController {

    @Autowired
    private CameraInfoService cameraInfoService;

    @GetMapping("/testResult")
    public Result testResult() {
        return Result.ok();
    }

    @GetMapping("/testException")
    public Result testException(@RequestParam Integer id) {
        if(id == null) {
            throw new NullPointerException();
        }
        if(id < 0) {
            throw new GlobalException("参数异常", 500);
        }
        return Result.ok().data("param", id).message("查询成功");
    }

    @GetMapping("/testLog")
    public Result testLog() {
        try{
            System.out.println(1/0);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
        return Result.ok().data("items", 111);
    }

    /**
     * 用于测试 Swagger 是否整合成功
     * @return
     */
    @Operation(summary = "测试 Swagger")
    @GetMapping("/testSwagger")
    public Result testSwagger() {
        return Result.ok().data("msg","111");
    }

    @Operation(summary = "测试图片上传")
    @PostMapping(value = "/testUploadImg",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result testUploadImg(@RequestPart("file") MultipartFile file) {
        // 文件校验
        if (file == null || file.isEmpty()) {
            throw new GlobalException("检测为空文件，请重新上传！");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename(); // 原始文件名
        String ext = FilenameUtils.getExtension(originalFilename); // 文件扩展名
        String uuid = UUID.randomUUID().toString().replace("-", ""); // 生成UUID
        String fileName = uuid + "." + ext;

        // 获取上传目录
        ApplicationHome applicationHome = new ApplicationHome(UploadImgUtil.class);
        String pre = applicationHome.getSource().getParentFile().getParentFile().getAbsolutePath() +
                "\\src\\main\\resources\\static\\images\\";
        String path = pre + fileName;

        // 保存文件
        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败", e);
        }

        return Result.ok().data("img", path);
    }

    @PostMapping("/testQiNiuUpload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("dn") String deviceNumber) {
        String fileUrl = QiniuUploadUtil.uploadImg(deviceNumber, file);
        return "文件上传成功，URL：" + fileUrl;
    }
}

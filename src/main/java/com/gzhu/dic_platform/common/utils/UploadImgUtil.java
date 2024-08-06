package com.gzhu.dic_platform.common.utils;

import com.gzhu.dic_platform.common.exception.GlobalException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UploadImgUtil {

    public static String uploadImg(String deviceNumber, MultipartFile file) {
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
        String directoryPath = pre + deviceNumber;
        String fullPath = directoryPath + "\\" + fileName;

        // 创建目录
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("无法创建目录: " + directoryPath);
            }
        }

        // 保存文件
        try {
            file.transferTo(new File(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败", e);
        }

        return fileName;
    }
}

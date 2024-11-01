package com.gzhu.dic_platform.common.utils;

import com.gzhu.dic_platform.common.exception.GlobalException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Component
public class QiniuUploadUtil {

    private static String accessKey;
    private static String secretKey;
    private static String bucket;
    private static String domain;

    private static final UploadManager uploadManager = new UploadManager(new Configuration());

    // 静态注入配置值
    @Value("${qiniu.access-key}")
    public void setAccessKey(String accessKey) {
        QiniuUploadUtil.accessKey = accessKey;
    }

    @Value("${qiniu.secret-key}")
    public void setSecretKey(String secretKey) {
        QiniuUploadUtil.secretKey = secretKey;
    }

    @Value("${qiniu.bucket}")
    public void setBucket(String bucket) {
        QiniuUploadUtil.bucket = bucket;
    }

    @Value("${qiniu.domain}")
    public void setDomain(String domain) {
        QiniuUploadUtil.domain = domain;
    }

    private static String getUploadToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket);
    }

    // 使用 MultipartFile 上传图片
    public static String uploadImg(String deviceNumber, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GlobalException("检测为空文件，请重新上传！");
        }

        String ext = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        String filePath = deviceNumber + "/" + fileName;

        try {
            Response response = uploadManager.put(file.getInputStream(), filePath, getUploadToken(), null, null);
            DefaultPutRet putRet = response.jsonToObject(DefaultPutRet.class);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    // 使用 byte[] 上传图片
    public static String uploadImg(String deviceNumber, byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new GlobalException("检测为空文件，请重新上传！");
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + ".png";
        String filePath = deviceNumber + "/" + fileName;

        try {
            Response response = uploadManager.put(imageBytes, filePath, getUploadToken());
            DefaultPutRet putRet = response.jsonToObject(DefaultPutRet.class);
//            return domain + "/" + putRet.key;
            return fileName;
        } catch (QiniuException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    // 获取文件扩展名
    private static String getExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "png"; // 默认扩展名
    }
}

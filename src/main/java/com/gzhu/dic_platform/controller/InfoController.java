package com.gzhu.dic_platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gzhu.dic_platform.common.exception.GlobalException;
import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.domain.CameraInfo;
import com.gzhu.dic_platform.dto.CameraInfoDTO;
import com.gzhu.dic_platform.service.CameraInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/info")
@Tag(name = "相机信息页面")
public class InfoController {

    @Autowired
    private CameraInfoService cameraInfoService;

    /**
     * 使用 @InitBinder 添加自定义日期转换器 局部生效
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
     */

    @GetMapping("/{deviceNumber}")
    @Operation(summary = "根据设备编号获取设备信息")
    public Result getInfoById(@PathVariable("deviceNumber") String deviceNumber) {
        CameraInfo result = cameraInfoService.getInfoByDeviceNumber(deviceNumber);
        return Result.ok().data("data", result);
    }

    @GetMapping("/all/{pageNum}/{pageSize}")
    @Operation(summary = "分页查询获取所有的设备信息")
    public Result getAllInfo(@PathVariable int pageNum, @PathVariable int pageSize) {
        IPage<CameraInfo> page = cameraInfoService.getAllInfoByPage(pageNum, pageSize);
        return Result.ok().data("records", page.getRecords()).data("total", page.getTotal());
    }

    @PostMapping("/add")
    @Operation(summary = "添加新的设备", description = "设备编号格式应为16位数字和小写字母的组合, 示例：{\"dn\": \"464dc717d6cb233e\"}")
    public Result addInfo(@Parameter(description = "设备编号", required = true) @RequestBody Map<Object, Object> requestParam) {
        String deviceNumber = (String) requestParam.get("dn");
        boolean flag = cameraInfoService.addInfo(deviceNumber);
        if (flag) return Result.ok(true, 200, "添加成功");
        else throw new GlobalException("添加失败");
    }

    @PutMapping(value = "/update")
    @Operation(summary = "更新设备信息")
    public Result updateInfo(CameraInfoDTO cameraInfoDTO) {
        boolean flag = cameraInfoService.updateInfo(cameraInfoDTO);
        if (flag) return Result.ok(true, 200, "更新成功");
        else throw new GlobalException("更新失败");
    }

    @PutMapping("/delete")
    @Operation(
            summary = "移除某个设备,实现逻辑删除",
            description = "根据设备编号逻辑删除设备",
            parameters = {
                    @Parameter(
                            name = "dn",
                            description = "设备编号，用于标识需要移除的设备",
                            required = true,
                            example = "c3f22a4184da1d98"
                    )
            }
    )
    public Result deleteInfo(@RequestParam("dn") String deviceNumber) {
        boolean flag = cameraInfoService.deleteInfoByDeviceNumber(deviceNumber);
        if (flag) return Result.ok(true, 200, "移除成功");
        else throw new GlobalException("移除失败");
    }
}


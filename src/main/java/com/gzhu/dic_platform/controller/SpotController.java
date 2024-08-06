package com.gzhu.dic_platform.controller;

import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.dto.TargetSpotDTO;
import com.gzhu.dic_platform.service.TargetSpotService;
import com.gzhu.dic_platform.vo.TargetSpotVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/spot")
@Tag(name = "靶点信息页面")
public class SpotController {
    @Autowired
    private TargetSpotService targetSpotService;


    @PostMapping
    @Operation( summary = "增加一个新的靶点" )
    public Result addTargetSpot(@RequestBody TargetSpotDTO targetSpotDTO) {
        try {
            targetSpotService.addTargetSpot(targetSpotDTO);
            return Result.ok(true, 200, "靶点添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error();
        }
    }

    @GetMapping("/nums/{deviceNumber}")
    @Operation( summary = "根据设备编号获取靶点数量")
    public Result getTargetSpotNums(@PathVariable("deviceNumber") String deviceNumber) {
        int nums = targetSpotService.getTargetSpotNums(deviceNumber);
        return Result.ok().data("nums", nums);
    }

    @GetMapping("/{deviceNumber}")
    @Operation( summary = "根据设备编号获取设备的所有靶点信息")
    public Result getTargetSpotByCameraId(@PathVariable("deviceNumber") String deviceNumber) {
        ArrayList<TargetSpotVO>list = targetSpotService.getTargetSpotByCameraId(deviceNumber);
        return Result.ok().data("records", list);
    }

    @PutMapping("/save")
    @Operation( summary = "根据设备编号修改靶点列表")
    public Result updateTargetSpot(@RequestBody List<TargetSpotDTO> spots){
        boolean success = targetSpotService.updateSpots(spots);
        if (success) {
            return Result.ok(true, 200, "保存成功");
        } else {
            return Result.error(false, 500, "保存失败");
        }
    }
}

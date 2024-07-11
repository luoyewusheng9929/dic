package com.gzhu.dic_platform.controller;

import com.gzhu.dic_platform.common.utils.Result;
import com.gzhu.dic_platform.domain.TargetSpot;
import com.gzhu.dic_platform.dto.TargetSpotDTO;
import com.gzhu.dic_platform.service.TargetSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/nums")
    @Operation( summary = "获取靶点数量")
    public Result getTargetSpotNums() {
        int nums = targetSpotService.getTargetSpotNums();
        return Result.ok().data("nums", nums);
    }
}

package com.gzhu.dic_platform.controller.test;

import com.gzhu.dic_platform.common.exception.GlobalException;
import com.gzhu.dic_platform.common.utils.ExceptionUtil;
import com.gzhu.dic_platform.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Tag(name = "测试页面")
public class TestController {
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
}

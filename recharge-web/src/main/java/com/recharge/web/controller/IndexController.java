package com.recharge.web.controller;

import com.recharge.common.result.Result;
import com.recharge.model.dto.response.IndexResponse;
import com.recharge.service.index.IndexService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器
 */
@Tag(name = "首页", description = "首页数据接口")
@RestController
@RequestMapping("/api/v1/index")
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;

    @Operation(summary = "获取首页数据")
    @GetMapping
    public Result<IndexResponse> getIndexData(@CurrentUser(required = false) Long userId) {
        return Result.success(indexService.getIndexData(userId));
    }
}

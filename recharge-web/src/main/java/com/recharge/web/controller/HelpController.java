package com.recharge.web.controller;

import com.recharge.common.result.Result;
import com.recharge.model.dto.response.HelpResponse;
import com.recharge.model.entity.HelpArticle;
import com.recharge.service.help.HelpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帮助中心控制器
 */
@Tag(name = "帮助中心", description = "帮助分类、文章查询")
@RestController
@RequestMapping("/api/v1/help")
@RequiredArgsConstructor
public class HelpController {

    private final HelpService helpService;

    @Operation(summary = "获取帮助中心数据")
    @GetMapping
    public Result<HelpResponse> getHelpData() {
        return Result.success(helpService.getHelpData());
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/articles/{articleId}")
    public Result<HelpArticle> getArticleDetail(@PathVariable Long articleId) {
        return Result.success(helpService.getArticleDetail(articleId));
    }

    @Operation(summary = "搜索文章")
    @GetMapping("/search")
    public Result<List<HelpArticle>> searchArticles(@RequestParam String keyword) {
        return Result.success(helpService.searchArticles(keyword));
    }
}

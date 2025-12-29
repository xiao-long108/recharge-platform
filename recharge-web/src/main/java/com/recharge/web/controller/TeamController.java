package com.recharge.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.model.entity.CommissionRecord;
import com.recharge.model.entity.User;
import com.recharge.service.team.TeamService;
import com.recharge.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队控制器
 */
@Tag(name = "团队管理")
@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "获取我的团队成员")
    @GetMapping("/members")
    public Result<List<User>> getTeamMembers(@CurrentUser Long userId,
                                              @RequestParam(required = false) Integer level) {
        List<User> members = teamService.getTeamMembers(userId, level);
        return Result.success(members);
    }

    @Operation(summary = "获取团队统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getTeamStats(@CurrentUser Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 一级成员数
        List<User> level1 = teamService.getTeamMembers(userId, 1);
        stats.put("level1Count", level1.size());

        // 二级成员数
        List<User> level2 = teamService.getTeamMembers(userId, 2);
        stats.put("level2Count", level2.size());

        // 三级成员数
        List<User> level3 = teamService.getTeamMembers(userId, 3);
        stats.put("level3Count", level3.size());

        // 总成员数
        stats.put("totalCount", level1.size() + level2.size() + level3.size());

        return Result.success(stats);
    }

    @Operation(summary = "获取佣金记录")
    @GetMapping("/commission/records")
    public Result<List<CommissionRecord>> getCommissionRecords(
            @CurrentUser Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CommissionRecord> page = teamService.getCommissionRecords(userId, pageNum, pageSize);
        return Result.success(page.getRecords());
    }
}

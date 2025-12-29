package com.recharge.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.model.dto.request.WithdrawRequest;
import com.recharge.model.entity.WithdrawRecord;
import com.recharge.service.withdraw.WithdrawService;
import com.recharge.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提现控制器
 */
@Tag(name = "提现管理")
@RestController
@RequestMapping("/api/withdraw")
@RequiredArgsConstructor
public class WithdrawController {

    private final WithdrawService withdrawService;

    @Operation(summary = "申请提现")
    @PostMapping("/apply")
    public Result<WithdrawRecord> applyWithdraw(@CurrentUser Long userId,
                                                 @RequestBody WithdrawRequest request) {
        WithdrawRecord record = withdrawService.applyWithdraw(userId, request.getBankAccountId(),
                request.getAmount(), request.getWithdrawType(), request.getPayPassword());
        return Result.success(record);
    }

    @Operation(summary = "获取提现记录")
    @GetMapping("/records")
    public Result<List<WithdrawRecord>> getRecords(@CurrentUser Long userId,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<WithdrawRecord> page = withdrawService.pageRecords(userId, pageNum, pageSize);
        return Result.success(page.getRecords());
    }

    @Operation(summary = "获取提现详情")
    @GetMapping("/{withdrawId}")
    public Result<WithdrawRecord> getWithdrawDetail(@CurrentUser Long userId,
                                                     @PathVariable Long withdrawId) {
        WithdrawRecord record = withdrawService.getWithdrawDetail(userId, withdrawId);
        return Result.success(record);
    }

    @Operation(summary = "取消提现")
    @PutMapping("/{withdrawId}/cancel")
    public Result<Void> cancelWithdraw(@CurrentUser Long userId,
                                       @PathVariable Long withdrawId) {
        withdrawService.cancelWithdraw(userId, withdrawId);
        return Result.success();
    }
}

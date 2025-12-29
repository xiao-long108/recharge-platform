package com.recharge.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.model.dto.request.YuebaoTransferRequest;
import com.recharge.model.entity.YuebaoAccount;
import com.recharge.model.entity.YuebaoRecord;
import com.recharge.service.yuebao.YuebaoService;
import com.recharge.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 余额宝控制器
 */
@Tag(name = "余额宝")
@RestController
@RequestMapping("/api/yuebao")
@RequiredArgsConstructor
public class YuebaoController {

    private final YuebaoService yuebaoService;

    @Operation(summary = "获取余额宝账户")
    @GetMapping("/account")
    public Result<YuebaoAccount> getAccount(@CurrentUser Long userId) {
        YuebaoAccount account = yuebaoService.getAccount(userId);
        return Result.success(account);
    }

    @Operation(summary = "转入余额宝")
    @PostMapping("/transfer-in")
    public Result<Void> transferIn(@CurrentUser Long userId,
                                   @RequestBody YuebaoTransferRequest request) {
        yuebaoService.transferIn(userId, request.getAmount(), request.getPayPassword());
        return Result.success();
    }

    @Operation(summary = "转出余额宝")
    @PostMapping("/transfer-out")
    public Result<Void> transferOut(@CurrentUser Long userId,
                                    @RequestBody YuebaoTransferRequest request) {
        yuebaoService.transferOut(userId, request.getAmount(), request.getPayPassword());
        return Result.success();
    }

    @Operation(summary = "获取余额宝记录")
    @GetMapping("/records")
    public Result<List<YuebaoRecord>> getRecords(@CurrentUser Long userId,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<YuebaoRecord> page = yuebaoService.pageRecords(userId, pageNum, pageSize);
        return Result.success(page.getRecords());
    }
}

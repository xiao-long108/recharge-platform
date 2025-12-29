package com.recharge.web.controller;

import com.recharge.common.result.Result;
import com.recharge.model.dto.request.AddBankAccountRequest;
import com.recharge.model.entity.BankAccount;
import com.recharge.service.bank.BankAccountService;
import com.recharge.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 银行账户控制器
 */
@Tag(name = "银行账户")
@RestController
@RequestMapping("/api/bank-account")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Operation(summary = "获取银行账户列表")
    @GetMapping("/list")
    public Result<List<BankAccount>> listAccounts(@CurrentUser Long userId) {
        List<BankAccount> accounts = bankAccountService.listAccounts(userId);
        return Result.success(accounts);
    }

    @Operation(summary = "添加银行账户")
    @PostMapping("/add")
    public Result<BankAccount> addAccount(@CurrentUser Long userId,
                                          @RequestBody AddBankAccountRequest request) {
        BankAccount account = bankAccountService.addAccount(userId, request.getBankName(),
                request.getAccountHolder(), request.getAccountNumber(), request.getBranchName());
        return Result.success(account);
    }

    @Operation(summary = "删除银行账户")
    @DeleteMapping("/{accountId}")
    public Result<Void> deleteAccount(@CurrentUser Long userId,
                                      @PathVariable Long accountId) {
        bankAccountService.deleteAccount(userId, accountId);
        return Result.success();
    }

    @Operation(summary = "设为默认账户")
    @PutMapping("/{accountId}/default")
    public Result<Void> setDefault(@CurrentUser Long userId,
                                   @PathVariable Long accountId) {
        bankAccountService.setDefault(userId, accountId);
        return Result.success();
    }

    @Operation(summary = "获取账户详情")
    @GetMapping("/{accountId}")
    public Result<BankAccount> getAccount(@CurrentUser Long userId,
                                          @PathVariable Long accountId) {
        BankAccount account = bankAccountService.getAccount(userId, accountId);
        return Result.success(account);
    }
}

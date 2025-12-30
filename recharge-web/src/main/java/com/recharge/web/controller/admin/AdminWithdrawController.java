package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.AccountFlowMapper;
import com.recharge.mapper.BankAccountMapper;
import com.recharge.mapper.UserMapper;
import com.recharge.model.entity.AccountFlow;
import com.recharge.model.entity.BankAccount;
import com.recharge.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 提现管理控制器
 * 使用原数据库 cloud_times_api_fund_detail 表 (data_type=1 为提现申请)
 */
@Tag(name = "管理后台-提现管理")
@RestController
@RequestMapping("/api/admin/withdraws")
@RequiredArgsConstructor
public class AdminWithdrawController {

    private final AccountFlowMapper accountFlowMapper;
    private final UserMapper userMapper;
    private final BankAccountMapper bankAccountMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Operation(summary = "提现统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 待审核数量
        Long pendingCount = accountFlowMapper.selectCount(
            new LambdaQueryWrapper<AccountFlow>()
                .eq(AccountFlow::getDataType, 1)
                .eq(AccountFlow::getStatus, 3)
        );
        stats.put("pendingCount", pendingCount);

        // 今日提现金额 (成功的)
        BigDecimal todayAmount = accountFlowMapper.sumAmountByCondition(1, 1, todayStart, null);
        stats.put("todayAmount", todayAmount != null ? todayAmount : BigDecimal.ZERO);

        // 本月提现金额 (成功的)
        BigDecimal monthAmount = accountFlowMapper.sumAmountByCondition(1, 1, monthStart, null);
        stats.put("monthAmount", monthAmount != null ? monthAmount : BigDecimal.ZERO);

        // 累计提现金额 (成功的)
        BigDecimal totalAmount = accountFlowMapper.sumAmountByCondition(1, 1, null, null);
        stats.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);

        return Result.success(stats);
    }

    @Operation(summary = "提现申请列表")
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "status", required = false) Integer status) {

        LambdaQueryWrapper<AccountFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFlow::getDataType, 1); // 1 = 申请提款
        wrapper.eq(userId != null, AccountFlow::getUserId, userId);
        wrapper.eq(status != null, AccountFlow::getStatus, status);
        wrapper.orderByDesc(AccountFlow::getCreateTime);

        Page<AccountFlow> page = accountFlowMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 转换为前端需要的格式，包含用户信息和银行信息
        List<Map<String, Object>> records = page.getRecords().stream()
                .map(this::convertToFrontendFormat)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());

        return Result.success(result);
    }

    /**
     * 转换为前端格式，包含用户和银行信息
     */
    private Map<String, Object> convertToFrontendFormat(AccountFlow flow) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", flow.getId());
        map.put("userId", flow.getUserId());
        map.put("price", flow.getPrice());
        map.put("status", flow.getStatus());
        map.put("reason", flow.getReason());
        map.put("createTime", flow.getCreateTime() != null ? flow.getCreateTime().format(FORMATTER) : null);
        map.put("updateTime", flow.getUpdateTime() != null ? flow.getUpdateTime().format(FORMATTER) : null);

        // 获取用户信息
        if (flow.getUserId() != null) {
            User user = userMapper.selectById(flow.getUserId());
            if (user != null) {
                map.put("userNickname", user.getNickname());
                map.put("userAvatar", user.getHead());
                map.put("userMobile", user.getMobile());
            } else {
                map.put("userNickname", "用户" + flow.getUserId());
                map.put("userAvatar", null);
                map.put("userMobile", null);
            }

            // 获取银行账户信息 (默认账户)
            BankAccount bank = bankAccountMapper.selectOne(
                new LambdaQueryWrapper<BankAccount>()
                    .eq(BankAccount::getUserId, flow.getUserId())
                    .eq(BankAccount::getIsDefault, 1)
                    .last("LIMIT 1")
            );
            if (bank != null) {
                map.put("bankName", bank.getBankName());
                map.put("accountNumber", bank.getAccountNumber());
                map.put("accountName", bank.getAccountName());
            } else {
                map.put("bankName", null);
                map.put("accountNumber", null);
                map.put("accountName", null);
            }
        }

        return map;
    }

    @Operation(summary = "提现详情")
    @GetMapping("/{withdrawId}")
    public Result<AccountFlow> detail(@PathVariable("withdrawId") Long withdrawId) {
        AccountFlow flow = accountFlowMapper.selectById(withdrawId);
        return Result.success(flow);
    }

    @Operation(summary = "审核通过")
    @PostMapping("/{withdrawId}/approve")
    public Result<Void> approve(@PathVariable("withdrawId") Long withdrawId,
                                @RequestBody(required = false) Map<String, String> body) {
        AccountFlow flow = accountFlowMapper.selectById(withdrawId);
        if (flow == null) {
            return Result.fail("记录不存在");
        }

        if (flow.getStatus() != 3) { // 3 = 等待审核
            return Result.fail("当前状态不可审核");
        }

        // 更新状态为成功
        flow.setStatus(1); // 1 = 成功
        flow.setUpdateTime(LocalDateTime.now());
        if (body != null && body.get("remark") != null) {
            flow.setNode(body.get("remark"));
        }
        accountFlowMapper.updateById(flow);

        return Result.success();
    }

    @Operation(summary = "审核拒绝")
    @PostMapping("/{withdrawId}/reject")
    public Result<Void> reject(@PathVariable("withdrawId") Long withdrawId,
                               @RequestBody Map<String, String> body) {
        AccountFlow flow = accountFlowMapper.selectById(withdrawId);
        if (flow == null) {
            return Result.fail("记录不存在");
        }

        if (flow.getStatus() != 3) { // 3 = 等待审核
            return Result.fail("当前状态不可审核");
        }

        String reason = body.get("reason");

        // 退回用户余额
        User user = userMapper.selectById(flow.getUserId());
        if (user != null && flow.getPrice() != null) {
            user.setPrice(user.getPrice().add(flow.getPrice()));
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }

        // 更新状态为失败
        flow.setStatus(2); // 2 = 失败
        flow.setReason(reason);
        flow.setUpdateTime(LocalDateTime.now());
        accountFlowMapper.updateById(flow);

        return Result.success();
    }
}

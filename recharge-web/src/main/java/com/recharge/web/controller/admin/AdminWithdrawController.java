package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.AccountFlowMapper;
import com.recharge.mapper.UserMapper;
import com.recharge.model.entity.AccountFlow;
import com.recharge.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

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

    @Operation(summary = "提现申请列表")
    @GetMapping
    public Result<Page<AccountFlow>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "status", required = false) Integer status) {

        LambdaQueryWrapper<AccountFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFlow::getDataType, 1); // 1 = 申请提款
        wrapper.eq(status != null, AccountFlow::getStatus, status);
        wrapper.orderByDesc(AccountFlow::getCreateTime);

        Page<AccountFlow> page = accountFlowMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @Operation(summary = "提现详情")
    @GetMapping("/{withdrawId}")
    public Result<AccountFlow> detail(@PathVariable Long withdrawId) {
        AccountFlow flow = accountFlowMapper.selectById(withdrawId);
        return Result.success(flow);
    }

    @Operation(summary = "审核通过")
    @PostMapping("/{withdrawId}/approve")
    public Result<Void> approve(@PathVariable Long withdrawId,
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
    public Result<Void> reject(@PathVariable Long withdrawId,
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

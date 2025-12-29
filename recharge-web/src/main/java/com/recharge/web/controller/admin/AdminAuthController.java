package com.recharge.web.controller.admin;

import com.recharge.common.result.Result;
import com.recharge.common.utils.JwtUtils;
import com.recharge.mapper.AdminMapper;
import com.recharge.mapper.AdminPermissionMapper;
import com.recharge.model.entity.Admin;
import com.recharge.model.entity.AdminPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 认证控制器
 */
@Tag(name = "管理后台-认证")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final JwtUtils jwtUtils;
    private final AdminMapper adminMapper;
    private final AdminPermissionMapper adminPermissionMapper;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody AdminLoginRequest request) {
        // 从数据库查询管理员
        Admin admin = adminMapper.selectByUsername(request.getUsername());
        if (admin == null) {
            return Result.fail("用户名或密码错误");
        }

        // 验证密码 (MD5)
        String inputPasswordMd5 = DigestUtils.md5DigestAsHex(
                request.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!inputPasswordMd5.equals(admin.getPassword())) {
            return Result.fail("用户名或密码错误");
        }

        // 检查状态
        if (admin.getStatus() != null && admin.getStatus() != 1) {
            return Result.fail("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        data.put("role", admin.getStype() == 1 ? "admin" : "agent");

        return Result.success(data);
    }

    @Operation(summary = "获取管理员信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(@RequestHeader("Authorization") String authorization) {
        // 从token解析用户名 (存储在phone字段中)
        String token = authorization.replace("Bearer ", "");
        String username = jwtUtils.getPhoneFromToken(token);

        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            return Result.fail("管理员不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        data.put("role", admin.getStype() == 1 ? "admin" : "agent");
        data.put("avatar", "");
        return Result.success(data);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @Operation(summary = "获取菜单列表")
    @GetMapping("/menus")
    public Result<List<Map<String, Object>>> getMenus(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String username = jwtUtils.getPhoneFromToken(token);

        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            return Result.fail("管理员不存在");
        }

        // 查询管理员的权限菜单
        List<AdminPermission> permissions;
        if (admin.getStype() != null && admin.getStype() == 1) {
            // 超级管理员获取所有菜单
            permissions = adminPermissionMapper.selectAllEnabled();
        } else {
            // 普通管理员根据角色获取菜单
            permissions = adminPermissionMapper.selectByAdminId(admin.getId());
        }

        // 构建树形菜单
        List<Map<String, Object>> menus = buildMenuTree(permissions, 0);
        return Result.success(menus);
    }

    /**
     * 构建树形菜单
     */
    private List<Map<String, Object>> buildMenuTree(List<AdminPermission> permissions, int parentId) {
        List<Map<String, Object>> result = new ArrayList<>();

        List<AdminPermission> children = permissions.stream()
                .filter(p -> p.getPid() != null && p.getPid() == parentId)
                .collect(Collectors.toList());

        for (AdminPermission permission : children) {
            Map<String, Object> menu = new HashMap<>();
            menu.put("id", permission.getId());
            menu.put("title", permission.getTitle());
            menu.put("href", permission.getHref());
            menu.put("icon", permission.getIcon());
            menu.put("type", permission.getType());

            List<Map<String, Object>> subMenus = buildMenuTree(permissions, permission.getId());
            if (!subMenus.isEmpty()) {
                menu.put("children", subMenus);
            }

            result.add(menu);
        }

        return result;
    }

    @Data
    public static class AdminLoginRequest {
        private String username;
        private String password;
    }
}

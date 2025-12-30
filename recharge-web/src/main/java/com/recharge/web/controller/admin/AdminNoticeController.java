package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.NoticeMapper;
import com.recharge.model.entity.Notice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 公告管理控制器
 * 对应原表: cloud_times_api_journalism
 */
@Tag(name = "管理后台-公告管理")
@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeMapper noticeMapper;

    @Operation(summary = "公告列表")
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "status", required = false) Integer status) {

        // 老表使用 delete_time 作为软删除
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Notice::getTitle, keyword);
        }

        // 按 sort 降序, create_time 降序排列
        wrapper.orderByDesc(Notice::getSort)
                .orderByDesc(Notice::getCreateTime);

        Page<Notice> page = noticeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 转换为前端格式
        List<Map<String, Object>> records = page.getRecords().stream()
                .map(this::convertToFrontendFormat)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        result.put("pages", page.getPages());

        return Result.success(result);
    }

    @Operation(summary = "公告统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        // 总公告数
        Long total = noticeMapper.selectCount(null);
        stats.put("total", total);
        stats.put("published", total);  // 老表没有状态字段，全部算已发布
        stats.put("draft", 0L);

        // 本月新增
        java.time.LocalDateTime monthStart = java.time.LocalDateTime.of(
            java.time.LocalDate.now().withDayOfMonth(1), java.time.LocalTime.MIN);
        Long monthCount = noticeMapper.selectCount(
            new LambdaQueryWrapper<Notice>().ge(Notice::getCreateTime, monthStart)
        );
        stats.put("monthCount", monthCount);

        return Result.success(stats);
    }

    @Operation(summary = "公告详情")
    @GetMapping("/{noticeId}")
    public Result<Map<String, Object>> detail(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            return Result.fail("公告不存在");
        }
        return Result.success(convertToFrontendFormat(notice));
    }

    @Operation(summary = "添加公告")
    @PostMapping
    public Result<Void> add(@RequestBody NoticeRequest request) {
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setImage(request.getImage());
        notice.setSort(request.getIsTop() != null && request.getIsTop() == 1 ? 999 : 0);
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());

        noticeMapper.insert(notice);
        return Result.success();
    }

    @Operation(summary = "修改公告")
    @PutMapping("/{noticeId}")
    public Result<Void> update(@PathVariable("noticeId") Long noticeId,
                               @RequestBody NoticeRequest request) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            return Result.fail("公告不存在");
        }

        if (request.getTitle() != null) {
            notice.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            notice.setContent(request.getContent());
        }
        if (request.getImage() != null) {
            notice.setImage(request.getImage());
        }
        if (request.getIsTop() != null) {
            notice.setSort(request.getIsTop() == 1 ? 999 : 0);
        }
        notice.setUpdateTime(LocalDateTime.now());

        noticeMapper.updateById(notice);
        return Result.success();
    }

    @Operation(summary = "置顶/取消置顶公告")
    @PutMapping("/{noticeId}/top")
    public Result<Void> toggleTop(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            return Result.fail("公告不存在");
        }

        // 切换置顶状态
        notice.setSort(notice.getSort() != null && notice.getSort() > 0 ? 0 : 999);
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
        return Result.success();
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{noticeId}")
    public Result<Void> delete(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            return Result.fail("公告不存在");
        }

        // 物理删除
        noticeMapper.deleteById(noticeId);
        return Result.success();
    }

    /**
     * 转换为前端格式
     */
    private Map<String, Object> convertToFrontendFormat(Notice notice) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", notice.getId());
        map.put("title", notice.getTitle());
        map.put("content", notice.getContent());
        map.put("image", notice.getImage());
        map.put("type", 1); // 老表没有类型字段，默认系统公告
        map.put("isTop", notice.getSort() != null && notice.getSort() > 0 ? 1 : 0);
        map.put("viewCount", 0); // 老表没有浏览次数
        map.put("status", 1); // 老表没有状态字段，默认上架
        map.put("sort", notice.getSort());
        // 前端期望 createTime 格式
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        map.put("createTime", notice.getCreateTime() != null ? notice.getCreateTime().format(formatter) : null);
        map.put("updateTime", notice.getUpdateTime() != null ? notice.getUpdateTime().format(formatter) : null);
        return map;
    }

    @Data
    public static class NoticeRequest {
        private String title;
        private String content;
        private String image;
        private Integer noticeType;
        private Integer type;  // 前端发送 type
        private Integer isTop;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer status;

        // 获取类型，优先使用 type
        public Integer getTypeValue() {
            return type != null ? type : noticeType;
        }
    }
}

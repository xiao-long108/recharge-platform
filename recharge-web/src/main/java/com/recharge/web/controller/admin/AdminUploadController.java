package com.recharge.web.controller.admin;

import com.recharge.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理后台 - 文件上传控制器
 */
@Slf4j
@Tag(name = "管理后台-文件上传")
@RestController
@RequestMapping("/api/admin/upload")
public class AdminUploadController {

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/uploads}")
    private String urlPrefix;

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "images");
    }

    @Operation(summary = "上传文件")
    @PostMapping("/file")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "files");
    }

    /**
     * 通用文件上传方法
     */
    private Result<Map<String, String>> uploadFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            return Result.fail("请选择要上传的文件");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.fail("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if ("images".equals(subDir)) {
            if (!isImageExtension(extension)) {
                return Result.fail("只支持上传图片文件 (jpg, jpeg, png, gif, webp)");
            }
        }

        try {
            // 生成存储路径: /uploads/images/20251229/uuid.jpg
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

            // 构建完整的文件存储路径
            String relativePath = "/" + subDir + "/" + dateDir;
            Path dirPath = Paths.get(System.getProperty("user.dir") + uploadPath + relativePath);

            // 创建目录
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 保存文件
            Path filePath = dirPath.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 返回访问URL
            String fileUrl = urlPrefix + relativePath + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("name", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            log.info("文件上传成功: {} -> {}", originalFilename, fileUrl);

            return Result.success(result);

        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }

    /**
     * 判断是否为图片扩展名
     */
    private boolean isImageExtension(String extension) {
        return "jpg".equals(extension) || "jpeg".equals(extension)
            || "png".equals(extension) || "gif".equals(extension)
            || "webp".equals(extension);
    }
}

package com.recharge.common.result;

import com.recharge.common.enums.ResultCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
@Schema(description = "统一响应结果")
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码")
    private Integer code;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "数据")
    private T data;

    @Schema(description = "时间戳")
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> failed() {
        return failed(ResultCodeEnum.FAILED);
    }

    /**
     * 失败响应（错误码枚举）
     */
    public static <T> Result<T> failed(ResultCodeEnum codeEnum) {
        return new Result<>(codeEnum.getCode(), codeEnum.getMessage(), null);
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCodeEnum.FAILED.getCode(), message, null);
    }

    /**
     * 失败响应（别名）
     */
    public static <T> Result<T> fail(String message) {
        return failed(message);
    }

    /**
     * 失败响应（自定义状态码和消息）
     */
    public static <T> Result<T> failed(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 参数校验失败
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(ResultCodeEnum.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized() {
        return failed(ResultCodeEnum.UNAUTHORIZED);
    }

    /**
     * 无权限
     */
    public static <T> Result<T> forbidden() {
        return failed(ResultCodeEnum.FORBIDDEN);
    }
}

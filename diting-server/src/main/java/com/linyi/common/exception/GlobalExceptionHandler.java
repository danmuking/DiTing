package com.linyi.common.exception;

import com.linyi.common.domain.vo.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @program: DiTing
 * @description: 全局异常处理
 * @author: lin
 * @create: 2024-01-16 19:39
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * @param e:
     * @return ApiResult
     * @description 参数校验异常
     * @date 2024/1/16 19:41
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException e){
        StringBuilder stringBuilder = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x -> stringBuilder.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String substring = stringBuilder.substring(0, stringBuilder.length() - 1);
        log.info("validation parameters error！The reason is:{}", substring);
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(),substring);
    }
    /**
     * @param e:
     * @return ApiResult
     * @description 未知异常
     * @date 2024/1/16 19:47
     */
    @ExceptionHandler(value = Exception.class)
    public ApiResult systemExceptionHandler(Exception e) {
        log.error("system exception！The reason is：{}", e.getMessage(), e);
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }
}

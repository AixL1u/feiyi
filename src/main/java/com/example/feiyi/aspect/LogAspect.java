package com.example.feiyi.aspect;

import com.example.feiyi.annotation.Log;
import com.example.feiyi.entity.OperationLog;
import com.example.feiyi.mapper.OperationLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperationLogMapper logMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(com.example.feiyi.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long duration = System.currentTimeMillis() - startTime;

        try {
            saveLog(point, duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint point, long duration) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        OperationLog log = new OperationLog();
        log.setOperation(logAnnotation.value());
        log.setMethod(point.getTarget().getClass().getSimpleName() + "." + method.getName());
        log.setDuration(duration);
        log.setCreateTime(LocalDateTime.now());

        // 获取请求参数（过滤不可序列化的参数）
        try {
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                List<Object> filteredArgs = new ArrayList<>();
                for (Object arg : args) {
                    if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                            || arg instanceof MultipartFile || arg instanceof Authentication) {
                        continue;
                    }
                    filteredArgs.add(arg);
                }
                if (!filteredArgs.isEmpty()) {
                    String params = objectMapper.writeValueAsString(filteredArgs);
                    if (params.length() > 500) {
                        params = params.substring(0, 500) + "...";
                    }
                    log.setParams(params);
                }
            }
        } catch (Exception e) {
            log.setParams("参数序列化失败");
        }

        // 获取当前用户（principal=userId, details=username）
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            log.setUserId((Long) auth.getPrincipal());
            Object details = auth.getDetails();
            if (details instanceof String) {
                log.setUsername((String) details);
            } else {
                log.setUsername("用户#" + auth.getPrincipal());
            }
        }

        // 获取IP
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            log.setIp(ip);
        }

        logMapper.insert(log);
    }
}

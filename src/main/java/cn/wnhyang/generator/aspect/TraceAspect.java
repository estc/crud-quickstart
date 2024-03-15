package cn.wnhyang.generator.aspect;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * @author wnhyang
 * @date 2024/3/14
 **/
@Aspect
@Slf4j
@Component
public class TraceAspect {

    /**
     * 对所有controller切面
     *
     * @param pjp ProceedingJoinPoint
     * @return 结果
     * @throws Throwable 异常
     */
    @Around("execution(* cn.wnhyang.generator.controller.*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        try {
            String traceId = IdUtil.simpleUUID();
            MDC.put("traceId", traceId);
            return pjp.proceed();
        } finally {
            MDC.clear();
        }
    }
}

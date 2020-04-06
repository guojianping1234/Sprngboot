package com.huawei.gjp.logAspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {
    private static final String SPLIT_SYMBOL = "|";


    @Pointcut("execution(public * com.huawei.gjp.demo.*.*(..)) || @annotation(AnoDot)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object res = null;
        String req = null;
        long start = System.currentTimeMillis();
        try {
            req = buildReqLog(proceedingJoinPoint);
            res = proceedingJoinPoint.proceed();
            return res;
        } catch (Throwable e) {
            res = "Un-Expect-Error";
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            System.out.println(req + "" + JSON.toJSONString(res) + SPLIT_SYMBOL + (end - start));
        }
    }


    private String buildReqLog(ProceedingJoinPoint joinPoint) {
        // 目标对象
        Object target = joinPoint.getTarget();
        // 执行的方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 请求参数
        Object[] args = joinPoint.getArgs();

        StringBuilder builder = new StringBuilder(target.getClass().getName());
        builder.append(SPLIT_SYMBOL).append(method.getName()).append(SPLIT_SYMBOL);
        for (Object arg : args) {
            builder.append(JSON.toJSONString(arg)).append(",");
        }
        return builder.substring(0, builder.length() - 1) + SPLIT_SYMBOL;
    }
}

package com.piterjk.springbootdemo.common.aspact;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PostAspact {
    @Before("execution(* com.piterjk.springbootdemo.post.service.impl.*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        System.out.println("메서드 실행 전: " + joinPoint.getSignature().getName());
    }

    @After("execution(* com.piterjk.springbootdemo.post.service.impl.*.*(..))")
    public void logAfterMethod(JoinPoint joinPoint) {
        System.out.println("메서드 실행 후: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.piterjk.springbootdemo.post.service.impl.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("메서드 실행 후 반환값: " + result);
    }

    @AfterThrowing(pointcut = "execution(* com.piterjk.springbootdemo.post.service.impl.*.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        System.out.println("예외 발생: " + exception.getMessage());
    }

    @Around("execution(* com.piterjk.springbootdemo.post.service.impl.*.*(..))")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Around 메서드 실행 전: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        System.out.println("Around 메서드 실행 후: " + joinPoint.getSignature().getName());
        return result;
    }
}

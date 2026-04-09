package se.uu.ebc.bemanning.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@Profile("dev")
public class LogTimingAspect {

    // Define a pointcut for all methods in the service package
    // You could also refine this to specific annotations or method patterns
//    @Around("execution(* se.uu.ebc.bemanning.service.*.*(..))")
	public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        String methodName = joinPoint.getSignature().toShortString();
        String className = joinPoint.getTarget().getClass().getName();

        log.debug("Entering method: {}.{}() with args: {}", className, methodName, joinPoint.getArgs());

        Object result = null;
        try {
            result = joinPoint.proceed(); // Execute the actual method
//            log.debug("Exiting method: {}.{}() with result: {}", className, methodName, result);
        } catch (Throwable ex) {
            log.error("Method {}.{}() threw an exception: {}", className, methodName, ex.getMessage());
            throw ex; // Re-throw the exception
        } finally {
            long endTime = System.nanoTime();
            long duration = (endTime - startTime)/1000000;
            log.debug("Method {}.{}() executed in {} ms", className, methodName, duration);
        }
        return result;
    }
}

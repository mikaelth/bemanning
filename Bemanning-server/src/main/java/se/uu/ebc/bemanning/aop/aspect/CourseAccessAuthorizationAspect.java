package se.uu.ebc.bemanning.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.uu.ebc.bemanning.aop.annotation.RequiresCoursePermission;

@Aspect
@Component
public class CourseAccessAuthorizationAspect {

	@Around("@annotation(requiresCoursePermission)")
	public Object checkCoursePermission(ProceedingJoinPoint joinPoint, RequiresCoursePermission requiresCoursePermission) throws Throwable {
		String requiredPermission = requiresCoursePermission.value();
		// Logic to check if the current user has \`requiredPermission\`
		// If not, throw AccessDeniedException
		// If yes, joinPoint.proceed();
		return joinPoint.proceed();
	}

}

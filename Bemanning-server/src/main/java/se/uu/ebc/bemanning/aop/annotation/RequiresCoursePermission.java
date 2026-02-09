package se.uu.ebc.bemanning.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresCoursePermission {
    String value(); // The permission string, e.g., "ADMIN", "READ_PRODUCTS"
}

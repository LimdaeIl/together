package com.book.together.common.annotation;

import com.book.together.auth.entity.MemberRole;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequireRole {
    MemberRole[] value() default {};
    boolean allowAnonymous() default false;
}
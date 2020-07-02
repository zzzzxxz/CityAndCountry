package com.example.demo.area.rateLimit;

import java.lang.annotation.*;

/**
 * @author zx
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    double limitNum() default 20;  //默认每秒放入桶中的token
}


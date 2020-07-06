package com.o2osys.log.client.annotation;

import java.lang.annotation.*;

/**
 * @Author chen
 * @Description
 * @Date 2020/7/3 15:16
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OperationLogIgnore {

    String value() default "";

}

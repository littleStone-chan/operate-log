package com.chen.log.client.annotation;

import com.chen.log.client.enums.OperateLogTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author chen
 * @Description 操作日志注解，在使用的方法上做此注解
 * @Date 2020/6/17 16:03
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface OperationLog {

    @AliasFor("tableName")
    String value() default "";

    /**
     * 表名
     */
    @AliasFor("value")
    String tableName() default  "";

    /**
     * 操作类型
     */
    OperateLogTypeEnum type() default OperateLogTypeEnum.UPDATE;

    /**
     * 备注
     */
    String remark() default "";

    /**
     * 是否主表
     * 如果有@Transactional注解 需要标记此字段
     * 否则不会自动提交表的基础信息
     * 如果没有 @Transactional注解  则无视此字段
     * @return
     */
    boolean primaryTable() default false;
}

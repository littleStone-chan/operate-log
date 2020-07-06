package com.chen.log.client.annotation;

import com.chen.log.client.enums.AliasTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Author chen
 * @Description 操作日志别名
 * @Date 2020/6/22 08:32
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OperationLogAlias {



    /**
     * 字典表的key
     * @return
     */
    @AliasFor("key")
    String key() default "";

    /**
     * 别名类型
     * @return
     */
    @AliasFor("type") AliasTypeEnum type() default AliasTypeEnum.DICTIONARY;

    /**
     * 库名
     * @return
     */
    @AliasFor("dataBaseName")
    String dataBaseName() default "";
    /**
     * 表名
     * @return
     */
    @AliasFor("tableName")
    String tableName() default "";

    /**
     * 字段名
     * @return
     */
    @AliasFor("fieldName")
    String fieldName() default "";



    /**
     * 显示名字的别名
     * 如果是空的话，则取表的注释
     * @return
     */
    String attributeAlias() default "";



    /**
     * 注解在参数上的时候 需要加上字段名对应数据库上的字段的名字
     * 比如 数据库是 user_name  则此参数需要传userName
     * @return
     */
//    String fieldName() default "";

}

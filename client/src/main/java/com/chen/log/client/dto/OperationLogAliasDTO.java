package com.chen.log.client.dto;

import lombok.Data;

/**
 * @Author chen
 * @Description @OperationLogAlias 注解 参数的数据所存的值
 * @Date 2020/6/22 16:00
 **/
@Data
public class OperationLogAliasDTO {

    /**
     * 字典表的key
     */
    String key;

    /**
     * 属性的别名
     */
    String attributeAlias;

    /**
     * 如果是类 则类的 ClassName
     */
    String objClassName;
}

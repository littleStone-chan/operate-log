package com.chen.log.client.dto;

import com.chen.log.client.enums.OperateLogTypeEnum;
import lombok.Data;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/17 17:03
 **/
@Data
public class AnnotationAopOperateLogDTO {

    /**
     * 是否添加操作日志
     */
    Boolean operateLog;

    /**
     * 表名字
     */
    String tableName;

    /**
     * 所对应的类的className
     */
    private String tableClassName;

    /**
     * 操作类型
     */
    OperateLogTypeEnum operateType;

    /**
     * 备注
     */
    String remark;

    /**
     * 是否主表
     */
    Boolean primaryTable;
}

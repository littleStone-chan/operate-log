package com.chen.log.client.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 */
@Getter
public enum OperateLogTypeEnum {

    UPDATE("update","修改"),
    INSERT("insert","插入");

    OperateLogTypeEnum(String type, String alias){
        this.type = type;
        this.alias = alias;
    }

    /**
     * 操作类型
     */
    String type;

    /**
     * 操作别名
     */
    String alias;


}

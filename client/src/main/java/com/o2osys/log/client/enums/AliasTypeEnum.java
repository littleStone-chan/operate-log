package com.o2osys.log.client.enums;

/**
 * 操作别名枚举
 */
public enum AliasTypeEnum {


    DICTIONARY("dictionary","字典表里取值"),
    TABLE("table","数据库表里取值");

    AliasTypeEnum(String key, String value){
        this.key = key;
        this.value = value;
    }

    /**
     * 操作类型
     */
    String key;

    /**
     * 操作别名
     */
    String value;


}

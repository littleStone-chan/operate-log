package com.o2osys.log.server.po;

import lombok.Data;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/16 09:48
 **/
@Data
public class Attribute {

    private String id;

    private String operationId;

    private String attributeType;

    private String attributeName;

    private String attributeAlias;

    private String oldValue;

    private String newValue;

    private String remark;
}

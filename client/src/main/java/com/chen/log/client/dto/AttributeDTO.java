package com.chen.log.client.dto;

import lombok.Data;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/16 17:21
 **/
@Data
public class AttributeDTO {


    private String id;

    private String operationId;

    /**
     * 所对应的类的className
     */
    private String tableClassName;

    private String attributeType;

    private String attributeName;

    private String attributeAlias;

    private Object oldValue;

    private Object newValue;


}

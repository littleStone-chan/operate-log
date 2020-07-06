package com.chen.log.client.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/15 16:47
 **/

@Data
public class OperationLogDTO {

    /**
     * 应用名
     */
    private String appName;

    /**
     * 类名
     */
    private String tableName;

    /**
     * 类的关联id
     */
    private String objectId;

    /**
     * 分组id  表示在同一组
     */
    private String groupId;

    /**
     * 操作人姓名
     */
    private String operator;

    /**
     * 操作名
     */
    private String operationType;

    /**
     * 操作别名
     */
    private String operationAlias;

    /**
     * 备注
     */
    private String remark;

    /**
     * 内容
     */
    private String comment;

    /**
     * 属性
     */
    List<AttributeDTO> attributeDTOList = new ArrayList();
}

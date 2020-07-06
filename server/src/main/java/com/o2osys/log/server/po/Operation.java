package com.o2osys.log.server.po;

import lombok.Data;

import java.util.Date;

@Data
public class Operation {

    private String id;
    private String appName;
    private String tableName;
    private String objectId;
    private String groupId;
    private String operator;
    private String operatorId;
    private String operationType;
    private String operationAlias;
    private String remark;
    private String comment;
    private Date createTime;
}

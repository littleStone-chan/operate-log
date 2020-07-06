package com.o2osys.log.server.vo;

import com.o2osys.log.server.po.Attribute;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/19 16:36
 **/
@Data
public class OperationVO {

    private String id;

    @ApiModelProperty(value = "应用名")
    private String appName;
    @ApiModelProperty(value = "所对应操作的类名")
    private String tableName;
    @ApiModelProperty(value = "类的id")
    private String objectId;
    @ApiModelProperty(value = "操作人名字")
    private String operator;
    @ApiModelProperty(value = "操作人id")
    private String operatorId;
    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "操作的别名")
    private String operationAlias;
    private String remark;
    private String comment;
    private Date createTime;

    private List<Attribute> attributeList = new ArrayList<>();

}

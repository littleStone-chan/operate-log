package com.o2osys.log.server.params;

import com.o2osys.tools.commons.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/19 15:58
 **/
@Data
public class OperateLogParam extends PageDTO {

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

}

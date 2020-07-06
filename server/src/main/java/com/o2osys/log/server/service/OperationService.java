package com.o2osys.log.server.service;

import com.github.pagehelper.PageInfo;
import com.o2osys.log.client.dto.OperationDTO;
import com.o2osys.log.server.params.OperateLogParam;
import com.o2osys.log.server.po.Operation;

public interface OperationService {

    /**
     * 创建表
     * @param tableName
     */
    void create(String tableName);

    /**
     * 添加操作日志
     * @param operationDTO
     * @return
     */

    Operation add(OperationDTO operationDTO);

    /**
     * 分页查询
     * @param operateLogParam
     * @return
     */
    PageInfo listByPage(OperateLogParam operateLogParam);
}

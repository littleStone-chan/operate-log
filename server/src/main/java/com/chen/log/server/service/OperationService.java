package com.chen.log.server.service;

import com.chen.log.server.params.OperateLogParam;
import com.chen.log.server.po.Operation;
import com.github.pagehelper.PageInfo;
import com.chen.log.client.dto.OperationDTO;

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

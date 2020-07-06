package com.chen.log.server.service;

import com.github.pagehelper.PageInfo;
import com.chen.log.client.dto.OperationLogDTO;
import com.chen.log.server.params.OperateLogParam;

public interface LogService {

    boolean add(OperationLogDTO operationLogDTO);

    PageInfo listByPage(OperateLogParam operateLogParam);
}

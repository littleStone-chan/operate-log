package com.o2osys.log.server.service;

import com.github.pagehelper.PageInfo;
import com.o2osys.log.client.dto.OperationLogDTO;
import com.o2osys.log.server.params.OperateLogParam;

public interface LogService {

    boolean add(OperationLogDTO operationLogDTO);

    PageInfo listByPage(OperateLogParam operateLogParam);
}

package com.o2osys.log.client;

import com.o2osys.log.client.dto.LogInterceptorDTO;
import com.o2osys.log.client.dto.OperationLogDTO;
import com.o2osys.log.client.intrceptor.LogInterceptor;
import com.o2osys.tools.util.ObjectUtil;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/29 16:54
 **/
public class OperateLogHelper {

    /**
     * 设置分组id
     * 表示几个操作日志在同一个分组
     * @param groupId 分组id
     */
    public static void putGroupId(String groupId) {
        //日志拦截的DTO
        LogInterceptorDTO logInterceptorDTO = ObjectUtil.isEmpty(LogInterceptor.threadLocal.get()) ? new LogInterceptorDTO() : LogInterceptor.threadLocal.get();

        //操作日志dto
        OperationLogDTO operationLogDTO = logInterceptorDTO.getOperationLogDTO();
        operationLogDTO = ObjectUtil.beNotNull(operationLogDTO,OperationLogDTO.class);
        operationLogDTO.setGroupId(groupId);
        logInterceptorDTO.setOperationLogDTO(operationLogDTO);

        LogInterceptor.threadLocal.set(logInterceptorDTO);
    }

}

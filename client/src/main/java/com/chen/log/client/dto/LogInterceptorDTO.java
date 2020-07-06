package com.chen.log.client.dto;

import lombok.Data;

/**
 * @Author chen
 * @Description 日志拦截器的dto 放在本地线程里
 * @Date 2020/6/19 09:55
 **/
@Data
public class LogInterceptorDTO {

    /**
     * 新增操作日志接口需要的dto
     */
    OperationLogDTO operationLogDTO;

    /**
     * 是否自动提交信息到日志服务
     * 如果有@Transactional注解，则需要设置成false
     */
    Boolean isAutoCommit = true;

    /**
     * 是否發送過數據
     */
    Boolean isSend = false;
}

package com.chen.log.client.service;

import com.chen.log.client.annotation.OperationLogAlias;

/**
 * 自定义接口
 * 此接口要在相关的服务实现
 */
public interface OperateLogCustomService {

    /**
     * 获取操作者名字
     * @return
     */
    String getOperator();

    /**
     * 原本传输的值 可能是 0 表示否
     * 在保存在日志 需要保存 否
     * @param value 原本传输的值
     * @param key 字典表的key
     * @return 字典表的name
     */
    Object getValueByDictionary(Object value, String key);

    /**
     * 原本传输的值 可能是 0 表示否
     * 在保存在日志 需要保存 否
     * @param id 表id
     * @param tableName 表名
     * @Param fieldName 表的字段名
     * @return 表值
     */
    Object getValueByTable(String dataBase, String tableName, String fieldName,String id);


    Object getValue(OperationLogAlias operationLogAlias, Object value);
}

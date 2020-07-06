package com.chen.log.server.log;

import com.chen.log.client.annotation.OperationLogAlias;
import com.chen.log.client.service.OperateLogCustomService;
import org.springframework.stereotype.Service;

@Service
public class OperateLogCustomServiceImpl implements OperateLogCustomService {

    public String getOperator() {
        return "测试名字";
    }

    @Override
    public Object getValueByDictionary(Object value, String key) {
        return value.toString() + key;
    }

    @Override
    public Object getValueByTable(String databaseName, String tableName,String fieldName,String id) {
        return id + tableName + fieldName;
    }

    @Override
    public Object getValue(OperationLogAlias operationLogAlias, Object value) {
        return value;
    }


}
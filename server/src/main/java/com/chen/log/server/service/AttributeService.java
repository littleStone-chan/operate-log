package com.chen.log.server.service;

import com.chen.log.server.po.Attribute;
import com.chen.log.client.dto.AttributeDTO;

import java.util.List;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/15 18:16
 **/
public interface AttributeService {

    /**
     * 创建表
     * @param tableName
     */
    void create(String tableName);


    /**
     * 批量插入
     * @param attributeModelList
     * @return
     */
    boolean addBatch(List<AttributeDTO> attributeModelList, String appName);

    /**
     * 查询信息
     * @param operationIdList
     * @param appName
     * @return
     */
    List<Attribute> queryByOperationIdList(List<String> operationIdList, String appName);
}

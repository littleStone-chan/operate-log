package com.chen.log.server.dao;

import com.chen.log.server.po.Attribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttributeDao {

    List<Attribute> queryByOperationIdList(@Param("list") List<String> operationIdList,@Param("tableName") String tableName);

    List<Attribute> queryByFilter(Attribute attribute);


    /**
     * 批量插入
     * @param attributeModelList
     * @return
     */
    Integer insertBatch(@Param("attributeModelList") List<Attribute> attributeModelList, @Param("tableName") String tableName);

    /**
     * 是否存在表
     * @param tableName
     * @return
     */
    int existTable(@Param("tableName") String tableName);

    /**
     * 创建表
     * @param tableName
     */
    void create(@Param("tableName") String tableName);

    /**
     * 添加触发器
     * @param tableName
     */
    void addTrigger(@Param("tableName") String tableName);
}

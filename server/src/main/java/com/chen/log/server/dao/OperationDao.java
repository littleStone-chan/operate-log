package com.chen.log.server.dao;

import com.chen.log.server.po.Operation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationDao {

    /**
     * 查询
     * @param operation
     * @return
     */
    List<Operation> queryByFilter(@Param("operation") Operation operation,@Param("tableName") String tableName);

    /**
     * 为空不插入
     * @param logOperation
     * @return
     */
    Integer insertSelective(Operation logOperation);

    /**
     * 是否存在表
     * @param tableName
     * @return
     */
    int existTable(@Param("tableName")String tableName);

    /**
     * 创建表
     * @param tableName
     */
    void create(@Param("tableName") String tableName);


    void update(@Param("appName") String appName,@Param("id") String id);
}

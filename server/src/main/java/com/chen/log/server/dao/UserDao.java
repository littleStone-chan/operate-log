package com.chen.log.server.dao;

import com.chen.log.server.po.User;
import com.chen.log.client.annotation.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    /**
     * 修改
     * @param user
     */
    @OperationLog(tableName = "user",remark = "修改保险基础信息")
    void update(@Param("user") User user);

    @OperationLog(tableName = "user",remark = "修改保险基础信息")
    void update2(@Param("name") String name2, @Param("id") String id);

}

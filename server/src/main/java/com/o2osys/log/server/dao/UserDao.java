package com.o2osys.log.server.dao;

import com.o2osys.log.client.annotation.OperationLog;
import com.o2osys.log.client.annotation.OperationLogAlias;
import com.o2osys.log.server.po.Attribute;
import com.o2osys.log.server.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

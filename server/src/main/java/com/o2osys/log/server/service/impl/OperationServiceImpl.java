package com.o2osys.log.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.o2osys.log.client.dto.OperationDTO;
import com.o2osys.log.server.dao.OperationDao;
import com.o2osys.log.server.params.OperateLogParam;
import com.o2osys.log.server.po.Operation;
import com.o2osys.log.server.service.OperationService;
import com.o2osys.tools.base.BaseException;
import com.o2osys.tools.util.BeanMapper;
import com.o2osys.tools.util.WebUtil;
import com.o2osys.tools.util.snowflake.SnowflakeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/15 14:43
 **/
@Service
public class OperationServiceImpl implements OperationService {

    @Autowired
    OperationDao operationDao;

    private final static String tableNamePre = "operation_";

    @Override
    public void create(String appName) {

        if (StringUtils.isEmpty(appName)){
            throw new BaseException("应用名不能为空");
        }

        String tableName = tableNamePre + appName;

        //判断表是否存在
        if (operationDao.existTable(tableName)==0){
            operationDao.create(tableName);
        }

    }

    @Override
    public Operation add(OperationDTO operationDTO) {

        //判断是否需要创表
        this.create(operationDTO.getAppName());

        Operation operation = BeanMapper.map(operationDTO,Operation.class);
        operation.setId(SnowflakeUtil.getId());
        operation.setOperatorId(WebUtil.getUid());
        if (operationDao.insertSelective(operation)>0){
            return operation;
        }

        throw new BaseException("添加操作日志失败，operationDTO："+operationDTO.toString());
    }

    @Override
    public PageInfo listByPage(OperateLogParam operateLogParam) {

        //先判断表是否存在
        String tableName = tableNamePre + operateLogParam.getAppName();

        //判断表是否存在
        if (operationDao.existTable(tableName)==0){
            return new PageInfo();
        }


        Operation operation = BeanMapper.map(operateLogParam,Operation.class);
        PageHelper.startPage(operateLogParam.getPageNo(), operateLogParam.getPageSize());
        return new PageInfo(operationDao.queryByFilter(operation,tableName));
    }

}

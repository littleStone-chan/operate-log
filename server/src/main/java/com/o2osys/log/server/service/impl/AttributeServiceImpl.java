package com.o2osys.log.server.service.impl;

import com.o2osys.log.client.dto.AttributeDTO;
import com.o2osys.log.server.dao.AttributeDao;
import com.o2osys.log.server.po.Attribute;
import com.o2osys.log.server.service.AttributeService;
import com.o2osys.tools.base.BaseException;
import com.o2osys.tools.util.BeanMapper;
import com.o2osys.tools.util.snowflake.SnowflakeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/15 18:17
 **/
@Service
public class AttributeServiceImpl implements AttributeService {

    @Autowired
    AttributeDao attributeDao;

    private final static String tableNamePre = "attribute_";

    @Override
    public void create(String appName) {
        if (StringUtils.isEmpty(appName)){
            throw new BaseException("应用名不能为空");
        }

        String tableName = tableNamePre + appName;

        //判断表是否存在
        if (attributeDao.existTable(tableName)==0){
            attributeDao.create(tableName);
            attributeDao.addTrigger(tableName);
        }
    }


    @Transactional
    @Override
    public boolean addBatch(List<AttributeDTO> attributeDTOList, String appName) {


        if (ObjectUtils.isEmpty(attributeDTOList)||StringUtils.isEmpty(appName)){
            return Boolean.FALSE;
        }

        //转化成po
        List<Attribute> attributeList = BeanMapper.mapList(attributeDTOList,Attribute.class);

        //如果没有table则创建
        this.create(appName);

        //设置id
        for (Attribute attribute : attributeList) {
            attribute.setId(SnowflakeUtil.getId());
        }

        //批量插入
        return attributeDao.insertBatch(attributeList,tableNamePre + appName) > 0;
    }

    @Override
    public List<Attribute> queryByOperationIdList(List<String> operationIdList, String appName) {

        String tableName = tableNamePre + appName;

        //判断表是否存在
        if (attributeDao.existTable(tableName)==0){
            return new ArrayList<>();
        }

        return attributeDao.queryByOperationIdList(operationIdList,tableName);
    }

}

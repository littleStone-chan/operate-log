package com.o2osys.log.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.o2osys.log.client.dto.OperationLogDTO;
import com.o2osys.log.client.dto.AttributeDTO;
import com.o2osys.log.client.dto.OperationDTO;
import com.o2osys.log.client.dto.OperationLogDTO;
import com.o2osys.log.server.dao.AttributeDao;
import com.o2osys.log.server.params.OperateLogParam;
import com.o2osys.log.server.po.Attribute;
import com.o2osys.log.server.po.Operation;
import com.o2osys.log.server.service.AttributeService;
import com.o2osys.log.server.service.LogService;
import com.o2osys.log.server.service.OperationService;
import com.o2osys.log.server.vo.OperationVO;
import com.o2osys.tools.base.BaseException;
import com.o2osys.tools.util.BeanMapper;
import com.o2osys.tools.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @Author chen
 * @Description
 * @Date 2020/6/15 18:14
 **/
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    OperationService operationService;

    @Autowired
    AttributeService attributeService;

    @Autowired
    AttributeDao attributeDao;

    @Transactional
    @Override
    public boolean add(OperationLogDTO operationLogDTO) {

        //先添加操作日志
        Operation operation = operationService.add(BeanMapper.map(operationLogDTO, OperationDTO.class));

        //有操作详情
        if (!ObjectUtils.isEmpty(operationLogDTO.getAttributeDTOList())){

            //先赋值操作
            List<AttributeDTO> attributeDTOList = operationLogDTO.getAttributeDTOList();
            for (AttributeDTO attributeDTO :attributeDTOList){
                attributeDTO.setOperationId(operation.getId());
            }

            if (!attributeService.addBatch(operationLogDTO.getAttributeDTOList(),operation.getAppName())){
                throw new BaseException("添加详情失败");
            }
        }

        return true;
    }

    @Override
    public PageInfo listByPage(OperateLogParam operateLogParam) {

        PageInfo pageInfo = operationService.listByPage(operateLogParam);
        List<Operation> operationList = pageInfo.getList();
        List<OperationVO> operationVOList = BeanMapper.mapList(operationList,OperationVO.class);

        //查询相应的属性
        if (!ObjectUtil.isEmpty(operationVOList)) {
            List<String> operationIdList = operationVOList.stream().map(OperationVO::getId).collect(Collectors.toList());
            List<Attribute> attributeList = attributeService.queryByOperationIdList(operationIdList,operateLogParam.getAppName());
            if (!CollectionUtils.isEmpty(attributeList)) {
                Map<String, List<Attribute>> attributeMap = new HashMap<>();
                for (Attribute attribute : attributeList) {
                    attributeMap.putIfAbsent(attribute.getOperationId(), new ArrayList<>());
                    attributeMap.get(attribute.getOperationId()).add(attribute);
                }

                for (OperationVO operationVO : operationVOList) {
                    if (attributeMap.containsKey(operationVO.getId())) {
                        operationVO.getAttributeList().addAll(attributeMap.get(operationVO.getId()));
                    }
                }
            }

        }
        pageInfo.setList(operationVOList);
        return pageInfo;
    }
}

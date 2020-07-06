package com.o2osys.log.server.controller;

import com.o2osys.log.client.dto.OperationLogDTO;
import com.o2osys.log.server.dao.OperationDao;
import com.o2osys.log.server.dao.UserDao;
import com.o2osys.log.server.params.OperateLogParam;
import com.o2osys.log.server.po.User;
import com.o2osys.log.server.service.LogService;
import com.o2osys.tools.base.BaseController;
import com.o2osys.tools.commons.PageDTO;
import com.o2osys.tools.commons.RespMsg;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "操作日志")
@RestController
@RequestMapping("/log")
public class LogController extends BaseController {


    @Autowired
    private LogService logService;

    @Autowired
    private UserDao userDao;

    /**
     * 日志的分页查询
     * @param operateLogParam
     * @return
     */
    @GetMapping("listByPage")
    public RespMsg listByPage(OperateLogParam  operateLogParam){
        return success(logService.listByPage(operateLogParam));
    }


    @PostMapping("test")
    public RespMsg test(@RequestBody User user){
        userDao.update(user);
        return success();
    }

}

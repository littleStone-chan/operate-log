package com.chen.log.server.controller;

import com.chen.log.server.dao.UserDao;
import com.chen.log.server.params.OperateLogParam;
import com.chen.log.server.po.User;
import com.chen.log.server.service.LogService;
import com.chen.tools.base.BaseController;
import com.chen.tools.commons.RespMsg;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
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

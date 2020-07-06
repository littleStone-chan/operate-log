package com.o2osys.log.client.intrceptor;

import com.o2osys.log.client.annotation.OperationLog;
import com.o2osys.log.client.dto.AnnotationAopOperateLogDTO;
import com.o2osys.log.client.dto.LogInterceptorDTO;
import com.o2osys.log.client.enums.OperateLogTypeEnum;
import com.o2osys.tools.base.BaseException;
import com.o2osys.tools.commons.StringUtils;
import com.o2osys.tools.rabbitmq.util.RabbitUtil;
import com.o2osys.tools.util.ObjectUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Author chen
 * @Description 拦截注解 @Transactional
 * @Date 2020/6/16 15:11
 **/
@Component
@Aspect
public class AnnotationTransactionalAspect {

    private final String POINT_CUT = "@annotation(org.springframework.transaction.annotation.Transactional)";


    @Autowired
    RabbitUtil rabbitUtil;

    /**
     * 定义一个切入点
     */
    @Pointcut(POINT_CUT)
    private void operateLogMethod() {

        System.out.println("定义一个切入点");
    }




    @Before("operateLogMethod()")
    public void before(JoinPoint joinPoint){

    }


    /**
     * 操作成功，往队列丢日志信息
     * @param joinPoint
     */
    @After(value = "operateLogMethod()")
    public void doAfterAdvice(JoinPoint joinPoint){
        LogInterceptorDTO logInterceptorDTO = LogInterceptor.threadLocal.get();

        if (logInterceptorDTO == null || logInterceptorDTO.getOperationLogDTO() == null || StringUtils.isEmpty(logInterceptorDTO.getOperationLogDTO().getAppName())){
            return;
        }
        //开启事务 在此处提交到日志服务
        if (!logInterceptorDTO.getIsAutoCommit()&&!logInterceptorDTO.getIsSend()&&(
                logInterceptorDTO.getOperationLogDTO().getOperationType().equals(OperateLogTypeEnum.INSERT.getType()) ||
                !ObjectUtil.isEmpty(logInterceptorDTO.getOperationLogDTO().getAttributeDTOList()))){
            rabbitUtil.sendMessageToExchange(new TopicExchange("o2osys.log"),"o2osys.operate_log",logInterceptorDTO.getOperationLogDTO());
            logInterceptorDTO.setIsSend(true);
            LogInterceptor.threadLocal.set(logInterceptorDTO);
        }
    }

}

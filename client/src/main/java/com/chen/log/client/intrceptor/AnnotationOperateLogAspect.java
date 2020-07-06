package com.chen.log.client.intrceptor;

import com.chen.log.client.annotation.OperationLog;
import com.chen.log.client.dto.AnnotationAopOperateLogDTO;
import com.chen.log.client.dto.LogInterceptorDTO;
import com.chen.log.client.enums.OperateLogTypeEnum;
import com.chen.tools.base.BaseException;
import com.chen.tools.commons.StringUtils;
import com.chen.tools.rabbitmq.util.RabbitUtil;
import com.chen.tools.util.ObjectUtil;
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
 * @Description 拦截@OperationLog
 * @Date 2020/6/16 15:11
 **/
@Component
@Aspect
public class AnnotationOperateLogAspect {

    /**
     * @OperationLog 注解的切面
     */
    private final String OPERATION_LOG_POINT_CUT = "@annotation(OperationLog)";

    /**
     * 存放注解信息的 本地线程
     */
    public static ThreadLocal<AnnotationAopOperateLogDTO> threadLocal = new ThreadLocal<AnnotationAopOperateLogDTO>();

    @Autowired
    RabbitUtil rabbitUtil;

    /**
     * 定义一个切入点
     */
    @Pointcut(OPERATION_LOG_POINT_CUT)
    private void operateLogMethod() {

        System.out.println("定义一个切入点");
    }


    /**
     * @OperationLog 注解的前置拦截
     * @param joinPoint
     */
    @Before("operateLogMethod()")
    public void operateLogBefore(JoinPoint joinPoint){

        //获取注解的基本信息
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        OperationLog myAnnotation = method.getAnnotation(OperationLog.class);

        String tableName =  !"".equals(myAnnotation.tableName()) ? myAnnotation.tableName() : myAnnotation.value();
        if (StringUtils.isEmpty(tableName)){
            throw new BaseException("日志的表名不能为空。");
        }

//        Map<String,OperationLogAliasDTO> operationLogAliasMap = new HashMap<>();
//        //获取方法上的注解
//        Annotation[][] annotationsList = method.getParameterAnnotations();
//        for (Annotation[] annotations : annotationsList){
//            for (Annotation annotation : annotations){
//                //参数上有注解
//                if (annotation instanceof OperationLogAlias){
//                    OperationLogAliasDTO operationLogAliasDTO = new OperationLogAliasDTO();
//                    operationLogAliasDTO.setKey(((OperationLogAlias) annotation).key());
//                    operationLogAliasDTO.setAttributeAlias(((OperationLogAlias) annotation).attributeAlias());
//                    operationLogAliasMap.put(((OperationLogAlias) annotation).fieldName(),operationLogAliasDTO);
//                }
//            }
//        }

        //将操作日志基本信息放在本地线程
        AnnotationAopOperateLogDTO operateLog = new AnnotationAopOperateLogDTO();
        operateLog.setOperateLog(Boolean.TRUE);
        operateLog.setTableClassName(method.getParameterTypes()[0].getName());
        operateLog.setOperateType(myAnnotation.type());
        operateLog.setRemark(myAnnotation.remark());
        operateLog.setTableName(tableName);
        operateLog.setPrimaryTable(myAnnotation.primaryTable());
        this.threadLocal.set(operateLog);

    }


    /**
     * @OperationLog 注解的后置拦截
     * 操作成功，往队列丢日志信息
     * @param joinPoint
     */
    @After(value = "operateLogMethod()")
    public void operateLogDoAfterAdvice(JoinPoint joinPoint){
        LogInterceptorDTO logInterceptorDTO = LogInterceptor.threadLocal.get();

        if (logInterceptorDTO == null || logInterceptorDTO.getOperationLogDTO() == null || StringUtils.isEmpty(logInterceptorDTO.getOperationLogDTO().getAppName())){
            return;
        }
        try {
            //开启事务 不自动提交到日志服务
            if (logInterceptorDTO.getIsAutoCommit()&&!logInterceptorDTO.getIsSend()&&(
                    logInterceptorDTO.getOperationLogDTO().getOperationType().equals(OperateLogTypeEnum.INSERT.getType()) ||
                            !ObjectUtil.isEmpty(logInterceptorDTO.getOperationLogDTO().getAttributeDTOList()))){
                rabbitUtil.sendMessageToExchange(new TopicExchange("o2osys.log"),"o2osys.operate_log",logInterceptorDTO.getOperationLogDTO());
                logInterceptorDTO.setIsSend(true);
                LogInterceptor.threadLocal.set(logInterceptorDTO);
            }
        }catch (NullPointerException e){
            System.out.println("请在@OperationLog注解将primaryTable设置成true");
            throw new BaseException("请在@OperationLog注解将primaryTable设置成true");
        }

    }




}

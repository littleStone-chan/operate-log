package com.chen.log.client.intrceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chen.log.client.annotation.OperationLogAlias;
import com.chen.log.client.annotation.OperationLogIgnore;
import com.chen.log.client.dto.AnnotationAopOperateLogDTO;
import com.chen.log.client.dto.AttributeDTO;
import com.chen.log.client.dto.LogInterceptorDTO;
import com.chen.log.client.dto.OperationLogDTO;
import com.chen.log.client.enums.AliasTypeEnum;
import com.chen.log.client.enums.OperateLogTypeEnum;
import com.chen.log.client.service.OperateLogCustomService;
import com.chen.log.client.util.ReflectUtil;
import com.google.common.base.CaseFormat;
import com.chen.tools.base.BaseException;
import com.chen.tools.commons.StringUtils;
import com.chen.tools.util.ObjectUtil;
import lombok.SneakyThrows;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/16 15:17
 **/
@Intercepts({@Signature(
        type= Executor.class,
        method = "update",
        args = {MappedStatement.class,Object.class})})
public class LogInterceptor implements Interceptor {

    //本地线程 存储OperationLogDTO对象
    public static ThreadLocal<LogInterceptorDTO> threadLocal = new ThreadLocal<>();

    //连接的数据库名
    private String tableSchema = "";

    private String appName = "";

    @Autowired
    OperateLogCustomService operateLogCustomService;

    /**
     * 代理对象每次调用的方法，就是要进行拦截的时候要执行的方法。在这个方法里面做我们自定义的逻辑处理
     */
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.proceed();
    }

    /**
     * plugin方法是拦截器用于封装目标对象的，通过该方法我们可以返回目标对象本身，也可以返回一个它的代理
     *
     * 当返回的是代理的时候我们可以对其中的方法进行拦截来调用intercept方法 -- Plugin.wrap(target, this)
     * 当返回的是当前对象的时候 就不会调用intercept方法，相当于当前拦截器无效
     */
    @SneakyThrows
    public Object plugin(Object target) {
        //先保存日志的基本信息  等成功后再保存日志
        if (target instanceof DefaultResultSetHandler && AnnotationOperateLogAspect.threadLocal.get() != null && AnnotationOperateLogAspect.threadLocal.get().getOperateLog()){

            AnnotationAopOperateLogDTO annotationAopOperateLogDTO = AnnotationOperateLogAspect.threadLocal.get();
            annotationAopOperateLogDTO.setOperateLog(Boolean.FALSE);
            AnnotationOperateLogAspect.threadLocal.set(annotationAopOperateLogDTO);

            //获取boundSql
            BoundSql boundSql = (BoundSql) ReflectUtil.getValueByFieldName(target, "boundSql");

            //sql请求的参数
            Object parameterObject = ReflectUtil.getValueByFieldName(boundSql, "parameterObject");

            //id参数
            String id = "";

            //获取sql 连接
            Executor executor = (Executor) ReflectUtil.getValueByFieldName(target, "executor");
            Connection connection = executor.getTransaction().getConnection();

            Boolean isAutoCommit = (Boolean)ReflectUtil.getValueByFieldName(connection,"isAutoCommit");
            isAutoCommit = isAutoCommit == null ? Boolean.TRUE : isAutoCommit;
            //需要的数据
            LogInterceptorDTO logInterceptorDTO = threadLocal.get();
            logInterceptorDTO = ObjectUtil.beNotNull(logInterceptorDTO,LogInterceptorDTO.class);

            //设置是否自动提交
            logInterceptorDTO.setIsAutoCommit(isAutoCommit);

            //获取表各个字段的注释
            String tableName = annotationAopOperateLogDTO.getTableName();
            Map<String, String> sqlFiledComment = this.getSqlFiledComment(connection, tableName);

            //修改的表的字段
            List<String> sqlFiledNameList = new ArrayList<>();

            /*修改的参数 如果添加注解@Param("et")  那么接收到的参数变成 et.id*/
            /*需要将收到的参数拆开 et 和 id */
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();

            //参数转化成map形式
            Map<String,Object> parameterMap = JSONObject.parseObject(JSON.toJSONString(parameterObject));

            //遇到有前缀的参数时 用的map
            Map<String,Object> finallyParameterMap = parameterMap;

            //对参数进行处理  判断是否有前缀
            String preParam = "";
            if (parameterMappingList.size()>0&&parameterMappingList.get(0).getProperty().contains(".")){
                preParam = StringUtils.split(parameterMappingList.get(0).getProperty(),".")[0];
                finallyParameterMap = JSONObject.parseObject(JSON.toJSONString(parameterMap.get(preParam)));
            }

            //带参数进行添加
            for (ParameterMapping parameterMapping :parameterMappingList){

                //参数的值
                Object paramValue ;
                //参数名称
                String paramName ;

                //如果有前缀
                if (StringUtils.isNotEmpty(preParam)){
                    paramName = StringUtils.split(parameterMapping.getProperty(),".")[1];
                }else {
                    paramName = parameterMapping.getProperty();
                }

                paramValue = finallyParameterMap.get(paramName);

                //如果是id 直接跳过
                if (StringUtils.equals("id",paramName)){
                    id = paramValue.toString();
                    continue;
                }
                sqlFiledNameList.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,paramName));
            }

            //需要提交的数据
            OperationLogDTO operationLogDTO = logInterceptorDTO.getOperationLogDTO();
            operationLogDTO = ObjectUtil.beNotNull(operationLogDTO,OperationLogDTO.class);
            List<AttributeDTO> attributeDTOList = operationLogDTO.getAttributeDTOList();

            /* 操作类型是更新 才有属性值*/
            if (annotationAopOperateLogDTO.getOperateType().equals(OperateLogTypeEnum.UPDATE)){
                //拿到之前的数据
                Map<String, Object> oldData = this.getOldData(connection, id, tableName, finallyParameterMap);

                //获取修改参数的对象
                Class clz = Class.forName(annotationAopOperateLogDTO.getTableClassName());

                //设置属性
                for (String sqlFiledName : sqlFiledNameList){
                    Object oldValue = oldData.get(sqlFiledName);
                    Object newValue = finallyParameterMap.get(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,sqlFiledName));

                    //新值和旧值都为空
                    if (oldValue == null && newValue == null)
                        continue;
                    //时间类判断
                    if (oldValue != null && oldValue instanceof LocalDate){
                        //如果获取到的值是时间戳
                        if (newValue instanceof Long){
                            newValue = Instant.ofEpochMilli((Long) newValue).atZone(ZoneOffset.ofHours(8)).toLocalDate();
                        }
                        if (newValue instanceof String){
                            newValue = LocalDate.parse(newValue.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        }
                    }
                    //新值和旧值一样则跳过
                    if (oldValue != null && oldValue.equals(newValue))
                        continue;
                    if (newValue instanceof BigDecimal && oldValue instanceof BigDecimal &&  ((BigDecimal)oldValue).compareTo((BigDecimal) newValue)==0){
                        continue;
                    }
                    //sql的字段名
                    String comment = sqlFiledComment.get(sqlFiledName);

                    //Class.forName("com.o2osys.log.server.po.User").getDeclaredFields()[1].getAnnotation(OperationLogAlias.class)
                    /*属性所对应的值是否需要从字典表上获取*/

                    //有前缀则表示 注解是加在字段上 比如 user.name
                    if (StringUtils.isNotEmpty(preParam)){
                        Field field = clz.getDeclaredField(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,sqlFiledName));

                        //忽略注解
                        OperationLogIgnore operationLogIgnore = field.getAnnotation(OperationLogIgnore.class);
                        if (operationLogIgnore != null){
                            continue;
                        }

                        //别名注解
                        OperationLogAlias operationLogAlias = field.getAnnotation(OperationLogAlias.class);
                        if (operationLogAlias != null){
                            if (AliasTypeEnum.DICTIONARY.equals(operationLogAlias.type()) && (StringUtils.isBlank(operationLogAlias.key()))){
                                throw new BaseException("字段（" + comment + "）当选择在字典里取值时，字典key不能为空");
                            }
                            if (AliasTypeEnum.TABLE.equals(operationLogAlias.type()) && (StringUtils.isBlank(operationLogAlias.tableName()) || StringUtils.isBlank(operationLogAlias.fieldName()) || StringUtils.isBlank(operationLogAlias.dataBaseName()))){
                                throw new BaseException("字段（" + comment +"）当选择在表里取值时，表名和字段和库名不能为空");
                            }
                            oldValue=operateLogCustomService.getValue(operationLogAlias,oldValue);
                            newValue=operateLogCustomService.getValue(operationLogAlias,newValue);
                            comment = StringUtils.isEmpty(operationLogAlias.attributeAlias()) ? comment : operationLogAlias.attributeAlias();
                        }
                    }else {

                    }


                    //属性
                    AttributeDTO attributeDTO = new AttributeDTO();
                    attributeDTO.setTableClassName(annotationAopOperateLogDTO.getTableClassName());
                    attributeDTO.setAttributeType(oldValue != null ? oldValue.getClass().getSimpleName():"");
                    attributeDTO.setAttributeName(sqlFiledName);
                    attributeDTO.setAttributeAlias(comment);
                    attributeDTO.setNewValue(newValue);
                    attributeDTO.setOldValue(oldValue);
                    attributeDTOList.add(attributeDTO);



                }
                //设置属性
                operationLogDTO.setAttributeDTOList(attributeDTOList);
            }

            //记录主表的信息    无事务或者主表 null
            if (annotationAopOperateLogDTO.getPrimaryTable()||logInterceptorDTO.getIsAutoCommit()||StringUtils.isEmpty(operationLogDTO.getAppName())){
                operationLogDTO.setAppName(this.appName);
                operationLogDTO.setObjectId(id);
                operationLogDTO.setTableName(tableName);
                operationLogDTO.setOperationType(annotationAopOperateLogDTO.getOperateType().getType());
                operationLogDTO.setOperationAlias(annotationAopOperateLogDTO.getOperateType().getAlias());
                operationLogDTO.setComment("");
                operationLogDTO.setOperator(operateLogCustomService.getOperator());
                operationLogDTO.setRemark(annotationAopOperateLogDTO.getRemark());
            }


            logInterceptorDTO.setOperationLogDTO(operationLogDTO);
            threadLocal.set(logInterceptorDTO);
        }

        return Plugin.wrap(target, this);
    }

    /**
     * 用于在Mybatis配置文件中指定一些属性的，注册当前拦截器的时候可以设置一些属性
     */
    public void setProperties(Properties properties) {
        this.tableSchema = properties.getProperty("tableSchema");
        this.appName = properties.getProperty("appName");
    }

    /**
     * 获取旧的数据信息
     * 为了判断新值和旧值是否一致
     * 将新值的数据代入，得到新值的数据类型 要不然rs.getObject 返回的都是string
     * @return
     */
    public Map<String,Object> getOldData(Connection connection,String id,String tableName,Map<String, Object> newDataMap){

        //返回回去的值
        Map<String,Object> resMap = new HashMap<>();

        //查询字段注解的sql
        String commentSql = "select * from "+ tableName+ " where id = '"+id+"' ";
        ResultSet rs = null;
        PreparedStatement  statement = null;
        try {
            //2.创建statement类对象，用来执行SQL语句！！
            statement = connection.prepareStatement(commentSql);
            //3. 设置参数
//            statement.setString(1,tableName);
//            statement.setString(2,id);
            //4.ResultSet类，用来存放获取的结果集！
            rs = statement.executeQuery(commentSql);
            //5. 查询
            while (rs.next()) {

                ResultSetMetaData resultSetMetaData = rs.getMetaData();
                //循环数据
                for (int i=1;i<=resultSetMetaData.getColumnCount();i++){
                    //sql的字段名
                    String columnName = resultSetMetaData.getColumnName(i);
                    //新值 来判断数据库的数据类型
                    Object newValue = newDataMap.get(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,columnName));
                    if (newValue == null){
                        continue;
                    }
                    //时间格式特殊处理
                    if (StringUtils.isHasEquals(resultSetMetaData.getColumnClassName(i),"java.sql.Date","java.sql.Timestamp")){
                        if (newValue instanceof Long){
                            resMap.put(columnName,Instant.ofEpochMilli(((Long) newValue).longValue()).atZone(ZoneOffset.ofHours(8)).toLocalDate());
                        }else{
                            resMap.put(columnName,Instant.ofEpochMilli(((Timestamp)JdbcUtils.getResultSetValue(rs, i, Timestamp.class)).getTime()).atZone(ZoneOffset.ofHours(8)).toLocalDate());
                        }
                        continue;
                    }
                    resMap.put(columnName,JdbcUtils.getResultSetValue(rs,i,newValue.getClass()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, statement);
        }
        return resMap;
    }

    private void close(ResultSet rs, PreparedStatement statement) {
        try {
            if (rs != null)
                rs.close();
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sql字段的注解
     * @return
     */
    public Map<String,String> getSqlFiledComment(Connection connection,String tableName){

        //返回回去的值
        Map<String,String> resMap = new HashMap<>();

        //查询字段注解的sql
        String commentSql = "select column_name,column_comment from INFORMATION_SCHEMA.Columns where table_name= '"+tableName+"' and table_schema= '"+tableSchema + "'";
        ResultSet rs = null;
        PreparedStatement  statement = null;
        try {
            //2.创建statement类对象，用来执行SQL语句！！
            statement = connection.prepareStatement(commentSql);
            //3. 设置参数
//            statement.setString(1,tableName);
//            statement.setString(2,this.tableSchema);
            //4.ResultSet类，用来存放获取的结果集！
            rs = statement.executeQuery(commentSql);
            //5. 查询
            while (rs.next()) {
                resMap.put(rs.getString("column_name"),rs.getString("column_comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, statement);
        }
        return resMap;
    }



}

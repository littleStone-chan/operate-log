package com.o2osys.log.server.po;

import com.o2osys.log.client.annotation.OperationLogAlias;
import com.o2osys.log.client.annotation.OperationLogIgnore;
import lombok.Data;

/**
 * @Author chen
 * @Description
 * @Date 2020/6/22 09:00
 **/
@Data
public class User {

    String id;

    @OperationLogIgnore
    @OperationLogAlias(key = "hello_test",attributeAlias = "名字")
    String name;

}

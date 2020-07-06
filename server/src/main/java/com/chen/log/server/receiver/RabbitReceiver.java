package com.chen.log.server.receiver;


import com.alibaba.fastjson.JSON;
import com.chen.log.client.dto.OperationLogDTO;
import com.chen.log.server.service.LogService;
import com.chen.tools.base.BaseException;
import com.chen.tools.commons.StringUtils;
import com.chen.tools.rabbitmq.util.RabbitUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * rabbit 监听接收
 */
@Component
public class RabbitReceiver {


    @Autowired
    RabbitUtil rabbitUtil;

    @Autowired
    LogService logService;

    /**
     * 监听 提交一诺材料信息订单
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "operate_log",
                    durable = "true"),
            exchange = @Exchange(value = "o2osys.log",
                    type = "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "o2osys.operate_log"
    )
    )
    @RabbitHandler
    public void pretrialOnMessage(Message message, Channel channel) throws Exception {
        try {
            rabbitUtil.vaildUidToken(message);
            OperationLogDTO operationLogDTO  = JSON.parseObject(new String((byte[]) message.getPayload()), OperationLogDTO.class);
            logService.add(operationLogDTO);
            System.out.println("收到数据啦：");
            System.out.println(JSON.toJSONString(operationLogDTO));
        }catch (BaseException baseExtion){
            baseExtion.printStackTrace();
        }finally {
            //手工ACK
            Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
            channel.basicAck(deliveryTag, false);
        }
    }


    public static void main(String[] args) {

        System.out.println( StringUtils.split("aa.cc",".").length);
    }

}

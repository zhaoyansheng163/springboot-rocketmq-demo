package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @date 2022/4/19 17:30
 * @description RocketMQListener: 接收没回复
 */
@Component
@Slf4j
@RocketMQMessageListener(
//        nameServer = "192.168.13.101:9876", // 指定其他nameserver
        topic = "spring-string",
        selectorType = SelectorType.TAG, // 默认就是按TAG 过滤
        selectorExpression = "tag0||tag1",  // 默认是 *， 接收所有的TAG
        consumeMode = ConsumeMode.CONCURRENTLY, // 默认就是该值。ConsumeMode.ORDERLY和MessageModel.BROADCASTING不能一起设置
        messageModel = MessageModel.BROADCASTING, // 默认是集群模式
        consumerGroup = "my-group1")
public class StringConsumer2 implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("message: {}", message);
    }
}

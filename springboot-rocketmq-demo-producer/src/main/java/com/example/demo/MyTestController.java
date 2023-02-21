package com.example.demo;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyTestController {
    @GetMapping("/test2")
    public String test2() {
        sendString();
        return "test2";
    }
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    private void sendString() {
        String springTopic = "spring-string";
//        String delayTopic = MQConstants.DELAY_TOPIC;
//        String userTopic = MQConstants.USER_TOPIC;

        /***********第一种发送方式*********/
        // 同步发送消息。默认重复两次。不指定超时时间会拿producer 全局的默认超时时间(默认3s)
        SendResult sendResult = rocketMQTemplate.syncSend(springTopic, "Hello, World!");
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", springTopic, sendResult);
        // 指定超时时间是10 s
        sendResult = rocketMQTemplate.syncSend(springTopic, "Hello, World2!", 10 * 1000);
        System.out.printf("syncSend2 to topic %s sendResult=%s %n", springTopic, sendResult);
        // 发送消息并且指定tag
        sendResult = rocketMQTemplate.syncSend(springTopic + ":tag0", "Hello, World! tag0!");
        System.out.printf("syncSend1 to topic %s sendResult=%s %n", springTopic, sendResult);
        // 单方向发送消息
        rocketMQTemplate.sendOneWay(springTopic, "Hello, World! sendOneWay!");
    }
}

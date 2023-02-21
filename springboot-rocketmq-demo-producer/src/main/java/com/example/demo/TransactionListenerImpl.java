package com.example.demo;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @date 2022/4/19 21:04
 * @description 全局的事务监听器，一个MQ是一个。 rocketmqTempplate 所有的
 */
// 可以用rocketMQTemplateBeanName 指定spring 容器中其他template 使用的事务监听器
@RocketMQTransactionListener
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    private AtomicInteger transactionIndex = new AtomicInteger(0);

    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<String, Integer>();

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        System.out.printf("#### executeLocalTransaction is executed, msgTransactionId=%s %n",
                transId);
        int value = transactionIndex.getAndIncrement();
        int status = value % 3;
        localTrans.put(transId, status);
        if (status == 0) {
            // Return local transaction with success(commit), in this case,
            // this message will not be checked in checkLocalTransaction()
            System.out.printf("    # COMMIT # Simulating msg %s related local transaction exec succeeded! ### %n", msg.getPayload());
            return RocketMQLocalTransactionState.COMMIT;
        }

        if (status == 1) {
            // Return local transaction with failure(rollback) , in this case,
            // this message will not be checked in checkLocalTransaction()
            System.out.printf("    # ROLLBACK # Simulating %s related local transaction exec failed! %n", msg.getPayload());
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        System.out.printf("    # UNKNOW # Simulating %s related local transaction exec UNKNOWN! \n");
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        RocketMQLocalTransactionState retState = RocketMQLocalTransactionState.COMMIT;
        Integer status = localTrans.get(transId);
        if (null != status) {
            switch (status) {
                case 0:
                    retState = RocketMQLocalTransactionState.COMMIT;
                    break;
                case 1:
                    retState = RocketMQLocalTransactionState.ROLLBACK;
                    break;
                case 2:
                    retState = RocketMQLocalTransactionState.UNKNOWN;
                    break;
            }
        }
        System.out.printf("------ !!! checkLocalTransaction is executed once," +
                        " msgTransactionId=%s, TransactionState=%s status=%s %n",
                transId, retState, status);
        return retState;
    }
}
package io.ont.elog.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import io.ont.elog.common.ElogSDKException;
import io.ont.elog.common.EventLog;
import io.ont.elog.interfaces.IMessageProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: Elog Consumer
 * @Date: 2022/11/1 下午4:04
 */
public class MessageQueue {

    private Connection connection;
    private Channel channel;
    private Set<String> topics;


    public MessageQueue(String uri) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        connection = factory.newConnection();
        channel = connection.createChannel();
        topics = new HashSet<>();
    }

    public void registerTopic(String topic, IMessageProcessor processor) throws ElogSDKException, IOException {
        if (topics.contains(topic)) {
            throw new ElogSDKException("topic has been registered");
        }
        channel.queueDeclare(topic, true, false, false, null);
        channel.basicConsume(topic, false, "", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                try {
                    String message = new String(body, StandardCharsets.UTF_8);
                    EventLog log = JSON.parseObject(message, EventLog.class);
                    processor.handle(log);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        topics.add(topic);
    }

    public void unRegisterTopic(String topic) {
        if (topics.contains(topic)) {
            topics.remove(topic);
        }
    }
}
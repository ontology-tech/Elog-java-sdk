package io.ont.elog.mq;

import com.rabbitmq.client.*;
import io.ont.elog.common.ElogSDKException;
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
                String message = new String(body, StandardCharsets.UTF_8);
                processor.handle(message);
                try {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (IOException e) {
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
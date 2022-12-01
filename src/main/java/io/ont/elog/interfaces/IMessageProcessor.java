package io.ont.elog.interfaces;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: IMessageProcessor
 * @Date: 2022/11/3 下午5:35
 */
public interface IMessageProcessor {

    void handle(String message);
}
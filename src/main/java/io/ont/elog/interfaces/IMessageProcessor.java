package io.ont.elog.interfaces;

import io.ont.elog.common.EventLog;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: IMessageProcessor
 * @Date: 2022/11/3 下午5:35
 */
public interface IMessageProcessor {

    void handle(EventLog message);
}
package io.ont.elogtest;

import com.alibaba.fastjson.JSONObject;
import io.ont.elog.interfaces.IMessageProcessor;
import io.ont.elog.interfaces.Topic;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: TestProcessor
 * @Date: 2022/11/14 下午3:18
 */
@Topic(chain = "bsc", address = "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E")
public class TestProcessor implements IMessageProcessor {
    @Override
    public void handle(String message) {
        System.out.println(message);
    }
}
package io.ont.elogtest;

import io.ont.elog.client.ElogClient;
import io.ont.elog.common.ElogSDKException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: ElogSDKTest
 * @Date: 2022/11/7 下午2:51
 */
public class ElogSDKTest {

    private static final Log logger = LogFactory.getLog(ElogSDKTest.class);

    @Test
    public void demo() throws ElogSDKException, IOException {
        ElogClient client = new ElogClient("http://43.134.29.209:31033", "did:etho:BdDAd30010924B39c9528e1A6e11b25477C6BeBF", "amqp://diu:WBv0nkRrs5cIrKUv@43.134.29.209:30831");

//        client.uploadContract("bsc", "2.abi", "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E", ContractType.OTHER);
//
//        client.subscribeEvents("bsc", "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E", Arrays.asList("TransferSingle"));
//
//        client.unSubscribeEvents("eth", "0xdac17f958d2ee523a2206206994597c13d831ec7", Arrays.asList("Approve"));

        while (true){

        }

    }

    @Test
    public void register() throws ElogSDKException {
        ElogClient client = new ElogClient("http://43.134.29.209:31033", "", "amqp://diu:WBv0nkRrs5cIrKUv@43.134.29.209:30831");
    }
}
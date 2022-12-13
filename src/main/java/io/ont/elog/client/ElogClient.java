package io.ont.elog.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.ont.elog.common.ContractType;
import io.ont.elog.common.ElogSDKException;
import io.ont.elog.common.Utils;
import io.ont.elog.interfaces.IMessageProcessor;
import io.ont.elog.mq.MessageQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: io.ont.elog.client.ElogClient
 * @Date: 2022/11/1 下午3:54
 */
public class ElogClient {

    private String addr;
    private String did;
    private MessageQueue messageQueue;
    private IMessageProcessor processor;

    private static final Log logger = LogFactory.getLog(ElogClient.class);

    public ElogClient(String addr, String wallet, String mqUri, IMessageProcessor processor) {
        this.addr = addr;
        this.did = wallet;
        this.processor = processor;
        try {
            register();
            this.messageQueue = new MessageQueue(mqUri);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws ElogSDKException, IOException {
        List<NameValuePair> params = new LinkedList<>();;
        params.add(new BasicNameValuePair("did", did));
        String response = Utils.httpPostByUrlEncoder(addr + "/querycontracts", params, null);
        if (!response.isEmpty()){
            JSONArray list = JSON.parseArray(response);
            if (!list.isEmpty()){
                for (Object c : list){
                    JSONObject contract = JSONObject.parseObject(c.toString());
                    registerTopic(contract.getString("chain"), contract.getString("address"));
                }
            }
        }
    }

    private void register() throws ElogSDKException {
        List<NameValuePair> params = new LinkedList<>();;
        params.add(new BasicNameValuePair("wallet", did));
        String response = Utils.httpPostByUrlEncoder(addr + "/register", params, null);
        this.did = response;
    }

    public void uploadContract(String chain, String path, String address, ContractType contractType) throws ElogSDKException, IOException {
        byte[] content = new byte[0];
        if (contractType == ContractType.OTHER && !chain.equalsIgnoreCase("nuls")){
            System.out.println(Paths.get(path).toAbsolutePath());
            content = Files.readAllBytes(Paths.get(path));
        }
        List<NameValuePair> params = new LinkedList<>();;
        params.add(new BasicNameValuePair("chain", chain.toLowerCase()));
        params.add(new BasicNameValuePair("did", did));
        params.add(new BasicNameValuePair("abi", new String(content)));
        params.add(new BasicNameValuePair("type", contractType.toString()));
        params.add(new BasicNameValuePair("address", address));
        String response = Utils.httpPostByUrlEncoder(addr + "/upload", params, null);
        registerTopic(chain, address);
    }

    public void chaseBlock(String chain, String path, String address, ContractType contractType, Integer startBlock, List<String> eventsName) throws ElogSDKException, IOException {
        byte[] content = new byte[0];
        if (contractType == ContractType.OTHER){
            content = Files.readAllBytes(Paths.get(path));
        }
        List<NameValuePair> params = new LinkedList<>();;
        params.add(new BasicNameValuePair("chain", chain.toLowerCase()));
        params.add(new BasicNameValuePair("did", did));
        params.add(new BasicNameValuePair("abi", new String(content)));
        params.add(new BasicNameValuePair("type", contractType.toString()));
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("startBlock", startBlock.toString()));
        for (String event : eventsName){
            params.add(new BasicNameValuePair("names", event));
        }
        String response = Utils.httpPostByUrlEncoder(addr + "/chase", params, null);
        registerTopic(chain, address);
    }

    public void subscribeEvents(String chain, String address, List<String> eventsName) throws ElogSDKException {
        List<NameValuePair> params = new LinkedList<>();;
        params.add(new BasicNameValuePair("chain", chain.toLowerCase()));
        params.add(new BasicNameValuePair("did", did));
        params.add(new BasicNameValuePair("address", address));
        for (String event : eventsName){
            params.add(new BasicNameValuePair("names", event));
        }
        String response = Utils.httpPostByUrlEncoder(addr + "/subscribe", params, null);
    }

    public void removeContract(String chain, String address) throws ElogSDKException {
        List<NameValuePair> params = new LinkedList<>();;
        params.add(new BasicNameValuePair("chain", chain.toLowerCase()));
        params.add(new BasicNameValuePair("did", did));
        params.add(new BasicNameValuePair("address", address));
        String response = Utils.httpPostByUrlEncoder(addr + "/remove", params, null);
    }

    public void unSubscribeEvents(String chain, String address, List<String> eventsName) throws ElogSDKException {
        List<NameValuePair> params = new LinkedList<>();;
        params.add(new BasicNameValuePair("chain", chain.toLowerCase()));
        params.add(new BasicNameValuePair("did", did));
        params.add(new BasicNameValuePair("address", address));
        for (String event : eventsName){
            params.add(new BasicNameValuePair("names", event));
        }
        String response = Utils.httpPostByUrlEncoder(addr + "/unsubsribe", params, null);
    }

    public long getTimestamp(String chain, Long height) throws ElogSDKException {
        List<NameValuePair> params = new LinkedList<>();;
        params.add(new BasicNameValuePair("chain", chain.toLowerCase()));
        params.add(new BasicNameValuePair("height", height.toString()));
        String response = Utils.httpPostByUrlEncoder(addr + "/getTime", params, null);
        return Long.parseLong(response);
    }


    private void registerTopic(String chain, String address) throws IOException, ElogSDKException {
        String topic = this.did + chain.toLowerCase() + address;
        if (Objects.nonNull(processor)){
            messageQueue.registerTopic(topic, processor);
            logger.info("elog register topic " + topic);
        }
    }
}
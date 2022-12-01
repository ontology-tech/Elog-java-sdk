package io.ont.elog.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.ont.elog.common.ContractType;
import io.ont.elog.common.ElogSDKException;
import io.ont.elog.common.Utils;
import io.ont.elog.interfaces.IMessageProcessor;
import io.ont.elog.interfaces.Topic;
import io.ont.elog.mq.MessageQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final Log logger = LogFactory.getLog(ElogClient.class);

    private static HashMap<String, IMessageProcessor> processorHashMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections();
        Set<Class<? extends IMessageProcessor>> processors = reflections.getSubTypesOf(IMessageProcessor.class);
        for (Class<? extends IMessageProcessor> item : processors){
            Topic annotation = item.getAnnotation(Topic.class);
            try {
                processorHashMap.put(annotation.chain().toLowerCase() + annotation.address(), item.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public ElogClient(String addr, String wallet, String mqUri) {
        this.addr = addr;
        this.did = wallet;
        try {
            register();
            this.messageQueue = new MessageQueue(mqUri);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws ElogSDKException, IOException {
        Map<String, String> params = new HashMap<>();
        params.put("did", did);
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
        Map<String, String> params = new HashMap<>();
        params.put("wallet", this.did);
        String response = Utils.httpPostByUrlEncoder(addr + "/register", params, null);
        this.did = response;
    }

    public void uploadContract(String chain, String path, String address, ContractType contractType) throws ElogSDKException, IOException {
        byte[] content = new byte[0];
        if (contractType == ContractType.OTHER){
            System.out.println(Paths.get(path).toAbsolutePath());
            content = Files.readAllBytes(Paths.get(path));
        }
        Map<String, String> params = new HashMap<>();
        params.put("chain", chain.toLowerCase());
        params.put("did", did);
        params.put("abi", new String(content));
        params.put("type", contractType.toString());
        params.put("address", address);
        String response = Utils.httpPostByUrlEncoder(addr + "/upload", params, null);
        registerTopic(chain, address);
    }

    public void chaseBlock(String chain, String path, String address, ContractType contractType, Integer startBlock, List<String> eventsName) throws ElogSDKException, IOException {
        byte[] content = new byte[0];
        if (contractType == ContractType.OTHER){
            content = Files.readAllBytes(Paths.get(path));
        }
        Map<String, String> params = new HashMap<>();
        params.put("chain", chain.toLowerCase());
        params.put("did", did);
        params.put("abi", new String(content));
        params.put("type", contractType.toString());
        params.put("address", address);
        params.put("startBlock", startBlock.toString());
        for (String event : eventsName){
            params.put("names", event);
        }
        String response = Utils.httpPostByUrlEncoder(addr + "/chase", params, null);
        registerTopic(chain, address);
    }

    public void subscribeEvents(String chain, String address, List<String> eventsName) throws ElogSDKException {
        Map<String, String> params = new HashMap<>();
        params.put("did", did);
        params.put("chain", chain);
        params.put("address", address);
        for (String event : eventsName){
            params.put("names", event);
        }
        String response = Utils.httpPostByUrlEncoder(addr + "/subscribe", params, null);
    }

    public void removeContract(String chain, String address) throws ElogSDKException {
        Map<String, String> params = new HashMap<>();
        params.put("did", did);
        params.put("chain", chain);
        params.put("address", address);
        String response = Utils.httpPostByUrlEncoder(addr + "/remove", params, null);
    }

    public void unSubscribeEvents(String chain, String address, List<String> eventsName) throws ElogSDKException {
        Map<String, String> params = new HashMap<>();
        params.put("did", did);
        params.put("chain", chain);
        params.put("address", address);
        for (String event : eventsName){
            params.put("names", event);
        }
        String response = Utils.httpPostByUrlEncoder(addr + "/unsubsribe", params, null);
    }

    public long getTimestamp(String chain, Long height) throws ElogSDKException {
        Map<String, String> params = new HashMap<>();
        params.put("chain", chain);
        params.put("height", height.toString());
        String response = Utils.httpPostByUrlEncoder(addr + "/getTime", params, null);
        return Long.parseLong(response);
    }


    private void registerTopic(String chain, String address) throws IOException, ElogSDKException {
        String suffix = chain.toLowerCase() + address;
        String topic = this.did + suffix;
        if (processorHashMap.containsKey(suffix)){
            messageQueue.registerTopic(topic, processorHashMap.get(suffix));
            logger.info("elog register topic " + suffix);
        }
    }
}
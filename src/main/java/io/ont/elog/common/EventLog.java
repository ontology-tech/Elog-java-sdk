package io.ont.elog.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: EventLog
 * @Date: 2022/12/1 下午3:01
 */
public class EventLog {
    private String chain;
    private String address;
    private String txHash;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String[] topics;
    private byte[] data;
    private Long height;
    @JSONField(name = "block_time")
    private Long blockTime;

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Long blockTime) {
        this.blockTime = blockTime;
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "chain='" + chain + '\'' +
                ", address='" + address + '\'' +
                ", txHash='" + txHash + '\'' +
                ", topics=" + Arrays.toString(topics) +
                ", data=" + Arrays.toString(data) +
                ", height=" + height +
                ", blockTime=" + blockTime +
                '}';
    }
}
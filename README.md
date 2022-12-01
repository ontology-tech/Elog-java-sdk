# Elog-Java-SDK

Document for use Elog in java.

## 1. 使用流程


* 使用wallet address进行client的实例化，并且订阅合约与事件

```
ElogClient client = new ElogClient("http://00.00.00.00:0000", "0xBdDAd30010924B39c9528e1A6e11b25477C6BeBF", "amqp://xx:xxxx@00.00.00.00:0000");

client.uploadContract("bsc", "2.abi", "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E", ContractType.OTHER);

client.subscribeEvents("bsc", "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E", Arrays.asList("TransferSingle"));

client.unSubscribeEvents("eth", "0xdac17f958d2ee523a2206206994597c13d831ec7", Arrays.asList("Approve"));
```

* 定义processor处理收到的事件消息

> @Topic注解包含链简称和合约地址两个属性，代表此processor专门用于处理某个chain的某个合约下的所有订阅的事件

> processor需要实现IMessageProcessor接口，处理经过封装的EventLog对象

```
@Topic(chain = "bsc", address = "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E")
public class TestProcessor implements IMessageProcessor {
    @Override
    public void handle(EventLog message) {
        System.out.println(message);
    }
}
```


## 2. SDK functions detail

* 实例化ElogClient

> params
> 
> * address : elog server地址
> * did : 钱包地址
> * mqUri : 连接rabbit mq的uri

```
ElogClient client = new ElogClient(address, did, mqUri);

```
eg : 

```
ElogClient client = new ElogClient("http://43.134.29.209:31033", "", "amqp://diu:WBv0nkRrs5cIrKUv@43.134.29.209:30831");

```


* 订阅合约

> params

> * chain : 链简称
> * path : abi文件相对路径
> * address : 合约地址
> * contractType : 合约类型

```
client.uploadContract("bsc", "2.abi", "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E", ContractType.OTHER);

```

* 订阅事件

> params

> * chain : 链简称
> * address : 合约地址
> * eventsName : 事件列表

```
client.subscribeEvents("bsc", "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E", Arrays.asList("TransferSingle"));

```

* 取消订阅合约

> params

> * chain : 链简称
> * address : 合约地址

```
client.removeContract("bsc", "0x659C45B17f1769d1CeBc89c2AaF0e6cE4404Aa7E");

```

* 取消事件订阅

> params

> * chain : 链简称
> * address : 合约地址
> * eventsName : 事件列表

```
client.unSubscribeEvents("eth", "0xdac17f958d2ee523a2206206994597c13d831ec7", Arrays.asList("Approve"));

```
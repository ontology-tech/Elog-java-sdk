package io.ont.elog.common;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: ElogSDKException
 * @Date: 2022/11/1 下午5:14
 */
public class ElogSDKException extends Exception{

    public ElogSDKException(){
        super();
    }

    public ElogSDKException(String msg){
        super(msg);
    }
}
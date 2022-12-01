package io.ont.elog.common;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: io.ont.elog.common.Utils
 * @Date: 2022/11/1 下午3:50
 */
public class Utils {

    private static HttpClient httpClient;

    static {
        //HttpClient4.5版本后的参数设置
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        //客户端和服务器建立连接的timeout
        requestConfigBuilder.setConnectTimeout(30000);
        //从连接池获取连接的timeout
        requestConfigBuilder.setConnectionRequestTimeout(30000);
        //连接建立后，request没有回应的timeout。
        requestConfigBuilder.setSocketTimeout(60000);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
        //连接建立后，request没有回应的timeout
        clientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(60000).build());
        clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(100);
        httpClient = clientBuilder.setConnectionManager(cm).build();
    }

    public static String httpPost(String url, String reqBodyStr, Map<String, Object> headerMap) throws ElogSDKException {

        String responseStr = "";

        StringEntity stringEntity = new StringEntity(reqBodyStr, StandardCharsets.UTF_8);
        stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(stringEntity);
        //设置请求头
        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue().toString());
        }

        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            responseStr = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            throw new ElogSDKException("connect network failed.");
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            return responseStr;
        } else {
            throw new ElogSDKException(response.getStatusLine().getReasonPhrase()+ "message : "+ responseStr);
        }
    }


    public static String httpGet(String uri, Map<String, Object> paramMap, Map<String, Object> headerMap) throws ElogSDKException {

        String responseStr = "";

        CloseableHttpResponse response = null;
        URIBuilder uriBuilder = null;
        try {
            //拼完整的请求url
            uriBuilder = new URIBuilder(uri);
            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, Object> entry :
                    paramMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            uriBuilder.setParameters(params);

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            //设置请求头
            for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue().toString());
            }
            response = (CloseableHttpResponse) httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            responseStr = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            throw new ElogSDKException("connect network failed.");
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            return responseStr;
        } else {
            throw new ElogSDKException(response.getStatusLine().getReasonPhrase()+ "message : "+ responseStr);
        }
    }

    public static String httpPostByUrlEncoder(String url, Map<String, String> paramsMap, Map<String, Object> headerMap) throws ElogSDKException {
        String responseStr;

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = (CloseableHttpResponse) httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            responseStr = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            throw new ElogSDKException("connect network failed.");
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            return responseStr;
        } else {
            throw new ElogSDKException(response.getStatusLine().getReasonPhrase() + "message : "+ responseStr);
        }
    }
}
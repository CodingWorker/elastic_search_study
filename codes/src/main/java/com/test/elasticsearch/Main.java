package com.test.elasticsearch;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA
 * User:    DaiYan
 * Date:    2017/9/25
 * Project: elasticsearchdemo
 */
public class Main {
    private static Client client;
    public static void main(String[] args) throws Exception{
        connect();
        index();
    }

    private static void connect()throws Exception{
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        try {
            //创建client
             client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.17.50.239"), 9300));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void close(){
        //关闭client
        client.close();
    }

    private static void index()throws Exception{
//        GetResponse response = client.prepareGet("blog", "article", "1").execute().actionGet();
//        System.out.println(response.getSourceAsString());
        GetRequest getRequest=new GetRequest("test");
        IndexResponse response= client.prepareIndex("test","type-test",null)
                .setSource("{\"content\":\"hello\"}")
                .get();
        System.out.println(response.status());
    }
}

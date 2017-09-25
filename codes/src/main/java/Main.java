import org.elasticsearch.client.Client;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA
 * Project: elasticsearchdemo
 * User: DaiYan
 * Date: 2017/9/25
 */
public class Main {
    private static Client client;
    public static void main(String[] args) throws Exception{
        client=new PreBuiltTransportClient(null)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getLocalHost(),9200));
        index();
    }

    public static void index(){

    }

}

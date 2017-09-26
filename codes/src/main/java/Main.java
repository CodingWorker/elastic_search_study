
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.collect.ImmutableOpenIntMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkIndexByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA
 * Project: elasticsearchdemo
 * User: DaiYan
 * Date: 2017/9/25
 */
public class Main {
    private static Client client;
    private IndicesAdminClient indicesAdminClient =null;
    public static void main(String[] args) throws Exception{
        client=new PreBuiltTransportClient(null)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getLocalHost(),9200));
        IndicesAdminClient= client.admin().indices();
        index();
    }

    public static void index(){
        String json="";
        IndexResponse resp= client.prepareIndex("","")
                .setSource(json, XContentType.JSON)
                .get();//execute
        System.out.println(resp.status());//集群状态
        System.out.println(resp.getId());//索引id
    }

    public static void prepareIndex()throws Exception{
        IndexRequest indexRequest=new IndexRequest("index","type","id")
                .source("json");

        IndexResponse resp=client.index(indexRequest).get();
        System.out.println(resp.getId());
    }

    public void get(){
        GetResponse resp=client.prepareGet("index","type","id")
                .get();//execute
        System.out.println(resp.getSourceAsString());
    }

    public void delete(){
        DeleteResponse resp=client.prepareDelete("index","type","id")
                .get();//execute
        System.out.println(resp.getResult().getOp());
    }

    public void deleteByQuery(){
        BulkIndexByScrollResponse response =//批量结果
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchQuery("gender", "male"))//query
                        .source("persons")//index
                        .get();//execute the opt

        long deleted = response.getDeleted();//number deleted

    }

    public void deleteByQueryAsync(){
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("gender", "male"))//query
                .source("persons")//index
                .execute(new ActionListener<BulkByScrollResponse>() {//async,listener
                    @Override
                    public void onResponse(BulkByScrollResponse response) {
                        long deleted = response.getDeleted();//number of deleted documents
                    }
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the exception
                    }
                });
    }

    public void update()throws Exception{
        UpdateRequest updateRequest=new UpdateRequest();
        updateRequest.index("index");
        updateRequest.type("type");
        updateRequest.id("id");
        updateRequest.doc("");
        client.update(updateRequest).get();
    }

    public void prepareUpdate(){
        client.prepareUpdate("index","type","id")
                .setDoc("json")
                .get();
    }

    public void multiGet(){
        MultiGetResponse responses= client.prepareMultiGet()
                .add("index1","type1","id1")
                .add("index2","type2","id2","id","id3","id4")
                .add("index3","type3","id5")
                .get();

        for(MultiGetItemResponse resp:responses.getResponses()){
            System.out.println(resp.getResponse().isExists());
            System.out.println(resp.getResponse().getSourceAsString());
        }
    }

    public void bulkOot(){
        BulkRequestBuilder bulkRequestBuilder=client.prepareBulk();//也可以使用bulkRequest
        bulkRequestBuilder.add(client.prepareIndex("index","type","id")
        .setSource("json",XContentType.JSON)
        );

        bulkRequestBuilder.add(client.prepareDelete("index","type","id"));

        BulkResponse resp= bulkRequestBuilder.get();
        if(resp.hasFailures()){
            for(BulkItemResponse itemResponse :resp.getItems()){
                System.out.println(itemResponse.getFailure());
            }
        }
    }

    public void bulkProcessor()throws Exception{
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                client,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) { ... }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) { ... }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) { ... }
                })
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        bulkProcessor.add(new IndexRequest("twitter", "tweet", "1").source("json"));
        bulkProcessor.add(new DeleteRequest("twitter", "tweet", "2"));

        bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
        //or bulkProcessor.close();
    }

    public void createIndex(){
        CreateIndexResponse resp =indicesAdminClient.prepareCreate("newIndex")
                .setSettings(Settings.builder()
                .put("index.number_of_shards",3)
                .put("index.number_of_replicas",2)).get();

        System.out.println(resp.isAcknowledged());
    }

    public void createIndexAndType(){
        client.admin().indices().prepareCreate("twitter")
                .addMapping("tweet", "{\n" +
                        "    \"tweet\": {\n" +
                        "      \"properties\": {\n" +
                        "        \"message\": {\n" +
                        "          \"type\": \"string\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }")
                .get();
    }

    public void refeshIndex(){
        indicesAdminClient.prepareRefresh().get();//refresh all
        indicesAdminClient
                .prepareRefresh("twitter")
                .get();//refresh twitter
        indicesAdminClient
                .prepareRefresh("twitter", "company")
                .get();//refresh twitter and company ...

    }

    public void getSettings(){
        GetSettingsResponse resp=indicesAdminClient
                .prepareGetSettings("index","type")
                .get();

        for(ImmutableOpenIntMap setting:resp.getIndexToSettings()){

        }
    }




}

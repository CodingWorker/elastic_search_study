using System;
using System.Collections.Generic;
using Nest;
using System.Linq;

namespace ElasticSearchc.learn
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("waiting for debug...");
            Console.ReadKey();
            var client = new Nest.ElasticClient(GetSettings());

            int page = 1;
            int size = 10000;
            string type = null;
            string keywords = "预告";

            var searchReq = new SearchRequest("resource_1486640677,resource_1489560828", "Resource");
            //searchReq.From = (page - 1) * size;
            //searchReq.Size = size;

            #region Field
            //使用MatchQuery
            var matchQ = new MatchQuery();
            matchQ.Field = "Title";
            matchQ.Query = keywords;

            var matchQ2 = new MatchQuery();
            matchQ2.Field = "BusinessObjectId";
            matchQ2.Query = type;
            #endregion
            if (string.IsNullOrEmpty(keywords) || string.IsNullOrEmpty(type))
            {
                searchReq.Query = (matchQ || matchQ2);
            }
            else
            {
                searchReq.Query = (matchQ && matchQ2);
            }

            //结果集
            var r = client.Search<Resource>(searchReq);
            var dr = client.Search<dynamic>(searchReq);
            var doc = r.Documents;
            var total = r.Total;
            Console.WriteLine("Total: "+total);
            List<Resource> resList = Enumerable.ToList<Resource>(doc);

            foreach (var d in dr.Documents)
            {
                int a = 1;
            }
        
           
        }

        private static ConnectionSettings GetSettings()
        {
            //获取es的uri
            var esUris = "http://192.168.100.249:9200".Split(',');

            var nodes = new List<Uri>();
            foreach (var uri in esUris)
            {
                Uri node = new Uri(uri);
                nodes.Add(node);
            }
            var pool = new Elasticsearch.Net.StaticConnectionPool(nodes);

            ConnectionSettings settings = new ConnectionSettings(pool);

            var ts = DateTime.Now.AddSeconds(5) - DateTime.Now;
            settings.DeadTimeout(ts);
            settings.MaxDeadTimeout(ts);
            settings.MaxRetryTimeout(ts);
            settings.PingTimeout(ts);
            settings.RequestTimeout(ts);
            return settings;
        }

    }

    public class Resource
{
    /// <summary>
    /// Resource UUID
    /// </summary>
    public string UUID { get; set; }
    /// <summary>
    /// Resource 业务对象ID
    /// </summary>
    public string BusinessObjectId { get; set; }
    /// <summary>
    /// Resource Title
    /// </summary>
    public string Title { get; set; }
    /// <summary>
    /// Resource 资源路径
    /// </summary>
    public string SourceUrl { get; set; }
    /// <summary>
    /// Resource 创建时间
    /// </summary>
    public DateTime TimeCreated { get; set; }
    /// <summary>
    /// Resource 修改时间
    /// </summary>
    public DateTime TimeModified { get; set; }
    /// <summary>
    /// Resource 关键词
    /// </summary>
    public List<string> Keywords { get; set; }
    /// <summary>
    /// 资源图片
    /// </summary>
    public string Image { get; set; }
    /// <summary>
    /// 资源简介
    /// </summary>
    public string Desc { get; set; }
    /// <summary>
    /// 视频标签数组
    /// </summary>
    public List<string> TagArr { get; set; }
    /// <summary>
    /// 额外信息
    /// </summary>
    public Dictionary<string, string> AdditionalInformations { get; set; }
}
}

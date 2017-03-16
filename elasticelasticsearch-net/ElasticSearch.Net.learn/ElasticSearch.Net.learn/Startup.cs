using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(ElasticSearch.Net.learn.Startup))]
namespace ElasticSearch.Net.learn
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}

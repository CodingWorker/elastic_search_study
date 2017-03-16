using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(ElasticSearch.net_learn.Startup))]
namespace ElasticSearch.net_learn
{
    public partial class Startup {
        public void Configuration(IAppBuilder app) {
            ConfigureAuth(app);
        }
    }
}

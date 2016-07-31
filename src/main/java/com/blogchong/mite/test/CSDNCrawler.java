package com.blogchong.mite.test;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import com.blogchong.mite.save.mysql.InitMySQL;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Author: blogchong
 * Time:   2016/7/30
 * Email:  blogchong@qq.com
 * WeChat: mute88
 * Des:    测试-爬取CSDN博文
 */
public class CSDNCrawler extends BreadthCrawler {

    public static JdbcTemplate jdbcTemplate = null;

    public CSDNCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    /*
       可以往next中添加希望后续爬取的任务，任务可以是URL或者CrawlDatum
       爬虫不会重复爬取任务，从2.20版之后，爬虫根据CrawlDatum的key去重，而不是URL
       因此如果希望重复爬取某个URL，只要将CrawlDatum的key设置为一个历史中不存在的值即可
       例如增量爬取，可以使用 爬取时间+URL作为key。

       新版本中，可以直接通过 page.select(css选择器)方法来抽取网页中的信息，等价于
       page.getDoc().select(css选择器)方法，page.getDoc()获取到的是Jsoup中的
       Document对象，细节请参考Jsoup教程
   */
    @Override
    public void visit(Page page, CrawlDatums next) {
        if (page.matchUrl("http://blog.csdn.net/.*/article/details/.*")) {
            String url = page.getUrl();
            System.out.println("URL: " + url);
            String title = page.select("div[class=article_title]").first().text();
            String author = page.select("div[id=blog_userface]").first().text();

//
//            if (title != null && author != null && title.length() != 0 && author.length() != 0) {
//                int updates=jdbcTemplate.update("insert into mite_csdn"
//                                +" (title,author) value(?,?)",
//                        title, author);
//                if(updates==1){
//                    System.out.println("mysql插入成功: anthor:" + author + "\ttitle:" + title);
//                } else {
//                    System.out.println("mysql插入失败: anthor:" + author + "\ttitle:" + title);
//                }
//            }
        }
    }

    public static void main(String[] args) throws Exception {

        //初始化MySQL练级
        InitMySQL initMySQL = new InitMySQL();

          /*创建数据表*/
        initMySQL.getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS mite_csdn ("
                + "id int(11) NOT NULL AUTO_INCREMENT,"
                + "title varchar(200),author varchar(50),"
                + "PRIMARY KEY (id)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;");
        System.out.println("成功创建数据表 mite_csdn");

        jdbcTemplate = initMySQL.getJdbcTemplate();

        CSDNCrawler crawler = new CSDNCrawler("mite-csdn", true);
        crawler.addSeed("http://blog.csdn.net/.*");
        crawler.addRegex("http://blog.csdn.net/.*/article/details/.*");

        /*可以设置每个线程visit的间隔，这里是毫秒*/
//        crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
//        crawler.setRetryInterval(1000);

        crawler.setThreads(5);
        crawler.start(2);
    }
}

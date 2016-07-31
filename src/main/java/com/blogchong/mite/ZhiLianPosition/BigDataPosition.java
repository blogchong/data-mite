package com.blogchong.mite.ZhiLianPosition;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Author: blogchong
 * Time:   2016/7/30
 * Email:  blogchong@qq.com
 * WeChat: mute88
 * Des:    解析智联招聘大数据职位
 */
public class BigDataPosition extends BreadthCrawler {
    public static JdbcTemplate jdbcTemplate = null;

    public BigDataPosition(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {

        System.out.println("#### => URL: " + page.getUrl());

//        if (page.matchUrl("http://jobs.zhaopin.com/.*.htm\\?ssidkey=y&ss=201&ff=03")) {
//            String url = page.getUrl();
//            System.out.println("URL: " + url);
//
            Elements div = page.select("div[class=inner-left fl]");
        if (div.size() != 0) {
            //职位url
            String Url = page.getUrl();

            //职位名称
            String position = div.select("h1").first().text();

            //只过滤固定字符的岗位
            if (position.contains("大数据")) {
                //公司名称
                String companyName = div.select("h2").first().text();

                //公司福利
                Elements companyWelfareTmp = div.select("div[class=welfare-tab-box]").select("span");
                String companyWelfare = "";
                for (Element element : companyWelfareTmp) {
                    if (companyWelfare.length() == 0) {
                        companyWelfare = element.text();
                    } else {
                        companyWelfare = companyWelfare + "," + element.text();
                    }
                }

                //解析具体的工作要求
                Elements divDetailWord = page.select("div[class=terminalpage-left]").select("ul").select("li").select("strong");
                String workIncome = "";
                int workIncomAvg = 0;
                String workPlace = "";
                String workRelease = "";
                String workNatrue = "";
                String workExp = "";
                double workExpAvg = 0L;
                String workEdu = "";
                int workNums = 0;
                String workType = "";
                if (divDetailWord.size() >= 8) {
                    workIncome = divDetailWord.get(0).text();
                    //计算平均月薪需求
                    if (!workIncome.trim().equals("面议")) {
                        String[] workIncomeAvgTmp1 = workIncome.split("元/月");
                        if (workIncomeAvgTmp1.length == 1) {
                            String[] workIncomeAvgTmp2 = workIncomeAvgTmp1[0].split("-");
                            if (workIncomeAvgTmp2.length == 2) {
                                workIncomAvg = (Integer.parseInt(workIncomeAvgTmp2[0]) + Integer.parseInt(workIncomeAvgTmp2[1])) / 2;
                            }
                        }
                    }
                    workPlace = divDetailWord.get(1).text();
                    workRelease = divDetailWord.get(2).text();
                    workNatrue = divDetailWord.get(3).text();
                    workExp = divDetailWord.get(4).text();
                    //计算平均工作经验
                    if (!workExp.trim().equals("不限")) {
                        String[] workExpAvgTmp1 = workExp.split("年");
                        if (workExpAvgTmp1.length >= 1) {
                            String[] workExpAvgTmp2 = workExpAvgTmp1[0].split("-");
                            int count = 0;
                            for(String year: workExpAvgTmp2) {
                                workExpAvg = workExpAvg + Double.parseDouble(year);
                                count++;
                            }
                            workExpAvg = workExpAvg / (double)count;
                        }
                    }
                    workEdu = divDetailWord.get(5).text();
                    String workNumsTmp1 = divDetailWord.get(6).text();
                    String[] workNumsTmp2 = workNumsTmp1.split("人");
                    if (workNumsTmp2.length == 1) {
                        workNums = Integer.parseInt(workNumsTmp2[0]);
                    }
                    workType = divDetailWord.get(7).text();
                }

                //解析工作岗位详细描述以及公司介绍
                Elements divDetailDesc = page.select("div[class=tab-inner-cont]");
                String workDesc = "";
                String companyDesc = "";
                if (divDetailDesc.size() >= 2) {
                    workDesc = divDetailDesc.get(0).text();
                    companyDesc = divDetailDesc.get(1).text();
                }

                //解析公司的具体属性
                Elements divCompany = page.select("ul[class=terminal-ul clearfix terminal-company mt20").select("li");
                String companyScale = "";
                int companyScaleAvg = 0;
                String companyNatrue = "";
                String companyIndex = "";
                String companyIndustry = "";
                String companyPlace = "";
                for (Element element :divCompany) {
                    if (element.select("span").text().contains("公司规模")){
                        companyScale = element.select("strong").text();
                        String[] companyScaleAvgTmp1 = companyScale.split("人");
                        if (companyScaleAvgTmp1.length >= 1) {
                            int count = 0;
                            String[] companyScaleAvgTmp2 = companyScaleAvgTmp1[0].split("-");
                            for(String scale: companyScaleAvgTmp2) {
                                companyScaleAvg = companyScaleAvg + Integer.parseInt(scale);
                                count++;
                            }
                            companyScaleAvg = companyScaleAvg / count;
                        }
                    } else if (element.select("span").text().contains("公司性质")) {
                        companyNatrue = element.select("strong").text();
                    } else if (element.select("span").text().contains("公司行业")) {
                        companyIndustry = element.select("strong").text();
                    } else if (element.select("span").text().contains("公司主页")) {
                        companyIndex = element.select("strong").text();
                    } else if (element.select("span").text().contains("公司地址")) {
                        companyPlace = element.select("strong").text();
                    }
                }
                //补充入库操作
                System.out.println("OK");
            }
        }
//            String position = page.select("div[style=width: 224px;*width: 218px; _width:200px; float: left]").first().text();
//            String city = page.select("td[class=gzdd]").first().text();
//            String money = page.select("td[class=zwyx]").first().text();
//            String company = page.select("td[class=gsmc]").first().text();

//            System.out.println(position + "\t" + city + "\t" + money + "\t" + company);

//        }
    }

    public static void main(String[] args) throws Exception {

        //解析地址view-source:http://jobs.zhaopin.com/652425724250541.htm?ssidkey=y&ss=201&ff=03


//        //初始化MySQL练级
//        InitMySQL initMySQL = new InitMySQL();
//
//          /*创建数据表*/
//        initMySQL.getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS mite_zhilian ("
//                + "id int(11) NOT NULL AUTO_INCREMENT,"
//                + "url varchar(100),position varchar(50),position varchar(50),"
//                + "PRIMARY KEY (id)"
//                + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;");
//        System.out.println("成功创建数据表 mite_csdn");
//
//        jdbcTemplate = initMySQL.getJdbcTemplate();

        BigDataPosition crawler = new BigDataPosition("mite-zhilian", true);
//        bigDataPosition.addSeed("http://blog.csdn.net/.*");
//        bigDataPosition.addRegex("http://jobs.zhaopin.com/.*.htm\\?ssidkey=y&ss=201&ff=03");
        crawler.addSeed("http://sou.zhaopin.com/jobs/searchresult.ashx?jl=%E6%B7%B1%E5%9C%B3&kw=%E5%A4%A7%E6%95%B0%E6%8D%AE&p=1&kt=3&isadv=0");
        crawler.addRegex("http://jobs.zhaopin.com/.*.htm.*");

        /*可以设置每个线程visit的间隔，这里是毫秒*/
//        crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
//        crawler.setRetryInterval(1000);

        crawler.setThreads(5);
        crawler.start(3);
    }
}

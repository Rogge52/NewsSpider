package com.sqc.news.crawel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sqc.ip.ProxyCralwerUnusedVPN;
import com.sqc.news.db.DataUtils;
import com.sqc.news.db.NewsData;
import com.sqc.news.util.DataParse;

import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.RedisScheduler;

public class NewsCrawel implements PageProcessor,Job{
	
	public static String [] userAgent = {"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0",
			 "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36",
			 "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36",
			 "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0",
			 "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 UBrowser/6.2.4092.1 Safari/537.36",
			 "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0; UBrowser/6.2.4092.1) like Gecko",
			 "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A",
			 "Mozilla/5.0 (Windows; U; Windows NT 6.1; tr-TR) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27",
			 "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533+ (KHTML, like Gecko) Element Browser 5.0",
			 "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16",
			 "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",
			 "Mozilla/5.0 (Windows NT 6.0; rv:2.0) Gecko/20100101 Firefox/4.0 Opera 12.14"
			};
	public static File dir=new File("D://crawels");
	String title=null;
	
	public synchronized void process(Page page) {			
		//判断当前的page链接是否与正则匹配
		if (page.getUrl().regex("https://voice\\.hupu\\.com/nba/\\d+").match()) {		
			//当前页面下的新闻标题链接
			page.addTargetRequests(page.getHtml().xpath("//div[@class='list-hd']/h4/a").links().all());											
			if ((title=page.getHtml().xpath("//div[@class='artical-title']/h1/text()").get())!=null) {	
				synchronized(this){
					getNews(page);								
				}
			}
			//获取下一页
		}		
		findNextPage(page);
	}
	public synchronized void getNews(Page page) {
		synchronized(this){
			NewsData list =new NewsData();
		try {			
			//得到文章标题
			title=page.getHtml().xpath("//div[@class='artical-title']/h1/text()").get();
			//得到发布时间
			String publishtime=page.getHtml().xpath("//span[@id='pubtime_baidu']/text()").get();
			//转换时间格式
			Date date = DataParse.parseStringToData(publishtime);
			//得到作者名称
			String editor1=page.getHtml().xpath("//span[@id='editor_baidu']/text()").get();
			String editor = getEditor(editor1);
			//获取文章内容
			String content = getContent(page);
			//获取来源
			String source=page.getHtml().xpath("//span[@id='source_baidu']/a/text()").get();
			//将网页内容打印并保存到本地文件中
			Printtolocalfile(content);
			//获取当前时间
			Date currentTime = DataParse.getCurrentTime();	
			//方便持久化存储		
			List<NewsData> lists=new ArrayList<>();
			list.setTitle(title);
			list.setDate(date);
			list.setEditor(editor);
			list.setSource(source);
			list.setInserttime(currentTime);
			lists.add(list);
			DataUtils.persisit(lists);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}
	//打印到本地
	public void Printtolocalfile(String content) {
		FileWriter fw=null;
		File file=new File(dir, title+".txt");
		try {
			fw=new FileWriter(file);
			fw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			//关闭流
			if (fw!=null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	//获取作者的名称
	public  String getEditor(String editor) {
		String[] s1=editor.split("：");
		String[] s2 = s1[1].split("\\)");
		return s2[0];
	}
	//获取文章内容
	public  String  getContent(Page page) throws Exception {		
		String url=page.getUrl().get();
		Document doc=Jsoup.connect(url).timeout(10000).get();
		Elements elements = doc.select("div.artical-main-content p");
		StringBuffer buffer=new StringBuffer();
		for (Element element : elements) {
			buffer.append(element.text());
			buffer.append("\n");
		}
		return buffer.toString();	
	}
	//获得下一页的链接
	private void findNextPage(Page page) {
		 if (page.getHtml().xpath("//div[@class='list-hd']/h4/a").toString()!=null) {
			//下一页的链接
			page.addTargetRequests(page.getHtml().xpath("//a[@class='page-btn-prev']").links().all());
		}		
	}
	//设置参数
	public Site getSite() {
		String user_Agent = userAgent[(int) (Math.random()*userAgent.length)];
		return Site.me().setRetryTimes(5)
				.setTimeOut(10000)
				.setCharset("utf-8")
				.setUserAgent(user_Agent);
	}
//	public static void main(String[] args) {
//		if (!dir.exists()) {
//			dir.mkdirs();
//			
//		}
//		Spider.create(new NewsCrawel()).addUrl("https://voice.hupu.com/nba/1")
//		.thread(5).setScheduler(new PriorityScheduler()).run();
//	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (!dir.exists()) {
			dir.mkdirs();			
		}
		 //与本地的redis连接在一起
		RedisScheduler scheduler=new RedisScheduler(new JedisPool("127.0.0.1", 6379));
		
		 //获取代理ip
		ProxyCralwerUnusedVPN proxyCrawler = new ProxyCralwerUnusedVPN();
		//想要获取的代理IP个数，由需求方自行指定。（如果个数太多，将导致返回变慢）
        String ipresult =  proxyCrawler.startCrawler(2);
        if(!("").equals(ipresult)&&ipresult.length()>2){
        	String[] iphost = ipresult.split(",");
	     // 如果不设置，只要代理IP和代理端口正确  
	        System.getProperties().setProperty("http.proxyHost", iphost[0]);  
	        System.getProperties().setProperty("http.proxyPort", iphost[1]);  
        }else{
        	// 如果不设置，只要代理IP和代理端口正确  
	        System.getProperties().setProperty("http.proxyHost", "58.252.6.165");  
	        System.getProperties().setProperty("http.proxyPort", "9000");  
        }
		
		Spider.create(new NewsCrawel()).addUrl("https://voice.hupu.com/nba/1")
		.thread(5).
		setScheduler(scheduler)
		.run();
		
	}

}

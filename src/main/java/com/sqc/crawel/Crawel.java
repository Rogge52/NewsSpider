package com.sqc.crawel;

import java.util.ArrayList;
import java.util.List;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class Crawel implements PageProcessor{
	String time = null;
	String title=null;
	//爬取代码
	public void process(Page page) {	 //http://news.youth.cn/gn/index_1.htm   index_2.htm
		
		if (page.getUrl().regex("http://news\\.youth\\.cn/gn/index\\w\\d+\\.htm").match()) {
			//将当前页面的新闻链接加入到待抓取行列中
			page.addTargetRequests(page.getHtml().xpath("//ul[@class='tj3_1']/li/a").links().all());
			//将下一页的新闻链接加入到待抓取行列中
			List<String> requests=new ArrayList<String>();
				for (int i = 1; i < 10; i++) {
					String list="http://news.youth.cn/gn/"+"index_"+i+".htm";
					requests.add(list);
				}			
			page.addTargetRequests(requests);
		}
		else {	
			if ((title=page.getHtml().xpath("//div[@class='page_bt']/p/text()").get())!=null) {
				title=page.getHtml().xpath("//div[@class='page_bt']/p/text()").get();		
			}
			else {
				title=page.getHtml().xpath("//div[@class='page_title']/h1/text()").get();
			}
			System.out.println(title);
			
			if ((time=page.getHtml().xpath("//span[@id='page_right']/text()").get())!=null) {
				time = page.getHtml().xpath("//span[@id='page_right']/text()").get();          
			}else {
				time = page.getHtml().xpath("//p[@class='pwz']/text()").get();				  
			}
			System.out.println(time);
		}
			
	}			
	//设置参数
	public Site getSite() {		
		return Site.me().setRetryTimes(10)
				.setTimeOut(3000)
				.setCharset("gb2312")
				.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0");
	}
	public static void main(String[] args) {
		System.out.println("开始运行");
		Spider.create(new Crawel()).addUrl("http://news.youth.cn/gn/index_1.htm")
			  .thread(3).run();
	}
}

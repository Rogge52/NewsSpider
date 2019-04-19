package com.sqc.crawel.monitor;

import javax.management.JMException;

import com.sqc.news.crawel.NewsCrawel;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;

/**
 * 对爬虫的监视
 * @author shen2594698
 *
 */
public class MonitorExample {
	public static void main(String[] args) throws JMException {
		Spider oschinaSpider = Spider.create(new NewsCrawel())
                .addUrl("https://voice.hupu.com/nba/1");
		 SpiderMonitor.instance().register(oschinaSpider);
		 oschinaSpider.start();
	}
}

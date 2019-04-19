package com.sqc.timecontrol;
import static org.quartz.CronScheduleBuilder.cronSchedule; 
import static org.quartz.JobBuilder.newJob; 
import static org.quartz.TriggerBuilder.newTrigger; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import org.quartz.CronTrigger; 
import org.quartz.JobDetail; 
import org.quartz.Scheduler; 
import org.quartz.SchedulerFactory; 
import org.quartz.impl.StdSchedulerFactory;

import com.sqc.news.crawel.NewsCrawel; 

public class TimeUpdate { 
    public void go() throws Exception { 
        // 首先，必需要取得一个Scheduler的引用 
        SchedulerFactory sf = new StdSchedulerFactory(); 
        Scheduler sched = sf.getScheduler(); 
        //jobs可以在scheduled的sched.start()方法前被调用 
        //job 1将每隔20秒执行一次 
        JobDetail job = newJob(NewsCrawel.class).withIdentity("job1", "group1").build(); 
        // 0/20 * * * * ?
        CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1").withSchedule(cronSchedule("0/10 * * * * ?")).build(); 
        Date ft = sched.scheduleJob(job, trigger); 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS"); 
        System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression()); 
        sched.start(); 
    } 
    public static void main(String[] args) throws Exception { 
        TimeUpdate test = new TimeUpdate(); 
        test.go(); 
    } 

} 
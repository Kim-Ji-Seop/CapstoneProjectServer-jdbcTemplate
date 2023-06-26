package com.capston.bowler.src.batch.domain;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class HankerJobA extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(HankerJobA.class);

    private JobKey jobKey = null;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("작업 스케쥴러 A");
        jobKey = jobExecutionContext.getJobDetail().getKey();

        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        // 조건을 걸기위해 DateTime 설정
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date nowDate = new Date();
        try{
            Date date = sdf.parse("2021-04-12 12:12");
            String newDate = sdf.format(nowDate);

            nowDate = sdf.parse(newDate);

            if(nowDate.compareTo(date) == 0){
                scheduler.pauseJob(jobKey);
                log.info("작업이 종료되었습니다.");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
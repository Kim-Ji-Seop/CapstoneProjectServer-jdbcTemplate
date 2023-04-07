package com.example.demo.src.batch;

import com.example.demo.src.batch.domain.HankerJobA;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;


public class JobSetting {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void start(){
        JobDetail jobDetail = buildJobDetail(HankerJobA.class, new HashMap());

        try{
            scheduler.scheduleJob(jobDetail, buildJobTrigger("0/20 * * * * ?"));
        } catch(SchedulerException e){
            e.printStackTrace();
        }
    }

    public Trigger buildJobTrigger(String scheduleExp){
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
    }

    public JobDetail buildJobDetail(Class job, Map params){
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);

        return newJob(job).usingJobData(jobDataMap).build();
    }
}

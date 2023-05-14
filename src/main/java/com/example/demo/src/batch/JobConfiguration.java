package com.example.demo.src.batch;

import com.example.demo.src.batch.domain.TestBatch;
import com.example.demo.src.batch.repository.TestBatchDao;
import com.example.demo.src.domain.match.service.MatchService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    @Autowired
    private TestBatchDao testBatchDao;

    @Autowired
    private MatchService matchService;

    @Value("${spring.application.name}")
    private String instanceId;

    @Bean
    public Job testJob() {
        return jobBuilderFactory.get("job")
                .start(step1()) //STEP 1 실행
                    .on("FAILED")// STEP 1 결과가 FAILED 일 경우
                    .end()// 종료
                .from(step1())
                    .on("*")
                    .to(step2())
                .from(step2())
                    .on("*")
                    .to(step3())
                    .next(step4())
                    .on("*")
                    .end() // FLOW 종료
                .end() // JOB 종료
                .build();
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .tasklet((stepContribution, chunkContext) -> {

                    log.debug("======= 전송 배치 시작 =======");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet((stepContribution, chunkContext) -> {
                    log.debug("step2: ", "되는지 확인");
                    int itemCnt = testBatchDao.countAllByStatus("A");
                    log.debug("======= 처리할 Item 개수 : {}", itemCnt);
                    if(0 == itemCnt){
                        log.debug("======= 처리할 Item 이 없어 종료 ");
                        stepContribution.setExitStatus(ExitStatus.FAILED);
                    }
                    stepContribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step3(){
        return stepBuilderFactory.get("step3")
                .tasklet((stepContribution, chunkContext) -> {
                    log.debug("step3: ", "되는지 확인ㅇㅇㅇ");
                    List<TestBatch> testList = testBatchDao.findAllByStatus("A");

                    for(TestBatch testBatch: testList){
                        // 무언가를 처리하고 결과값이 TRUE 일때 결과 성공 처리
                        if(true){
                            testBatchDao.changeStatus(testBatch.getId());
                        }
                    }
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step4(){
        return stepBuilderFactory.get("step4")
                .tasklet((stepContribution, chunkContext) -> {
                    testBatchDao.changeAndInsert();
                    log.debug("======= 전송 배치 종료 =======");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job updatedMatchRoomStatusByGameTime(){
        return jobBuilderFactory.get("updatedMatchRoomStatusByGameTime")
                .start(deactivateMatchRoom()) //STEP 1 실행
                /*    .on("FAILED")// STEP 1 결과가 FAILED 일 경우
                    .to(deativateFailed())
                    .on("*")
                    .end()// 종료
                .from(step1())
                    .on("*")
                    .end()
                .end()*/
                .build();
    }

    @Bean
    public Step deactivateMatchRoom(){
        return stepBuilderFactory.get("deactivateMatchRoom")
                .tasklet((stepContribution, chunkContext) -> {
                    SimpleDateFormat dateF =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    log.info("deactiveMatchStart log={}", "\n\n<------ " +  dateF.format(new Date())+ "시간지난 오프라인 매칭방 비활성화 ------>");
                    List<Integer> result = matchService.deactivateMatch();
                    log.info("<------ 오프라인 매칭방 비활성화 결과 ------>");
                    log.info("deactiveResult log={}" , "unvalidMatchCount: " + result.get(0) + "\n" +
                            "updatedMatchCount : " + result.get(1) + "\n" +
                            "updatedHistoryCount : " + result.get(2));

                    stepContribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;

                }).build();
    }

    @Bean
    public Step deativateFailed(){
        return stepBuilderFactory.get("deativateFailed")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("Failed Occur log={} ", "<****** " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") + "매칭방 비활성화 실패 ******>");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}

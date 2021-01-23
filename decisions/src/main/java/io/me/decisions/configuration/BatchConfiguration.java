package io.me.decisions.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step startStep() {
        return stepBuilderFactory.get("startStep")
                .tasklet((contribution, ctx) -> {
                    System.out.println("This is the start tasklet");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    Step evenStep() {
        return stepBuilderFactory.get("evenStep")
                .tasklet((contribution, ctx) -> {
                    System.out.println("This is the even tasklet");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    Step oddStep() {
        return stepBuilderFactory.get("oddStep")
                .tasklet((contribution, ctx) -> {
                    System.out.println("This is the odd tasklet");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    JobExecutionDecider oddDecider() {
        return new OddDecider();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("Job")
                .start(startStep())
                .next(oddDecider())
                .from(oddDecider()).on("ODD").to(oddStep())
                .from(oddDecider()).on("EVEN").to(evenStep())
                .from(oddStep()).on("*").to(oddDecider())
                .end()
                .build();
    }

    public static class OddDecider implements JobExecutionDecider {
        private int count = 0;

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution,
                                          StepExecution stepExecution) {
            count++;

            if (count % 2 == 0) {
                return new FlowExecutionStatus("EVEN");
            }
            else {
                return new FlowExecutionStatus("ODD");
            }
        }
    }

}

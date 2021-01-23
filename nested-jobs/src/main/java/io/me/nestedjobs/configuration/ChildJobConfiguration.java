package io.me.nestedjobs.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChildJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    public ChildJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step step1a() {
        return stepBuilderFactory.get("step1a")
                .tasklet((contrib, ctx) -> {
                    System.out.println(">> This is step 1a");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    Job childJob() {
        return jobBuilderFactory.get("childJob")
                .start(step1a())
                .build();
    }
}

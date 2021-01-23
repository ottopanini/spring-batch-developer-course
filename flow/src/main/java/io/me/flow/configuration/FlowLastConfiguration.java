package io.me.flow.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowLastConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public FlowLastConfiguration(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step myLastStep() {
        return stepBuilderFactory.get("myStep")
                .tasklet((contrib, ctx) -> {
                    System.out.println("my last step was executed");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    Job flowLastJob(Flow flow) {
        return jobBuilderFactory.get("flowLastJob")
                .start(myLastStep())
                .on("COMPLETED").to(flow)
                .end()
                .build();
    }

}

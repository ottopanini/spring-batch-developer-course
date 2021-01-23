package io.memyselfandi.transitions.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepTransitionConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public StepTransitionConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, ctxt) -> {
                    System.out.println(">> this is step 1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, ctxt) -> {
                    System.out.println(">> this is step 2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet((contribution, ctxt) -> {
                    System.out.println(">> this is step 3");
                    return RepeatStatus.FINISHED;
                }).build();
    }
    @Bean
    Job job() {
       return jobBuilderFactory.get("job")
               .start(step1())
               .on("COMPLETED").to(step2())
               .from(step2()).on("COMPLETED").fail()
               .from(step3()).end()
               .build();
    }
}

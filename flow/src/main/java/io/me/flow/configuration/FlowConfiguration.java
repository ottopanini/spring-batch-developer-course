package io.me.flow.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowConfiguration {
    private final StepBuilderFactory stepBuilderFactory;

    public FlowConfiguration(StepBuilderFactory stepBuilderFactory) {
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
    Flow flow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("foo");
        flowBuilder.start(step1())
                .next(step2())
                .end();
        return flowBuilder.build();
    }
}

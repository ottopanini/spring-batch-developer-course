package io.me.restart.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public JobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public Tasklet restartTasklet() {
        return (stepContribution, chunkContext) -> {
            Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();
            if (stepExecutionContext.containsKey("ran")) {
                System.out.println("This time we'll let it go");
                return RepeatStatus.FINISHED;
            }
            else {
                System.out.println("Don't think so...");
                chunkContext.getStepContext().getStepExecution().getExecutionContext().put("ran", true);

                throw new RuntimeException("Not this time...");
            }
        };
    }

    @Bean
    Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(restartTasklet())
                .build();
    }

    @Bean
    Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(restartTasklet())
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step1())
                .next(step2())
                .build();
    }
}

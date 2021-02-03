package io.me.skipretrylisteners.config;

import io.me.skipretrylisteners.components.CustomException;
import io.me.skipretrylisteners.components.CustomSkipListener;
import io.me.skipretrylisteners.components.SkipItemProcessor;
import io.me.skipretrylisteners.components.SkipItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
    public ListItemReader<String> reader() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(String.valueOf(i));
        }

        ListItemReader<String> reader = new ListItemReader<>(items);
        return reader;
    }

    @Bean
    @StepScope
    public SkipItemProcessor processor() {
        return new SkipItemProcessor();
    }

    @Bean
    @StepScope
    public SkipItemWriter writer() {
        return new SkipItemWriter();
    }

    @Bean
    Step step1() {
        return stepBuilderFactory.get("step1003")
                .<String, String>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skip(CustomException.class)
                .skipLimit(15)
                .listener(new CustomSkipListener())
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step1())
                .build();
    }

}

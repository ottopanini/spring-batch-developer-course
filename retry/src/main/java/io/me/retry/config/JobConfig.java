package io.me.retry.config;

import io.me.retry.components.CustomRetryableException;
import io.me.retry.components.RetryItemProcessor;
import io.me.retry.components.RetryItemWriter;
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
    public RetryItemProcessor processor(@Value("#{jobParameters['retry']}") String retry) {
        RetryItemProcessor processor = new RetryItemProcessor();
        processor.setRetry(StringUtils.hasText(retry) && retry.equalsIgnoreCase("processor"));
        return processor;
    }

    @Bean
    @StepScope
    public RetryItemWriter writer(@Value("#{jobParameters['retry']}") String retry) {
        RetryItemWriter writer = new RetryItemWriter();
        writer.setRetry(StringUtils.hasText(retry) && retry.equalsIgnoreCase("processor"));
        return writer;
    }

    @Bean
    Step step1() {
        return stepBuilderFactory.get("step1001")
                .<String, String>chunk(10)
                .reader(reader())
                .processor(processor(null))
                .writer(writer(null))
                .faultTolerant()
                .retry(CustomRetryableException.class)
                .retryLimit(15)
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step1())
                .build();
    }

}

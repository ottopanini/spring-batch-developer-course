package io.me.skip.config;

import io.me.skip.components.CustomRetryableException;
import io.me.skip.components.SkipItemProcessor;
import io.me.skip.components.SkipItemWriter;
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
    public SkipItemProcessor processor(@Value("#{jobParameters['skip']}") String retry) {
        SkipItemProcessor processor = new SkipItemProcessor();
        processor.setSkip(StringUtils.hasText(retry) && retry.equalsIgnoreCase("processor"));
        return processor;
    }

    @Bean
    @StepScope
    public SkipItemWriter writer(@Value("#{jobParameters['skip']}") String skip) {
        SkipItemWriter writer = new SkipItemWriter();
        writer.setSkip(StringUtils.hasText(skip) && skip.equalsIgnoreCase("writer"));
        return writer;
    }

    @Bean
    Step step1() {
        return stepBuilderFactory.get("step1002")
                .<String, String>chunk(10)
                .reader(reader())
                .processor(processor(null))
                .writer(writer(null))
                .faultTolerant()
                .skip(CustomRetryableException.class)
                .skipLimit(15)
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step1())
                .build();
    }



}

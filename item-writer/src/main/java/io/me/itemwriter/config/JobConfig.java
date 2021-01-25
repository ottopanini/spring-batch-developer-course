package io.me.itemwriter.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    ListItemReader<String> itemReader() {
        List<String> items = new ArrayList<>(100);

        for (int i = 0; i < 100; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    ItemWriter<String> itemWriter() {
        return new SysOutItemWriter();
    }

    @Bean
    Step step() {
        return stepBuilderFactory.get("step")
                .<String, String>chunk(10)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

}

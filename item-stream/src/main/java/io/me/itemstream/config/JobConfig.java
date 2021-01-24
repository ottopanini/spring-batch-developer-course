package io.me.itemstream.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
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
    @StepScope
    StatefulItemReader itemReader() {
        List<String> items = new ArrayList<>(100);

        for (int i = 0; i < 100; i++) {
            items.add(String.valueOf(i));
        }

        return new StatefulItemReader(items);
    }

    @Bean
    ItemWriter<String> itemWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(">> " + item);
            }
        };
    }

    @Bean
    Step step() {
        return stepBuilderFactory.get("step6")
                .<String, String>chunk(10)
                .reader(itemReader())
                .writer(itemWriter())
                .stream(itemReader())
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

}

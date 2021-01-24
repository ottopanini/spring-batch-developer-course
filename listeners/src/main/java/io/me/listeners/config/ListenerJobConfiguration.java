package io.me.listeners.config;

import io.me.listeners.listener.ChunkListener;
import io.me.listeners.listener.Joblistener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ListenerJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    public ListenerJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    ItemReader<String> reader() {
        return new ListItemReader<>(Arrays.asList("one", "two", "three"));
    }

    @Bean
    ItemWriter<String> writer() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                for (String item : items) {
                    System.out.println("Writing item " + item);
                }
            }
        };
    }

    @Bean
    Step step() {
        return stepBuilderFactory.get("step")
                .<String, String>chunk(2)
                .faultTolerant()
                .listener(new ChunkListener())
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    Job job(JavaMailSender javaMailSender) {
        return jobBuilderFactory.get("job")
                .start(step())
                .listener(new Joblistener(javaMailSender))
                .build();
    }
}
